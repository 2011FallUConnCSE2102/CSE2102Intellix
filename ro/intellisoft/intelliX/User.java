/**
 * User: Administrator
 * @Author: qurtach@intellisoft.ro
 * @Date: May 29, 2002
 * @Time: 4:19:19 PM
 */

package ro.intellisoft.intelliX;

import com.hermix.event.hAudioListener;
import com.hermix.AudioFrame;
import com.hermix.VideoThread;
/*import com.ibm.media.codec.audio.gsm.GsmEncoder; tms*/

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.sound.sampled.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.Vector;

import ro.intellisoft.intelliX.UI.MyButton;
import ro.integrasoft.audio.GSMDecoder;

public class User extends JDialog implements hAudioListener, ActionListener, Runnable, ChangeListener{
	//user related properties:
	private String nick = null;
	private String realName = null;
	private String description = null;

	/**tells if user is broadcasting:*/
	private boolean broadcastOn = false;

	private static int errorOccured = 0;

	/**link to the rest of application:*/
	private IntelliX IDE = null;

	/**this is a thread(runnable)*/
	private Thread internalThread = null;

	/**tells if the user was spwaking max 1 sec ago..*/
	private int userIsSpeaking = 0;

	/**linia pe care se scroi datele*/
	private SourceDataLine source_line = null;
	private GSMDecoder gsm_decoder = new GSMDecoder();
	/**act as a cache if more audio frames comes at once!*/
	Vector audioframes = new Vector();

	//menu variables:
	private JPopupMenu userMenu = new JPopupMenu();
	private JMenuItem properties = new JMenuItem("Properties");
	private JMenuItem recordToFile = new JMenuItem("Record...");

	//interface fields:
	private JTextField nickField = new JTextField();
    private JTextField nameField = new JTextField();
	private JTextField descField = new JTextField();
	private JCheckBox audioEnabled = new JCheckBox("Enabled", false);
	private JSlider audioVolume = new JSlider(JSlider.HORIZONTAL, 0, 99, 50);
	private MyButton okButton = new MyButton("Ok");
	private MyButton cancelButton = new MyButton("Cancel");
	private MyButton applyButton = new MyButton("Apply");
	private JLabel audioStatusLabel = new JLabel();

	public static final int AUDIO_NOT_STARTED 		=-1;
	public static final int AUDIO_USER_OK 			= 0;
	public static final int UNWANTED_AUDIO_USER 	= 1;
	public static final int USER_IS_NOT_BROADCASTING= 2;
	public static final int USER_IS_SPEAKING 		= 3;
	public static final int USER_IS_CURRENT_USER	= 4;

	public static final int GSM_FRAME_SIZE = 33;

	//here we remember the old state in order to restore it if cancel is pressed:
	private String oldNick = null;
	private boolean oldAudioEnabled = false;
	private int oldAudioVolumeLevel = 0;

	public User(IntelliX IDE, String nick, String realName, String description) {
		super(IDE.getMainFrame(), true);
		this.IDE = IDE;
		this.description = description;
		this.nick = nick;
		this.realName = realName;

		userMenu.add(recordToFile);
		userMenu.addSeparator();
		userMenu.add(properties);
		recordToFile.addActionListener(this);
		properties.addActionListener(this);
		recordToFile.setEnabled(false);

		initUI();
		initAudioPlayer();
	}

	/**builds up the interface for the dialog*/
	private void initUI(){
		this.setTitle("Properties for " + nick);
		this.setResizable(false);
		this.getContentPane().setLayout(new BorderLayout(4, 4));

		JPanel userLabels = new JPanel(new GridLayout(3, 1, 2, 2));
		this.getContentPane().add(userLabels, BorderLayout.WEST);
		userLabels.add(new JLabel("Nick:", JLabel.LEFT));
		userLabels.add(new JLabel("Real name:", JLabel.LEFT));
		userLabels.add(new JLabel("Description:", JLabel.LEFT));

		JPanel userValues = new JPanel(new GridLayout(3, 1, 2, 2));
		this.getContentPane().add(userValues, BorderLayout.CENTER);
		userValues.add(nickField);
		userValues.add(nameField);
		userValues.add(descField);
		nickField.setEditable(false);
		nameField.setEditable(false);
		descField.setEditable(false);

		JPanel lowerPanel = new JPanel(new BorderLayout(2, 2));
		this.getContentPane().add(lowerPanel, BorderLayout.SOUTH);
		JPanel audioPanel = new JPanel(new BorderLayout(2, 2));
		lowerPanel.add(audioPanel, BorderLayout.CENTER);
		audioPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Audio properties "));
		audioPanel.add(audioStatusLabel, BorderLayout.WEST);
		audioPanel.add(audioEnabled, BorderLayout.CENTER);
		audioPanel.add(audioVolume, BorderLayout.SOUTH);
		audioVolume.addChangeListener(this);
		audioEnabled.addActionListener(this);
		if (nick.equals(IDE.getHermixLink().getNick())){
			audioEnabled.setText("Enable broadcast");
		}

		JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 2, 2));
		lowerPanel.add(buttonPanel, BorderLayout.EAST);
		buttonPanel.add(applyButton);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		cancelButton.addActionListener(this);
		applyButton.addActionListener(this);
		okButton.addActionListener(this);

		this.pack();
	}

	public void changeNick(String newNick){
		nick = newNick;
		if (isVisible()){
			this.setTitle("Properties for " + nick);
			nickField.setText(nick);
		}
	}

	/**play an audio frame */
	public void play_audio_frame(AudioFrame af) {
		if (!af.nick.equals(nick)){
			// frame not for this user!
			return;
		}
		//overflow control!!!
		if (audioframes.size() < 20){
			synchronized (audioframes) {
				audioframes.addElement(af);
			}
		} else {
			System.err.println("Audio overflow on "+ internalThread.getName());
		}
	}

	/**Invoked when an action occurs.*/
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == audioEnabled){
			audioVolume.setEnabled(audioEnabled.isSelected());
		} else if (e.getSource() == okButton){
			updateStatus();
			super.setVisible(false);
		} else if (e.getSource() == applyButton){
			updateStatus();
		} else if (e.getSource() == cancelButton){
			audioEnabled.setSelected(oldAudioEnabled);
			audioVolume.setValue(oldAudioVolumeLevel);
			super.setVisible(false);
		} else if (e.getSource() == properties){
			setVisible(true);
		} else if (e.getSource() == recordToFile){
			//open fileChooser
            //open selected .gsm file
			//change menu to stopRecording
		}
	}

	/**updates the status for this user*/
	private void updateStatus(){
		if (audioEnabled.isSelected() != oldAudioEnabled){
			//send a message in order to add/remove user from wanted a/v uswers
			oldAudioEnabled = !oldAudioEnabled;
			//test for broadcast:
			if (nick.equals(IDE.getHermixLink().getNick())){
				if (audioEnabled.isSelected()){
					IDE.getHermixLink().startBroadcast();
				} else {
					IDE.getHermixLink().stopBroadcast();
				}
			} else {//other user:
				IDE.getHermixLink().sendNewAudioList();
				if (oldAudioEnabled) {
					IDE.getHermixLink().add_audio_listener(this.nick, this);
				} else {
					IDE.getHermixLink().remove_audio_listener(this.nick, this);
				}
			}
		}
		if (oldNick != null && !nickField.getText().equals(oldNick)){
			//send change name
			IDE.getHermixLink().s_change_nick(nickField.getText(), "guest", "", "");
			oldNick = nickField.getText();
		}
		audioStatusLabel.setIcon(getIconForState(this, getUserStatus()));
		this.repaint();
	}

	public void toggleSound(){
		if (broadcastOn){
			audioEnabled.setSelected(!oldAudioEnabled);
			updateStatus();
		}
	}


	/**overwrite in order to update some fields*/
	public void setVisible(boolean flag){
		if (flag == true){
			if (IDE.getHermixLink().getNick().equals(nick) && IDE.getHermixLink().getBroadcast().getErrorFound() != 0){
				errorOccured = -3;
			}
			audioEnabled.setEnabled(broadcastOn || IDE.getHermixLink().getNick().equals(nick));
			if (broadcastOn){
				audioVolume.setEnabled(audioEnabled.isSelected());
			} else {
				audioVolume.setEnabled(false);
			}
			audioStatusLabel.setIcon(getIconForState(this, getUserStatus()));
			//now test to see whetever the broadcast/play engine has correctly started!
			if (errorOccured != 0){
				audioStatusLabel.setIcon(getIconForState(this, User.UNWANTED_AUDIO_USER));
				audioVolume.setEnabled(false);
				audioEnabled.setEnabled(false);
				audioEnabled.setText("Hardware error");
			}
			setLocationRelativeTo(IDE.getMainFrame());
			nickField.setText(nick);
			nameField.setText(realName);
			descField.setText(description);

			nickField.setEditable(getUserStatus() == User.USER_IS_CURRENT_USER);
			oldNick = nickField.getText();
			oldAudioEnabled = audioEnabled.isSelected();
			oldAudioVolumeLevel = audioVolume.getValue();
			super.setVisible(true);
		} else {
			//act as usual
			super.setVisible(false);
		}
	}

	public void showMenu(Component invoker, int x, int y){
		this.userMenu.show(invoker, x, y);
	}

	/**
	 * update the broadcast flag @param broadcastStarted is true if
	 * user has started the broadcast, false if it is not broadcasting
	 */
	public void updateBroadcastState(boolean broadcastStarted){
		broadcastOn = broadcastStarted;
	}

	/**sets the flag that enables to play audio on this user*/
	public void enableAudio(boolean flag){
		audioEnabled.setSelected(flag);
		oldAudioEnabled = flag;
	}

	/**returns the (audio) status of this user*/
	public int getUserStatus(){
		if (nick.equals(IDE.getHermixLink().getNick())){
			return USER_IS_CURRENT_USER;
		} else if (userIsSpeaking>0){
			return USER_IS_SPEAKING;
		} else if (!broadcastOn){
			return USER_IS_NOT_BROADCASTING;
		} else {
			if (!audioEnabled.isSelected()){
				return UNWANTED_AUDIO_USER;
			} else {
				//test to see if it is playing, if so, return USER_IS_SPEAKING else
				return AUDIO_USER_OK;
			}
		}
	}//getUserStatus method

	/**
	 * When an object implementing interface <code>Runnable</code> is used
	 * to create a thread, starting the thread causes the object's
	 * <code>run</code> method to be called in that separately executing thread.
	 * The general contract of the method <code>run</code> is that it may take any action whatsoever.
	 * @see     java.lang.Thread#run()
	 */
	public void run() {
        Thread myThread = Thread.currentThread();
        while (internalThread == myThread) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
			//if there is at least one audio frame
			if (audioframes.size()>0){
				if (userIsSpeaking<1){
					IDE.updateUserList();
				}
				userIsSpeaking = 3;
				processAudioNextFrame();
			} else {
				userIsSpeaking--;
				if (userIsSpeaking==0){
					IDE.updateUserList();
				}
			}
        }
    }

	private void processAudioNextFrame(){
		//get the frame out:
		AudioFrame af = null;
		synchronized (audioframes) {
			af = (AudioFrame) audioframes.elementAt(0);
			audioframes.removeElementAt(0);
		}

		byte[] audio_bytes = null;
		//analyze its type:
		if (af.type == VideoThread.FR_A_GSM) {
			//then decode gsm to PCM 16b 8KHz
			byte[] gsm_frame = new byte[GSM_FRAME_SIZE];
			//a frame may have one or more GSM_FRAME_SIZE size chuncks:
			int chunks = af.data.length / GSM_FRAME_SIZE;
			try {
				for (int i = 0; i < chunks; i++) {
					//process only one chunck at the time:
					for (int j = 0; j < GSM_FRAME_SIZE; j++) {
						gsm_frame[j] = af.data[i * GSM_FRAME_SIZE + j];
					}
					int[] output = gsm_decoder.decode(gsm_frame);
					//now we have the decoded sound as int
					byte[] bytes = new byte[2 * output.length];
					for (int k = 0; k < output.length; k++) {
						bytes[2 * k] = (byte) output[k];
						bytes[2 * k + 1] = (byte) (output[k] / 256);
					}
					//the 2k+2 and 2k+3 are just garbage
					if (audio_bytes == null) {
						audio_bytes = bytes;
					}
					byte[] temp_bytes = audio_bytes;
					//the size of frame is growing up..
					audio_bytes = new byte[temp_bytes.length + bytes.length];
					//copy old content to the new allocated location...
					System.arraycopy(temp_bytes, 0, audio_bytes, 0, temp_bytes.length);
					//append new byates at the end..
					System.arraycopy(bytes, 0, audio_bytes, temp_bytes.length, bytes.length);
				}//for
			} catch (Exception ex) {
				System.out.println("Gsm decode error\n");
				return;
			}
		} else if (af.type == VideoThread.FR_A_PCM_MONO_16b_8KHz) {
			audio_bytes = af.data;
		} else
			return;//unknown format
		//now we can write these bytes on the line source
		if (audio_bytes != null) {
			source_line.write(audio_bytes, 0, audio_bytes.length);
		}
	}

	/**Inits the player system*/
	private void initAudioPlayer(){
		if (errorOccured != 0){
            return;
		}
		AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, false);
		DataLine.Info source_info = new DataLine.Info(SourceDataLine.class, format);
		if (!AudioSystem.isLineSupported(source_info)) {
			System.out.println("Can't get a source data line with format: " + format.toString());
			JOptionPane.showMessageDialog(IDE.getMainFrame(), "Can't get a source data line with format:\n" + format.toString() + "\nBroadcast disabled.","Error starting audio player engine", JOptionPane.ERROR_MESSAGE);
			errorOccured = -1;
			return;
		}
		try {
			source_line = (SourceDataLine) AudioSystem.getLine(source_info);
			source_line.open(format, 24000); //one second and a half buffer
		} catch (LineUnavailableException e) {
			System.out.println("Start play exception: " + e.getMessage());
			JOptionPane.showMessageDialog(IDE.getMainFrame(), "Can't get a source data line with format:\n" + format.toString() + "\nBroadcast disabled.","Error starting audio player engine", JOptionPane.ERROR_MESSAGE);
			source_line = null;
			errorOccured = -1;
			return;
		}
		source_line.start();
		start();
	}

	/**starts current thread*/
	public void start() {
        if (internalThread == null) {
            internalThread = new Thread(this, "AudioPlayer for "+ nick);
            internalThread.start();
        }
    }

	/**stops the thread. It may be working when this method exits, but not more than 1 sec.*/
	public void stop() {
        internalThread = null;
		source_line.stop();
		source_line.close();
    }

	/**Invoked when the target of the listener has changed its state.*/
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == audioVolume){
            setVolume(audioVolume.getValue());
		}
	}

	public void setVolume(int volume) {
		if (source_line == null) {
			return;//???
		}
		FloatControl volume_control = null;
		try {
			volume_control = (FloatControl) source_line.getControl(FloatControl.Type.MASTER_GAIN);
		} catch (Exception e) {}
		float volume_max = volume_control.getMaximum();
		float volume_min = volume_control.getMinimum();
		float volume_new = 20.0f * (float) Math.log(volume);
		volume_new += volume_min;
		volume_control.setValue(volume_new);
		return;
	}


	static public Icon getIconForState(Component c, int status){
		String imageName = "disabled";
		switch (status) {
			case User.AUDIO_USER_OK:
				imageName = "";
				break;
			case User.UNWANTED_AUDIO_USER:
				imageName = "off";
				break;
			case User.USER_IS_SPEAKING:
				imageName = "on";
				break;
		}
		return IntelliX.loadImageResource(c, "images/sound" + imageName + ".gif");
	}

}//User class
