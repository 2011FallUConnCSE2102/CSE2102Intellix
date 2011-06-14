
package com.hermix;

/**
 * Title:
 * Description: Clasa care se ocua cu 'traficul' frema-uri audio/video.
 *      Aceste frame-uri sunt uploadate pe un thread diferit de cel al HermixApi
 *      Frame-rile primte sunt despatch-uite la cache-ul audio/video in functie
 *              de tipul lor si de utilizatorul care le-a trimis!
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.04
 */

import com.hermix.event.hAudioListener;
import com.hermix.event.hVideoListener;
import ro.integrasoft.chat.message.Statistics;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


public class VideoThread extends Thread {
	private String password;
	private String host;
	private Socket s;

	private HermixApi parent;
	private Statistics statistics = null;

	public static final int FR_V_JPG = 0x00000000;
	public static final int FR_A_GSM = 0x80000000;
	public static final int FR_A_PCM_MONO_16b_8KHz = 0x80000001;
	public static final int FRAME_SIZE = 36;

	private Hashtable videocache;
	private AudioCache audiocache;

	private boolean stopp;

	public VideoThread(HermixApi parent, String host, String password) {
		super(parent.getThreadGroup(), "Video thread");
		videocache = new Hashtable();
		audiocache = new AudioCache("audioCache", parent);
		this.host = host;
		this.password = password;
		this.parent = parent;
		statistics = parent.getStatistics();
		parent.i_log("[VideoThread] Starting...");
		stopp = false;
	}

	//add video listener
	public void add_video_listener(String nick, hVideoListener vl) {
		parent.i_log("[videothread] addvideolistener for nick " + nick);
		synchronized (videocache) {
			if (videocache.get(nick) == null) {
				VideoCache vc = new VideoCache(nick, parent);
				vc.add_video_listener(vl);
				videocache.put(nick, vc);
			} else {
				VideoCache vc = (VideoCache) videocache.get(nick);
				vc.add_video_listener(vl);
			}
		}
	}

	//add audio listener
	public void add_audio_listener(String nick, hAudioListener vl) {
		parent.i_log("[videothread] addaudiolistener for nick " + nick);
		synchronized (audiocache) {
			audiocache.add_audio_listener(vl);
		}
	}

	//remove video listener
	public void remove_video_listener(String nick, hVideoListener vl) {
		if (videocache != null)
			synchronized (videocache) {
				VideoCache vc = (VideoCache) videocache.get(nick);
				if (vc == null)
					return;
				vc.remove_video_listener(vl);
				//stop videocache thread first:
				vc.stopThread();
				//then remove object from collection
				videocache.remove(nick);
			}
	}

	//remove audio listener
	public void remove_audio_listener(String nick, hAudioListener vl) {
		if (audiocache != null)
			synchronized (audiocache) {
				audiocache.remove_audio_listener(vl);
			}
	}

	//turn on video (starts the thread)
	public void videoOn() {
		parent.i_log("[VideoThread] VideoOn()");
		start();
	}

	//video connection and dispatch stopped
	public synchronized void videoOff() {
		try {
			s.close();
		} catch (Exception ex) {
		}
		stopThread();
	}

	//writes a video frame to socket
	//returns true if operation succeded
	public boolean s_send_video_frame(VideoFrame vf) {
		if (vf == null)
			System.out.println("vf is null");
		byte[] frame = new byte[VideoThread.FRAME_SIZE];
		DataOutputStream dos = null;
		try {
			dos = new DataOutputStream(s.getOutputStream());
		} catch (Exception ex) {
			//error, unable to create output stream
			return false;
		}

		putInt(vf.type, frame, 0);
		putInt(vf.timestamp, frame, 4);
		putInt(vf.framesize, frame, 8);
		putInt(vf.id, frame, 12);
		System.arraycopy(vf.user, 0, frame, 16, vf.user.length);
		write(dos, frame, VideoThread.FRAME_SIZE);
		write(dos, vf.data, vf.framesize);
		if (vf.type == FR_V_JPG) {
			statistics.add(FRAME_SIZE + vf.framesize, Statistics.UP_VIDEO);
		} else if (vf.type == FR_A_GSM || vf.type == FR_A_PCM_MONO_16b_8KHz) {
			statistics.add(FRAME_SIZE + vf.framesize, Statistics.UP_AUDIO);
		} else
			statistics.add(FRAME_SIZE + vf.framesize, Statistics.UP_SYSTEM);
		try {
			dos.flush();
		} catch (Exception ex) {
			return false;
			//error, error sending data
		}
		return true;
	}

	//utilities
	private int getInt(byte[] bytes, int index) {
		int d0 = getVal(bytes[index]);
		int d1 = getVal(bytes[index + 1]);
		int d2 = getVal(bytes[index + 2]);
		int d3 = getVal(bytes[index + 3]);
		return (int) ((long) d0 + (long) d1 * 256 + (long) d2 * 256 * 256 + (long) d3 * 256 * 256 * 256);
	}

	private void putInt(int val, byte[] bytes, int index) {
		bytes[index] = (byte) (val & 0x000000ff);
		bytes[index + 1] = (byte) ((val & 0x0000ff00) >> 8);
		bytes[index + 2] = (byte) ((val & 0x00ff0000) >> 16);
		bytes[index + 3] = (byte) ((val & 0xff000000) >> 24);
	}

	private int getVal(byte bb) {
		int b = bb;
		if (b >= 0) {
			return b;
		}
		b++;
		b = 127 + b;
		b += 128;
		return b;
	}

	void read(byte b[], int nr) throws IOException {
		int len = 0;
		while (len < nr) {
			len += s.getInputStream().read(b, len, nr - len);
		}
	}

	private void write(DataOutputStream dos, byte[] b, int size) {
		int c = 0;
		try {
			dos.write(b, c, size - c);
		} catch (Exception exx) {
		}
	}

	public boolean stopped() {
		return stopp;
	}

	public void stopThread() {
		stopp = true;
		stop();
		stopAllListeners();
	}


	//video connection and dispatch start
	public void run() {
		parent.i_log("[VideoThread] Connecting to " + host);
		try {
			s = new Socket(host, parent.getServerPort() + 2);
			s.getOutputStream().write(password.getBytes(), 0, 10);
			statistics.add(password.length(), Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			parent.i_log("[VideoThread] Error opening video connection");
			stopp = true;
			return;
		}
		parent.i_log("[VideoThread] Connected");
		stopp = false;
		while (!stopp) {
			byte[] frame = new byte[FRAME_SIZE];
			byte[] video_frame = null;
			VideoFrame vf = new VideoFrame();
			vf.user = new byte[20];
			stopp = false;
			try {
				read(frame, FRAME_SIZE);
				//construct the frame object, from the frame header;
				vf.type = getInt(frame, 0);
				vf.timestamp = getInt(frame, 4);
				vf.framesize = getInt(frame, 8);
				vf.id = getInt(frame, 12);
				//System.out.println("Video Timestamp is :"+vf.timestamp );
//                vf.user = new byte[20];
				vf.nick = "";
				boolean b = false;
				for (int i = 0; i < 20; i++) {
					if (b) {
//                        vf.user[i]=0;
//                        continue;
						break;
					}

					if (frame[16 + i] != 0) {
						vf.nick = vf.nick + (char) frame[i + 16];
//                        vf.user[i]=frame[16+i];
					} else
						b = true;
				}//for

				vf.data = new byte[vf.framesize];
				read(vf.data, vf.framesize);		//read frame data;
				if (vf.type == FR_V_JPG) {//daca e video pune in videocache
					statistics.add(FRAME_SIZE + vf.framesize, Statistics.DOWN_VIDEO);
					dispatchFrameVideo(vf);
				} else if (vf.type == FR_A_GSM || vf.type == FR_A_PCM_MONO_16b_8KHz) {//daca e audio pune in audiocache
					statistics.add(FRAME_SIZE + vf.framesize, Statistics.DOWN_AUDIO);
					dispatchFrameAudio(vf);
				} else
					statistics.add(FRAME_SIZE + vf.framesize, Statistics.DOWN_SYSTEM);
			} catch (Exception ex) {
				parent.i_log("[VideoThread] Socket read exception");
				ex.printStackTrace();
				stopp = true;
				break;
			}
		}
		parent.i_log("[VideoThread] stop...");
		stopAllListeners();
		System.out.println("[VideoThread] end...");
	}

	private void stopAllListeners() {
		if (videocache != null) {
			Enumeration en = (videocache.elements());
			while (en.hasMoreElements()) {
				VideoCache vc = (VideoCache) en.nextElement();
				vc.stopThread();
				vc = null;
			}
		}
		videocache = null;
		if (audiocache != null)
			audiocache.stopThread();
		audiocache = null;
	}

	private void dispatchFrameVideo(VideoFrame vf) {
		synchronized (videocache) {
			VideoCache vc = (VideoCache) videocache.get(vf.nick);
			if (vc != null) {
				vc.add_video_frame(vf);
			} else {
				parent.iadd_video_user(vf.nick);
			}
		}
	}

	private void dispatchFrameAudio(VideoFrame vf) {
		AudioFrame af = new AudioFrame();

		af.data = vf.data;
		af.framesize = vf.framesize;
		af.id = vf.id;
		af.nick = vf.nick;
		af.timestamp = vf.timestamp;
		af.type = vf.type;
//        af.user = vf.user;
		synchronized (audiocache) {
			audiocache.add_audio_frame(af);
		}
		//daca nick transmite doar sunet
		//il adaug la video useri
		synchronized (videocache) {
			VideoCache vc = (VideoCache) videocache.get(vf.nick);
			if (vc == null) {
				parent.iadd_video_user(vf.nick);
			}
		}

	}

	/**
	 * By Qurtach:
	 * Tre sa modificam si in hashtable cheia dupa care memorez videocache-urile
	 */
	public void changeNick(String newNick, String oldNick) {
		VideoCache vc2save = (VideoCache) videocache.get(oldNick);
		if (vc2save == null)
			return;
		//stergem vechea inregistrare:
		videocache.remove(oldNick);
		//si o inlocuim cu una cu noul nick:
		videocache.put(newNick, vc2save);
		/*for debug only (Qurtach):
        System.out.println("VideoThread new userList:");
        Enumeration en = videocache.keys();
        while (en.hasMoreElements())
            System.out.print(en.nextElement()+" ");
        System.out.println();*/
	}

	public Vector getAudioListeners() {
		return audiocache.getListeners();
	}

}