/**
 * Last update by Maxiniuc Ovidiu
 * clasa similara cu Videothread-ul, momentan nefolosita!
 */

package com.hermix;

import ro.integrasoft.chat.message.Statistics;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Hermix classes
 */
public class AudioThread extends Thread {
	public static final int SERVER_DEFAULT_PORT = 1;

	public static final int FRAME_SIZE = 12;

	private Socket server_socket = null;
	private String host_name;
	private InputStream videoStream;
	private HermixApi parent;
	private boolean broadcast;
	private boolean stopped;
	private DataOutputStream dos;
	private Statistics statistics = null;

	//start audio for nonbroadcast.
	public AudioThread(HermixApi parent, String host, String password) {
		super(parent.getThreadGroup(), "Audio Thread");
		this.parent = parent;
		statistics = parent.getStatistics();
		this.host_name = host;
		dos = null;
		broadcast = false;
		parent.i_log("AudioThread instantiated.");
		broadcast = false;
		try {
			sleep(1000);
		} catch (Exception ex) {
		}

		parent.i_log("AudioThread Host is " + host + " pass is " + password);
		try {
			server_socket = new Socket(host_name, parent.getServerPort() + SERVER_DEFAULT_PORT);
			server_socket.getOutputStream().write(password.getBytes());
		} catch (Exception e) {
			parent.i_log("AudioThread error " + e.getMessage());
			server_socket = null;
		}
		parent.i_log("AudioThread Connected to video mixer.");
		start();
	}

	//starts an audio player for broadcast clients
	public AudioThread(HermixApi parent) {
		broadcast = true;
	}

	public boolean isConnected() {
		return (server_socket != null);
	}

	public void run() {
		byte[] frame = new byte[FRAME_SIZE];
		if (broadcast)
			return;
		stopped = false;
		while (!stopped) {
			AudioFrame vf = new AudioFrame();
			try {
				read(frame, FRAME_SIZE);
				vf.type = getVal(frame[0]);
				vf.packet = getVal(frame[1]);
				vf.length = getInt(frame, 4);
				vf.timestamp = getInt(frame, 8);
				if (vf.type == 1) {
					//read frame data;
					vf.data = new byte[vf.length];
					read(vf.data, vf.length);
					//System.out.println("dispatch Audio Frame NOW!!!!!!!");
					parent.dispatchAudioFrame(vf);
					statistics.add(FRAME_SIZE + vf.length, Statistics.DOWN_AUDIO);
					continue;
				}
				String name = "";
				if (vf.type == 2) { //user timestamp delay , from server
					vf.data = new byte[vf.length];
					read(vf.data, vf.length);
					statistics.add(FRAME_SIZE + vf.length, Statistics.DOWN_AUDIO);
					continue;
				}
			} catch (Exception e) {
				parent.i_log("AudioThread connection lost");
				parent.r_audio_connection_lost();
				break;
			}
		}
		try {
			server_socket.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void audioOff() {
		stopped = true;
	}

	private int getInt(byte[] bytes, int index) {
		int d0 = getVal(bytes[index]);
		int d1 = getVal(bytes[index + 1]);
		int d2 = getVal(bytes[index + 2]);
		int d3 = getVal(bytes[index + 3]);
		return (int) ((long) d0 + (long) d1 * 256 + (long) d2 * 256 * 256 + (long) d3 * 256 * 256 * 256);
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
			len = len + server_socket.getInputStream().read(b, len, nr - len);
		}
	}

	public void send_audio_frame(AudioFrame af) {
		byte[] frame = new byte[AudioThread.FRAME_SIZE];
		if (dos == null) {
			try {
				dos = new DataOutputStream(server_socket.getOutputStream());
			} catch (Exception ex) {
			}
		}
		frame[0] = getVal(af.type);
		frame[1] = getVal(af.packet);
		putInt(af.length, frame, 4);
		putInt(af.timestamp, frame, 8);
		write(frame, AudioThread.FRAME_SIZE);
		write(af.data, af.length);
		statistics.add(FRAME_SIZE + af.length, Statistics.UP_AUDIO);

	}

	private byte getVal(int val) {
		if (val < 128)
			return (byte) val;
		val = val - 128;
		val = val - 127;
		val = val - 1;
		return (byte) val;
	}

	//puts an int to a byteArray
	private void putInt(int val, byte[] bytes, int index) {
		bytes[index] = (byte) (val & 0x000000ff);
		bytes[index + 1] = (byte) ((val & 0x0000ff00) >> 8);
		bytes[index + 2] = (byte) ((val & 0x00ff0000) >> 16);
		bytes[index + 3] = (byte) ((val & 0xff000000) >> 24);
	}


	//writes a byte array to output Stream.
	private void write(byte[] b, int size) {
		int c = 0;
		try {
			dos.write(b, c, size - c);
		} catch (Exception exx) {
			System.out.println(exx);
		}
	}
}