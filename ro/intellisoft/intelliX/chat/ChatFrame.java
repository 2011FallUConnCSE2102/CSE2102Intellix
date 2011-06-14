
package ro.intellisoft.intelliX.chat;

/*
 * User: Administrator
 * Date: Mar 25, 2002
 * Time: 10:30:10 AM
 */

import ro.intellisoft.intelliX.IntelliX;
import ro.intellisoft.intelliX.UI.MyButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

public class ChatFrame extends JPanel implements ActionListener {
	private static final String myColor = "blue";
	private static final String otherColor = "black";
	private static final String errColor = "red";
	private static final String sysColor = "green";

	private static final long logOnTime = System.currentTimeMillis();
	private long pingTime = System.currentTimeMillis();

	private JPanel toolBar = new JPanel(new GridLayout(1, 5));
	private MyButton changeNickButton = new MyButton();
	private MyButton sendPrivateMessage = new MyButton();
	private ColorTextPane textArea = new ColorTextPane();
	private JScrollPane textAreaScroller = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	private HistTextField inputField = new HistTextField();

	private IntelliX parent;

	public ChatFrame(IntelliX parent) {
		super(new BorderLayout());
		this.parent = parent;

		changeNickButton.setMargin(new Insets(1, 1, 1, 1));
		sendPrivateMessage.setMargin(new Insets(1, 1, 1, 1));
		try {
			changeNickButton.setIcon(IntelliX.loadImageResource(this, "images/changename.gif"));
			sendPrivateMessage.setIcon(IntelliX.loadImageResource(this, "images/privatemessage.gif"));
		} catch (Exception e) {
			System.out.println("Images not found!");
		}
		toolBar.add(changeNickButton);
		toolBar.add(sendPrivateMessage);
		changeNickButton.setToolTipText("Use this button to change your nick.");
		sendPrivateMessage.setToolTipText("Sends a private message to another chatter.");
//		createNewTextShare.addActionListener(this);
		changeNickButton.addActionListener(this);
		sendPrivateMessage.addActionListener(this);
		//deocamdata scoatem asta de aici:
		//		this.add(toolBar, BorderLayout.NORTH);
		this.add(textAreaScroller, BorderLayout.CENTER);
		JPanel auxPanel = new JPanel(new GridLayout(2, 1));
		auxPanel.add(new JLabel("Your message:", JLabel.LEFT));
		auxPanel.add(inputField);
		this.add(auxPanel, BorderLayout.SOUTH);
		inputField.addActionListener(this);
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == inputField) {
			String s = ae.getActionCommand().trim();
			if (s.equals("")){
				inputField.setText("");
			} else if (s.charAt(0) == '/'){
				if (processChatMessage(s.substring(1))){
					inputField.setText("");
				} // else leave the input as it was before
			} else {//normal text
				textArea.append(s, myColor);
				parent.getHermixLink().g_send_text(s);
				inputField.setText("");
			}
		}
	}

	public void append(String text) {
		textArea.append(text);
	}

	public void wakeup(String who){
		Toolkit.getDefaultToolkit().beep();
		textArea.append(who + " waked you up!", errColor);
	}

	public void append(String text, String htmlColor) {
		textArea.append(text, htmlColor);
	}

	/**
	 * Process a "loco" message!
	 * Text must start with a command or else it is ignored.
	 * @return true if command has been successfully processed
	 */
	public boolean processChatMessage(String text){
        StringTokenizer analyzer = new StringTokenizer(text);
		if (!analyzer.hasMoreTokens()){
			//nothing to process
			return true;
		}
		String command = analyzer.nextToken();
		String param = "";
		if (analyzer.hasMoreTokens()){
			param = analyzer.nextToken();
		}

		if (command.equals("msg")){
			//private message
			if (param.equals("")){
				return false;
			}
			String message = null;
			if (analyzer.hasMoreTokens()){
				message = analyzer.nextToken("\n");
			} else {
				return false;
			}
			parent.getHermixLink().g_send_text("/msg " + param + " " + message);
			append("You said to "+ param + ": "+message, myColor);
			return true;
		} else if (command.equals("announcement") || command.equals("rm_announcement")){
			return true;
		} else if (command.equals("wakeup")){
			if (param.equals("")){
				return false;
			}
			parent.getHermixLink().s_wake_up(param);
			append("You woke up " + param, sysColor);
			return true;
		} else if (command.equals("nick")){
            String errMsg = ChatUtils.isValidName(param);
			if (errMsg == null){
				parent.getHermixLink().s_change_nick(param, "", "", "");
				append("SYS: Sending request to change nick to " + param + ".", sysColor);
				return true;
			} else {
				return false;
			}
		} else if (command.equals("ping")){
			if (ChatUtils.isValidName(param)!=null){
				param = "";
			}
			pingTime = System.currentTimeMillis();
			parent.getHermixLink().g_send_text("/ping " + param);
			append("SYS: Pinging " + param + "...", sysColor);
			return true;
		} else if (command.equals("clear")){
			textArea.clearScreen();
			return true;
		} else if (command.equals("join") || (command.equals("leave"))){
			append("SYS: Command not available" + param + ".", sysColor);
			return true;
		} else if (command.equals("auth")){
			append("SYS: Command not available" + param + ".", sysColor);
			return true;
		} else if (command.equals("gc")){
			System.runFinalization();
			System.gc();
			append("SYS: Garbage Collector called.", sysColor);
			return processSys("memory");
		} else if (command.equals("property")){
			if (!param.equals("")){
				return processSys(param.toLowerCase());
			} else {
				return false;
			}
		} else if (command.equals("help")){
			String message = "HELP: syntax /help [command]<br>HELP:    where [command] can be msg, nick, ping, etc...<br>";
			if (param.equals("msg")){
				message = "HELP: syntax /msg {nick} [message]<br>HELP:    - sends a message to a specified nick<br>HELP:    - where [nick] is the username to witch the message to be send<br>HELP:          [message] is the text to be send<br>";
			} else if (param.equals("auth") || param.equals("join") || param.equals("leave")) {
				message = "";
			} else if (param.equals("ping")) {
				message = "HELP: syntax /ping [nick]<br>HELP:    - returns the time in witch a message circulates from you to server and back or the time within the [nick] user receives a message from you<br>";
			} else if (param.equals("wakeup")) {
				message = "HELP: syntax /wakeup [nick]<br>" + "HELP:    - play a sonor wakeup to specified nick<br>";
			} else if (param.equals("nick")) {
				message = "HELP: syntax /nick [nick]<br>" + "HELP:    - change your nick to specified one<br>";
			} else if (param.equals("clear")) {
				message = "HELP: syntax /clear <br>" + "HELP:    - clears the chat zone<br>";
			}
			append("<hr>" + message, sysColor);
			return true;
		}
		//do not know how to handle this command
		return false;
	}//processChatMessage(String)

	/**
	 * Processes a message that has came from another User.
	 */
	public void processChatMessage(String text, String nick){
		if (text.charAt(0) != '/') {
			if (!nick.equals(parent.getHermixLink().getNick())) {
				append(nick + " said: " + text, otherColor);
				return;
			}//else o nothin 'cause I sent this line
		}

		StringTokenizer analyzer = new StringTokenizer(text.substring(1));
		if (!analyzer.hasMoreTokens()){
			//nothing to process
			return;
		}

		String command = analyzer.nextToken();
		String param = "";
		if (analyzer.hasMoreTokens()){
			param = analyzer.nextToken();
		}
		if (command.equals("msg")){
			if (param.equals(parent.getHermixLink().getNick())){
				//private message for me
				String message = "";
				if (analyzer.hasMoreTokens()){
					message = analyzer.nextToken("\n");
				}
				append("Private message from "+nick+": "+message, errColor);
			}
		} else if (command.equals("ping")){
			//2 cases: w/ parameter and w/o parameter
			if (param.equals("")){
				if (nick.equals(parent.getHermixLink().getNick())){
					//me sent me received
					long pingPongMillis = System.currentTimeMillis() - pingTime;
					append("Ping to server: "+pingPongMillis+ "ms.", sysColor);
				}//else do nothing, not my message
			} else { //nick contains the user that should respond:
				if (param.equals(parent.getHermixLink().getNick())){
					//me should act some way:
                    if (analyzer.hasMoreTokens()){
						//now I should print the value
						long pingPongMillis = System.currentTimeMillis() - pingTime;
						append("Ping to " + nick + ": "+pingPongMillis+ "ms.", sysColor);
                    } else {
						//nick asked me to send him a ping:
						parent.getHermixLink().g_send_text("/ping "+nick+" pong");
                    }
				} //else do nothing, not my message
			}
		}
	}

	private boolean processSys(String s){
		if (s.equals("server")){
			append("SYS: Hermix Server = " + parent.getHermixLink().getServerHost() + ":" + parent.getHermixLink().getServerPort(), sysColor);
		} else if (s.equals("memory")){
			append("SYS: Memory status: <ul><li>Total = " + Runtime.getRuntime().totalMemory() + " <li>Free = " + +Runtime.getRuntime().freeMemory() + "(" + (10000 * Runtime.getRuntime().freeMemory() / Runtime.getRuntime().totalMemory() / 100.0) + "%)" + "</ul>", sysColor);
		}else if (s.equals("logon")) {
			long totalTime = (System.currentTimeMillis() - logOnTime) / 1000;
			append("SYS: You are connected to server since " + new java.util.Date(logOnTime).toString() + "(" + totalTime / 60 + "'" + totalTime % 60 + "\").", sysColor);
		} else if (s.equals("ip")) {
			String IP = null;
			try {
				java.net.InetAddress localHost = java.net.InetAddress.getLocalHost();
				IP = localHost.getHostName() + "(" + localHost.getHostAddress() + ")";
			} catch (Exception ex) {
				IP = "N/A (Security exception).";
			}
			append("SYS: Your host is " + IP + ".", sysColor);
		} else {
			return false;
		}
		return true;
	}//processSys(String)
}
