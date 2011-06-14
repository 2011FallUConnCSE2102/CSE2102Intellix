
package com.hermix;

/**
 * Title:
 * Description: Thread care imparte framurile audio la listenerii specificati.
 *          Exista un singur thread de acest fel :( ????
 *          Este creat o data cu thread-ul video si traieste la fel de mult cu acesta
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nantu Isac Cristian
 * @version 1.0
 */

import com.hermix.event.hAudioListener;

import java.util.Vector;

public class AudioCache extends Thread {
	private Vector listeners;
	private HermixApi parent;
	private Vector cache;
	private String name;
	private boolean stopp = false;

	public AudioCache(String name, HermixApi parent) {
		super(parent.getThreadGroup(), "AudioCache " + name);
		this.setPriority(Thread.NORM_PRIORITY + 1);
		this.name = name;
		this.parent = parent;
		listeners = new Vector();
		cache = new Vector();
		start();
	}

	public void add_audio_listener(hAudioListener al) {
		synchronized (listeners) {
			if (!listeners.contains(al)){
				listeners.addElement(al);
			}
		}
	}

	public void remove_audio_listener(hAudioListener al) {
		synchronized (listeners) {
			if (listeners.contains(al))
				listeners.removeElement(al);
		}
	}

	public void add_audio_frame(AudioFrame af) {
		synchronized (cache) {
			cache.addElement(af);
		}
	}

	public void run() {
		while (!stopp) {
			//wait for at least 1 audioframe
			while (cache.size() < 1) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException ex) {
				}
				if (stopp)
					return;
			}
			AudioFrame af = null;
			//get the first audioframe
			synchronized (cache) {
				af = (AudioFrame) cache.elementAt(0);
				cache.removeElementAt(0);
			}

			//dispatch it to listeners;
			synchronized (listeners) {
				for (int i = 0; i < listeners.size(); i++) {
					hAudioListener al = (hAudioListener) listeners.elementAt(i);
					al.play_audio_frame(af);
				}
			}
		}
	}

	public void stopThread() {
		stopp = true;
	}

	public Vector getListeners() {
		return listeners;
	}
}