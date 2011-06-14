
package com.hermix;

/**
 * Clasa care distribuie frame-urile video pentru un anumit user.
 * Pentru fiecaer user ar trebui sa existe un asemenea thread.
 * Fiecare frame primit de thread, acesata este trimisa sa fie 'afisata '
 * catre un hVideoListener. Este responsabilitatea utilizatorului sa
 * implementeze interfata mai sus amintita!
 */

import com.hermix.event.hVideoListener;

import java.util.Vector;

public class VideoCache extends Thread {
	private Vector listeners;
	private HermixApi parent;
	private Vector cache;
	private String name;
	private boolean stopp = false;

	public VideoCache(String name, HermixApi parent) {
		super(parent.getThreadGroup(), "VideoCache " + name);
		this.name = name;
		this.parent = parent;
		listeners = new Vector();
		cache = new Vector();
		start();
	}

	public void add_video_listener(hVideoListener vl) {
		synchronized (listeners) {
			listeners.addElement(vl);
		}
	}

	public void remove_video_listener(hVideoListener vl) {
		synchronized (listeners) {
			if (listeners.contains(vl))
				listeners.removeElement(vl);
		}
	}

	public void add_video_frame(VideoFrame vf) {
		synchronized (cache) {
			if (cache.size() < 30)
				cache.addElement(vf);
			//else drop frame = pastram max 30 frame-uri in cache
		}
	}

	public void run() {
		while (!stopp) {
			//wait for at least 1 videoframe
			while (cache.size() < 1) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex) {
				}
				if (stopp)
					return;
			}
			VideoFrame vf = null;
			//get the first videoframe
			synchronized (cache) {
				vf = (VideoFrame) cache.elementAt(0);
				cache.removeElementAt(0);
			}

			//audio/video sinchronization
			if (audio_video_syncronize(vf) < 0)
				continue;

			//dispatch it to listeners;
			synchronized (listeners) {
				for (int i = 0; i < listeners.size(); i++) {
					hVideoListener vl = (hVideoListener) listeners.elementAt(i);
					vl.play_video_frame(vf);
				}
			}
		}
	}

	public void stopThread() {
		stopp = true;
	}


	/**
	 * Wait a number of miliseconds for audio. (if this frame is delayed, then just set
	 * it's value to null and return -1;
	 */
	public int audio_video_syncronize(VideoFrame vf) {
		//delay for audio/video sincronization... not implemented yet.
		return 0;
	}

}