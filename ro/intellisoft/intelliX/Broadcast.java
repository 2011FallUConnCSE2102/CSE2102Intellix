/**
 * User: Administrator
 * @Author: qurtach@intellisoft.ro
 * @Date: May 31, 2002
 * @Time: 7:15:37 PM
 */

package ro.intellisoft.intelliX;

import com.hermix.VideoFrame;
import com.hermix.VideoThread;

import javax.sound.sampled.*;
import javax.swing.*;

public class Broadcast implements Runnable {
	/**line from microfon or other*/
	TargetDataLine target_line = null;

	/**stop condition*/
	private boolean stopFlag = false;

	/**error condition*/
	private int errorFound = 0;

	/**the link withj the Hermix API*/
	HermixLink hermixLink = null;

	private static final int BUFFR_SIZE = 8000;

	public Broadcast(HermixLink hermixLink) {
		this.hermixLink = hermixLink;
		AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, false);
		DataLine.Info target_info = new DataLine.Info(TargetDataLine.class, format);
		if (!AudioSystem.isLineSupported(target_info)) {
			System.out.println("Can't get a target data line with format: " + format.toString());
			errorFound = -1;
			return;
		}
		try {
			target_line = (TargetDataLine) AudioSystem.getLine(target_info);
			target_line.open(format, 24000); //one second and a half buffer
		} catch (LineUnavailableException e) {
			System.out.println("Start capture exception: " + e.getMessage());
			target_line = null;
			errorFound = -2;
			return;
		}
		target_line.start();
	}

	public void start() {
		if (errorFound == 0){
			stopFlag = false;
			new Thread(this).start();
		} else {
			//some error(s) occured
			JOptionPane.showMessageDialog(hermixLink.getIDE().getMainFrame(), "Can't get a target data line.\nBroadcast disabled.","Error starting broadcast engine", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void run() {
		System.out.println("Broadcast.run");
		byte[] buffer = new byte[BUFFR_SIZE];  //half a second
		VideoFrame af = null;
		while (!stopFlag) {
			try {
				Thread.sleep(100);//some time
			} catch (InterruptedException e) {
			}
			//test if can fill the buffer:
			if (target_line.available() >= BUFFR_SIZE) {
				target_line.read(buffer, 0, BUFFR_SIZE);
				af = hermixLink.construct_video_frame(VideoThread.FR_A_PCM_MONO_16b_8KHz, buffer);
				hermixLink.s_send_video_frame(af);
			}
		}
		System.out.println("Broadcast.run Exit");
	}

	public void stopThread() {
		stopFlag = true;
	}

	public int getErrorFound() {
		return errorFound;
	}
}
