/**
 * last Update by Maxiniuc Ovidiu at 10.10.2001
 * Clasa principala res[ponsabila cu comunicatia cu serverul.
 * Ingobeaza toate functiile de transmitere (finale)
 * Se comporta ca un adapter pentru functiile de receptie,
 *      utilizatorul supreascrie numai functiile de care are nevoie.
 */

package com.hermix;

import com.hermix.event.hAudioListener;
import com.hermix.event.hLogListener;
import com.hermix.event.hVideoListener;
import ro.integrasoft.chat.message.*;

import java.awt.*;
import java.net.Socket;
import java.util.Date;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

public class HermixApi extends Thread {
	private String nick;
	private String pass;
	private String name;
	private String desc;
	private int port;
	private String host;
	private boolean connected;
	private Socket socket;
	private MessageReader msgin;
	private MessageSender msgout;
	private Hashtable videolisteners;
	private Hashtable audiolisteners;
	private Hashtable grouplisteners;
	private hLogListener loglistener;
	private int errorcode;
	private ThreadGroup threadgroup;

	private String version = "cl2030b1";

	protected VideoThread videothread;
	protected AudioThread audiothread;

	//folosit pentru a termina 'cu bine' thread -ul
	private boolean stopp = false;

	private Statistics statistics;
	/** Folosit pentru statistica */
	private int lastMessageSize = 0;

	/**
	 * pentru transmisii de fisiere... acestea este numele complete ale
	 *  fisierelor ce trbuie transmise <br>
	 */
	public Vector longFileNames = new Vector(10, 3);

	public HermixApi() {
		super(new ThreadGroup("HermixThreads" + (new Date()).getTime()), "Main thread");
		threadgroup = getThreadGroup();
		statistics = new Statistics();
		statistics.start();
	}

	public final Statistics getStatistics() {
		return statistics;
	}

	public void stopVideoThread() {
		videothread.stopThread();
	}

	public final void s_connect(String host, int port, String nick, String password, String real, String description) {
		connected = false;
		videolisteners = new Hashtable();
		audiolisteners = new Hashtable();
		grouplisteners = new Hashtable();
		loglistener = null;
		connected = false;
		videothread = null;
		audiothread = null;
		this.nick = nick;
		this.pass = password;
		this.name = real;
		this.desc = description;
		this.port = port;
		this.host = host;
		errorcode = 0;
		i_log("Connecting to host " + host + ":" + port);
		start();
	}

	public void stopThread() {
		stopp = true;
	}

	/**some threads may stop later , after returning from disconect.
	 *  as an advice, wait for 5-10 secs, after calling disconnect.
	 */
	public final void s_disconnect() {
		i_log("Disconnect called.");
		try {
			socket.close();
			msgin.stop();       //stopping messagereader
			msgout.stop();      //stopping messagesender
			stopThread();             //stop this thread.
		} catch (Exception ex) {
		}
		//i_log( "Stopping all threads, curently there are running "+
		// threadgroup.activeCount() );
		connected = false;
		if (videothread != null) {
			videothread.videoOff();
		}
		if (audiothread != null) {
			audiothread.audioOff();
		}
	}

	public final void s_get_available_groups() {
		i_log("->SYS_Get_My_Groups ");
		SYSMessage sms = new SYSMessage(SC.SYS_Get_My_Groups);
		try {
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while querying my groups", errorcode);
			ex.printStackTrace();
		}
	}

	public final void s_get_connected_users() {
		i_log("->SYS_Get_Connected_Users ");
		SYSMessage sms = new SYSMessage(SC.SYS_Get_Connected_Users);
		try {
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while querying connected users", errorcode);
			ex.printStackTrace();
		}
	}

	public final void s_change_nick(String aNick, String aPassword, String realName, String description) {
		Vector v = new Vector();
		i_log("->SYS_Change_Nick to " + aNick + " realName=" + realName + " passord=" + aPassword + " description=" + description);
		v.addElement(aNick);//newNick
		v.addElement(realName);//Real Name
		v.addElement(description);//descriere
		v.addElement(aPassword);//new Password
		SYSMessage sms = new SYSMessage(SC.SYS_Change_Nick, v);
		try {
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while sending change nick request", errorcode);
			ex.printStackTrace();
		}
	}

	public void r_connected(String newnick, String rights) {
	}

	public void r_communication_error(int code) {
	}

	public void r_communication_error(String errMessage, int code) {
	}

	public void r_change_rights(String rights) {
	}

	public void r_insert_group(String groupname, String description, String rights) {
	}

	public void r_remove_group(String groupname) {
	}

	public void r_change_nick(String aNick, String oldNick) {
	}

	public void r_insert_user(String nick, String realname, String description, String time) {
	}

	public void r_remove_user(String nick) {
	}

	public void r_join_group(String groupname) {
	}

	public void r_leave_group(String name) {
	}

	public void r_system_user_message(int id, Object data) {
	}

	public void r_get_users_from_group(String groupname, Vector users) {
	}

	public void r_get_user_groups(String nick, Vector users) {
	}

	public void r_wake_up(String nick) {
	}

	public void r_create_private_group(String groupname) {
	}

	public final void s_set_audio(int send, int receive) {
	}

	public final void s_set_video(int send, int receive) {
	}

	public final void s_set_file_transfer(int send, int receive) {
	}

	//raspuns de la server cu lista de fisiere
	//added by seKurea
	//  vect[0] = group name
	//  vect[1,..,..] = nume fisiere
	public void r_get_file_list(String group, Vector vect) {
	}

	//raspuns la s_send_file_info
	//added by seKurea
	// vect[0] :    filename
	// vect[1] :  ? id (ex: file_997875306738)
	// vect[2] :    group
	// vect[3] :    size
	// vect[4] :    uploader name
	// vect[5] :  ? (ex: "no")
	public void r_get_file_info(Vector vect) {
	}

	//raspuns de la server ca e gata de transfer
	//added by seKurea
	// vect[0] : server
	// vect[1] : port
	// vect[2] : size
	// vect[3] : op (?)
	// vect[4] : group
	// vect[5] : username
	// vect[6] : file
	// vect[7] : coords (?)
	public void r_get_file(Vector vect) {
	}

	//raspuns de la server ca e gata de upload
	//added by seKurea
	public void r_send_file(String server, int port, String file, String group) {
	}

	public void r_video_watching(String watcher, boolean status) {
	}

	public final void s_set_whiteboard(int send, int receive) {
	}

	public void r_add_video_user(String nick, int id) {
	}

	public void r_remove_video_user(String nick, int id) {
	}

	public final void s_allow_video_user(String nick, int id) {
	}

	public void r_invite_join_private_group(String privateGroup, String owner) {
	}

	public void r_video_connection_lost() {
	}

	public void r_audio_connection_lost() {
	}

	public final void s_deny_video_user(Vector vect) {
		i_log("->SYS_Deny_Video_Users");
		SYSMessage sms = new SYSMessage(SC.SYS_Deny_Video_User, vect);
		try {
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while sending wanted video users", errorcode);
			ex.printStackTrace();
		}
	}

	public final void s_join_group(String groupname) {
		i_log("->SYS_Join_Group " + groupname);
		SYSMessage sms = new SYSMessage(SC.SYS_Join_Group, groupname);
		try {
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while sending join request", errorcode);
			ex.printStackTrace();
		}
	}

	public final void s_leave_group(String groupname) {
		i_log("->SYS_Leave_Group " + groupname);
		SYSMessage sms = new SYSMessage(SC.SYS_Leave_Group, groupname);
		try {
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while sending leave request", errorcode);
			ex.printStackTrace();
		}
	}

	public final void s_system_user_message(int id, Object data) {
		i_log("->SYS_User_Message id " + id);
		SYSMessage sms = new SYSMessage(id + 1000, data);
		try {
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while sending user message #" + id, errorcode);
			ex.printStackTrace();
		}
	}

	//administrative
	public final void s_get_users_from_group(String groupname) {
		i_log("->SYS_Get_Users_From_Group " + groupname);
		SYSMessage sms = new SYSMessage(SC.SYS_Get_Users_From_Group, groupname);
		try {
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while querying users fron group", errorcode);
			ex.printStackTrace();
		}
	}

	public final void s_get_user_groups(String nick) {
		i_log("->SYS_Get_User_Groups " + nick);
		SYSMessage sms = new SYSMessage(SC.SYS_Get_User_Groups, nick);
		try {
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while querying groups for user " + nick, errorcode);
			ex.printStackTrace();
		}
	}

	public final void s_create_private_group(String groupname, Vector users) {
		i_log("->SYS_Create_Group " + groupname);
		users.insertElementAt(groupname, 0);
		SYSMessage sms = new SYSMessage(SC.SYS_Create_Group, users);
		try {
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while creating private group " + groupname, errorcode);
			ex.printStackTrace();
		}
	}

	public final void s_wake_up(String nick) {
		i_log("->SYS_Wake_Up " + nick);
		SYSMessage sms = new SYSMessage(SC.SYS_Wake_Up, nick);
		try {
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while sending wake-up to " + nick, errorcode);
			ex.printStackTrace();
		}
	}

	public final void s_set_band_out(int bandout) {
		Vector v = new Vector();
		i_log("->SYS_Client_Options (bandout=" + bandout);
		v.addElement("BandOut " + bandout);
		SYSMessage sms = new SYSMessage(SC.SYS_Client_Options, v);
		try {
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while setting band-out", errorcode);
			ex.printStackTrace();
		}
	}

	//cerere catre server pentru file list
	//added by seKurea
	public final void s_get_file_list(String group) {
		i_log("->SYS_Get_File_List " + nick);
		Vector vect = new Vector();
		vect.add(group);
		vect.add("10");     //ultimele 10 fis
		SYSMessage sms = new SYSMessage(SC.SYS_Get_File_List, vect);
		try {
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while querying file list on " + group, errorcode);
			ex.printStackTrace();
		}
	}

	//cerere informatii de la server despre un file
	//added by seKurea
	public final void s_get_file_info(String group, String nick, String filename) {
		i_log("->SYS_Get_File_Info filename: " + filename + " by: " + nick + " from: " + group);
		Vector vect = new Vector();
		vect.add(group);
		vect.add(nick);
		vect.add(filename);
		SYSMessage sms = new SYSMessage(SC.SYS_Get_File_Info, vect);
		try {
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while querying info for file " + filename, errorcode);
			ex.printStackTrace();
		}
	}

	//cerere catre server download file
	//added by seKurea
	public final void s_get_file(String file_id, String group, String nick) {
		i_log("->SYS_Get_File id: " + file_id + " by: " + nick + " from: " + group);
		Vector vect = new Vector();
		vect.add(file_id);
		vect.add(group);
		vect.add(nick);
		SYSMessage sms = new SYSMessage(SC.SYS_Get_File, vect);
		try {
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while querying file " + file_id, errorcode);
			ex.printStackTrace();
		}
	}

	//cerere catre server upload file
	// added by seKurea
	// op: "0"-whiteboard, "1"-window, "2"-file
	public final void s_send_file(String filesize, String op, String group, String file) {
		i_log("->SYS_Send_File file: " + file + " op: " + op + " to: " + group);
		Vector vect = new Vector();
		vect.add(filesize);
		vect.add(op);
		vect.add(group);
		vect.add(file);
		SYSMessage sms = new SYSMessage(SC.SYS_Send_File, vect);
		try {
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while sending file " + file, errorcode);
			ex.printStackTrace();
		}
	}

	/**
	 * Metoda apelata ca sa informez emitatorul ca il urmaresc.
	 * @param who persoana urmatita
	 * @param status incep sa urmaresc sau am terminat urmarirea
	 */
	public final void s_video_watching(String who, boolean status) {
		i_log("->SYS_Send_Video_Watching: " + getNick() + " on: " + who);
		Vector vect = new Vector();
		vect.add(who);
		vect.add(getNick());
		vect.add(status?"1":"0");
		SYSMessage sms = new SYSMessage(SC.SYS_Video_Watching, vect);
		try {
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while sending watch warning to " + who, errorcode);
			ex.printStackTrace();
		}
	}

	/**
	 * Metoda care trimite mesaj ban pe un user.
	 */
	public final void s_ban_user(String kickedUser) {
		i_log("->SYS_Send_Ban");
		Vector vect = new Vector();
		vect.add(kickedUser);
		vect.add("being ugly");
		SYSMessage sms = new SYSMessage(SC.SYS_Kick_User, vect);
		try {
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while banning user", errorcode);
			ex.printStackTrace();
		}
	}

	/**
	 * Metoda care trimite mesaj kick off pe un user.
	 */
	public final void s_kick_off_user(String kickedUser, String aGroup) {
		i_log("->SYS_Send_Kick");
		Vector vect = new Vector();
		vect.add(kickedUser);
		vect.add(aGroup);
		vect.add("all the reasons");
		SYSMessage sms = new SYSMessage(SC.SYS_Remote_Disconnect, vect);
		try {
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while kicking off user", errorcode);
			ex.printStackTrace();
		}
	}

	public final boolean s_send_video_frame(VideoFrame vf) {
		return videothread.s_send_video_frame(vf);
	}

	public final VideoFrame construct_video_frame(int type, byte[] data) {
		//i_log("Construct videoframe type "+type );
		VideoFrame vf = new VideoFrame();
		vf.type = type;
		vf.data = data;
		vf.framesize = data.length;
		vf.id = -1;
		vf.user = new byte[20];
		for (int i = 0; i < 20; i++) {
			if (i < nick.length())
				vf.user[i] = (byte) nick.charAt(i);
			else
				vf.user[i] = 0;
		}
		vf.timestamp = (int) (new Date()).getTime();
		return vf;
	}

	public final void s_send_audio_frame(AudioFrame af) {
		if (audiothread != null)
			audiothread.send_audio_frame(af);
	}

	//added by seKurea
	/** Trimite serverului cerere ca de la userul "dest_nick" sa nu
	 * primim mai mult de "nr_frames" frame-uri <br>
	 */
	public final void s_set_video_frames(String dest_nick, int nr_frames) {
		i_log("->SYS_Send_Set_Max_Frames : no more than " + nr_frames + " frames from : " + dest_nick);
		Vector vect = new Vector();
		vect.add(dest_nick);
		vect.add("" + nr_frames);
		SYSMessage sms = new SYSMessage(SC.SYS_Set_Max_Frames, vect);
		try {
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while seting maxframe rate", errorcode);
			ex.printStackTrace();
		}
	}//end s_set_video_frames

	public final AudioFrame construct_audio_frame(int type, int packet, int length, byte[] data) {
		AudioFrame af = new AudioFrame();
		af.type = type;
		af.packet = packet;
		af.length = length;
		af.data = data;
		return af;
	}

	//completed by seKurea
	// i call when i stop the broadcasting
	public final void s_remove_video_user(String aNick, int id) {
		i_log("->SYS_Del_Video_User " + aNick);
		SYSMessage sms = new SYSMessage(SC.SYS_Alt_Del_Video_User, aNick);
		try {
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while sending broadcast off message", errorcode);
			ex.printStackTrace();
		}
	}

	//listeners
	public final void add_group_listener(String groupname, hGroup listener) {
		//pentru fiecare grup, grouplisteners contine un vector cu clasele care vor primi mesajul
		i_log("Registering grouplistener " + listener + " for group " + groupname);
		Vector vect = null;
		synchronized (grouplisteners) {
			vect = (Vector) grouplisteners.get(groupname);
			if (vect == null) {
				vect = new Vector();
				vect.addElement(listener);
			} else {
				if (vect.contains(listener))
					vect.removeElement(listener);
				vect.addElement(listener);
			}
			grouplisteners.put(groupname, vect);
		}
	}

	public final void remove_group_listener(String groupname, hGroup listener) {
		i_log("Unregistering grouplistener " + listener + " for group " + groupname);
		Vector vect = null;
		synchronized (grouplisteners) {
			vect = (Vector) grouplisteners.get(groupname);
			if (vect == null)
				return;
			if (vect.contains(listener)) {
				vect.remove(listener);
			}
			if (vect.size() < 1) {
				grouplisteners.remove(groupname);
				//help gc to clean up the memory
				vect = null;
			}
		}
	}

	public final void add_log_listener(hLogListener loglistener) {
		this.loglistener = loglistener;
	}

	public final void add_video_listener(String nick, hVideoListener videolistener) {
		i_log("Adding video listener for nick " + nick);
		videothread.add_video_listener(nick, videolistener);
	}

	public void remove_video_listener(String nick, hVideoListener videolistener) {
		i_log("Removing video listener for nick " + nick);
		videothread.remove_video_listener(nick, videolistener);
	}

	public final void add_audio_listener(String nick, hAudioListener audiolistener) {
		//i_log("Adding audio listener " + audiolistener + " for nick " + nick);
		videothread.add_audio_listener(nick, audiolistener);
	}

	public final void remove_audio_listener(String nick, hAudioListener audiolistener) {
		//i_log("Removing audio listener " + audiolistener + " for nick " + nick);
		videothread.remove_audio_listener(nick, audiolistener);
	}

	public final void i_log(String message) {
		System.out.println((new Date()).toString() + " " + message);
		//if there is a registered log listener, we call it.
		if (loglistener != null)
			loglistener.log(new Date(), message);
	}

	private Message nextMessage() throws Exception {
		IntHolder size = new IntHolder();
		Message msg = msgin.nextMessage(size);
		lastMessageSize = size.getValue();
		return msg;
	}

	private int addMessage(Message msg) throws Exception {
		return msgout.addMessage(msg);
	}

	//handle all incomming system messages
	private void doSysMessage(SYSMessage sms) {
		Vector v = null;
		String groupname = null;
		String nick = null;

		switch (sms.getMsgId()) {
			case SC.SYS_Your_Groups:
				i_log("<-SYS_Your_Groups from server.");
				v = (Vector) sms.getData();
				//normal ar trebui sa fie un grup 'defaut'
				//daca nu este il folosesc pe ultimul!
				boolean foundDefaultGroup = false;
				for (int i = 0; i < v.size(); i++) {
					String[] s = (String[]) v.elementAt(i);
					//s[0] = groupName
					//s[1] = description
					//there is no right supplied by the server right now
					r_insert_group(s[0], s[1], "");
				}
				break;
			case SC.SYS_Connected_Users:
				i_log("<-SC.SYS_Connected_Users from server.");
				v = (Vector) sms.getData();
				for (int i = 0; i < v.size(); i++) {
					String[] s = (String[]) v.elementAt(i);
					r_insert_user(s[0], s[1], "", s[2]);
					//description not supplied by the server
				}
				break;
			case SC.SYS_User_Join:
				i_log("<-SC.SYS_User_Join from server.");
				v = (Vector) sms.getData();
				r_insert_user((String) v.elementAt(0), (String) v.elementAt(1), "", (String) v.elementAt(2));
				break;
			case SC.SYS_Nick_Changed:
				i_log("<-SC.SYS_Nick_Changed from server.");
				String oldNick = (String) sms.getUser();
				nick = (String) sms.getData();
				if (this.nick.equals(oldNick))
					this.nick = nick;
				r_change_nick(nick, oldNick);
				break;
			case SC.SYS_User_Part:
				i_log("<-SC.SYS_User_Joign from server.");
				nick = (String) sms.getData();
				r_remove_user(nick);
				break;
			case SC.SYS_Join_Group_Ok:
				i_log("<-SYS_Join_Goup_Ok from server.");
				groupname = (String) sms.getData();
				r_join_group(groupname);
				break;
			case SC.SYS_Leave_Group_Ok:
				i_log("<-SYS_Leave_Group_Ok from server.");
				groupname = (String) sms.getData();
				r_leave_group(groupname);
				break;
			case SC.SYS_Wake_Up:
				i_log("<-SYS_Wake_Up from server.");
				nick = (String) sms.getUser();
				r_wake_up(nick);
				break;
			case SC.SYS_Create_Group_Ok:
				groupname = (String) sms.getData();
				String s = sms.getUser();
				i_log("<-SYS_Create_Group_Ok " + s);
				r_insert_group(groupname, "private", "");
				//group name and rights not supplied by server
				if (s.equals(this.nick)) {
					r_create_private_group(groupname);
					r_join_group(groupname);
				} else
					r_invite_join_private_group(groupname, s);
				break;
			case SC.SYS_Remove_Group:
				groupname = (String) sms.getData();
				i_log("<-SYS_Remove_Group " + groupname);
				r_remove_group(groupname);
				break;
			case SC.SYS_Users_From_Group:
				v = (Vector) sms.getData();
				groupname = (String) v.elementAt(0);
				v.removeElementAt(0);
				i_log("<-SYS_Users_from_Group " + groupname);
				ir_get_users_from_group(groupname, v);
				break;
				//by Qurtach in kick user:
			case SC.SYS_Remote_Disconnect:
				v = (Vector) sms.getData();
				String user = (String) v.elementAt(0);
				if (!user.toUpperCase().equals(this.getNick().toUpperCase()))
					return; //e pentru alt user
				groupname = (String) v.elementAt(1);
				String rs = (String) v.elementAt(2);
				doGroupMessage(new GRPMessage(SC.GRP_Text_Message, new GroupObject(groupname, "You have been kicked off for " + rs + " by " + sms.getUser() + ".")));
				//si il scoatem afara!
				this.s_disconnect();
				break;
				//File Transfer Messages
				//added by seKurea
			case SC.SYS_Get_File_List:
				v = (Vector) sms.getData();
				groupname = (String) v.elementAt(0);
				i_log("<-SYS_Get_File_List " + groupname);
				StringTokenizer analizer = new StringTokenizer(groupname);
				String grpname = new String();
				if (analizer.hasMoreElements())
					grpname = (String) analizer.nextElement();
				r_get_file_list(grpname, v);
				break;
			case SC.SYS_Get_File_Info:
				i_log("<-SYS_Get_File_Info ");
				v = (Vector) sms.getData();
				r_get_file_info(v);
				break;
			case SC.SYS_Get_File_Ok:
				i_log("<-SYS_Get_File_Ok ");
				v = (Vector) sms.getData();
				//fak download doar daca nu e trimis de mine
				if (!((String) v.elementAt(5)).equals(this.nick))
					r_get_file(v);
				break;
			case SC.SYS_Send_File_Ok:
				i_log("<-SYS_Send_File_Ok ");
				v = (Vector) sms.getData();
				String server = (String) v.elementAt(0);
				String port = (String) v.elementAt(1);
				String file = (String) v.elementAt(2);
				String group = (String) v.elementAt(3);
				r_send_file(server, Integer.parseInt(port), file, group);
				break;
				//pentru a afla cine urmareste broadcastul meu
			case SC.SYS_Video_Watching:
				i_log("<-SYS_Video_Watching");
				v = (Vector) sms.getData();
				if (((String) v.elementAt(0)).equals(this.nick))
					r_video_watching((String) v.elementAt(1), !v.elementAt(2).equals("0"));
				break;
				//end by seKurea
			case SC.SYS_Video_Password:
				v = (Vector) sms.getData();
				String pass = (String) v.elementAt(0);
				String host = (String) v.elementAt(1);
				i_log("<-SYS_Video_Passwdord " + pass + " on host " + host);
				if (videothread != null)
					videothread.videoOff();
				videothread = new VideoThread(this, host, pass);
				videothread.start();
				break;
			case SC.SYS_Audio_Password:
				v = (Vector) sms.getData();
				String pass1 = (String) v.elementAt(0);
				String host1 = (String) v.elementAt(1);
				i_log("<-SYS_Audio_Password " + pass1 + " on host " + host1);
				if (audiothread != null) {
					//audiothread.stop();
					audiothread.audioOff();
				}
				audiothread = new AudioThread(this, host1, pass1);
				break;
				//case added by seKurea
			case SC.SYS_Del_Video_User:
			case SC.SYS_Alt_Del_Video_User:
				nick = (String) sms.getUser();
				i_log("<-SYS_Del_Video_User from server(nick: " + nick + ").");
				r_remove_video_user(nick, 0);
				break;
				//end of block by seKurea
			default:
				if (sms.getMsgId() >= 1000) {
					i_log("UserDefinedSystemMessage from server, id " + (sms.getMsgId() - 1000));
					r_system_user_message(sms.getMsgId() - 1000, sms.getData());
					break;
				} else {
					i_log("Unhandled system message, id is " + sms.getMsgId());
					break;
				}
		}//switch
	}//doSysMessage

	private void doGroupMessage(GRPMessage gpm) {
		GroupObject go = (GroupObject) gpm.getData();
		String group = go.getGroup();

		if (grouplisteners == null)
			i_log("-1-");
		Vector v = (Vector) grouplisteners.get(group);
		if (v == null)   //no listener registered for this message.
			return;
		String user = gpm.getUser();
		int i = 0;
		hGroup hg = null;
		switch (gpm.getMsgId()) {
			case SC.GRP_User_Join:
				i_log("[client] <-GRP_User_Join");
				statistics.add(lastMessageSize, Statistics.DOWN_SYSTEM);
				for (i = 0; i < v.size(); i++) {
					hg = (hGroup) v.elementAt(i);
					hg.g_user_join(group, user);
				}
				break;
			case SC.GRP_User_Leave:
				i_log("[client] <-GRP_User_Leave ");
				statistics.add(lastMessageSize, Statistics.DOWN_SYSTEM);
				for (i = 0; i < v.size(); i++) {
					hg = (hGroup) v.elementAt(i);
					hg.g_user_leave(group, go.getText());
				}
				break;
			case SC.GRP_Text_Message:
				i_log("[client] <-GRP_Text_Message");
				statistics.add(lastMessageSize, Statistics.DOWN_CHAT);
				String text = go.getText();
				for (i = 0; i < v.size(); i++) {
					hg = (hGroup) v.elementAt(i);
					hg.g_receive_text(group, user, text);
				}
				break;
			case SC.GRP_Lock_Screen_Ok:
				i_log("[client] <-GRP_Lock_Screen_Ok ");
				statistics.add(lastMessageSize, Statistics.DOWN_SYSTEM);
				for (i = 0; i < v.size(); i++) {
					hg = (hGroup) v.elementAt(i);
					hg.g_receive_lock_whiteboard(group, user, 1);
				}
				break;
			case SC.GRP_Unlock_Screen_Ok:
				i_log("[client] <-GRP_Unlock_Screen_Ok ");
				statistics.add(lastMessageSize, Statistics.DOWN_SYSTEM);
				for (i = 0; i < v.size(); i++) {
					hg = (hGroup) v.elementAt(i);
					hg.g_receive_lock_whiteboard(group, user, 0);
				}
				break;
			case SC.GRP_Video_On_Ok:
				i_log("[client] <-SC.GRP_Video_On_Ok");
				statistics.add(lastMessageSize, Statistics.DOWN_SYSTEM);
				Vector v1 = (Vector) go.getObject();
				user = (String) v1.elementAt(0);
				for (i = 0; i < v.size(); i++) {
					hg = (hGroup) v.elementAt(i);
					if (user.equals(nick)) {
						hg.g_receive_set_video(group, 1);
					} else {//informam aplicatia ca utilizatorul a pornit modulul video pe un anumit grup
						hg.g_user_change_video_state(group, user, SC.WATCH_ON);
					}
				}
				i_log("Video on user is " + user);
				break;
			case SC.GRP_Video_Off_Ok:
				i_log("[client] <-SC.GRP_Video_Off_Ok");
				statistics.add(lastMessageSize, Statistics.DOWN_SYSTEM);
				Vector v2 = (Vector) go.getObject();
				user = (String) v2.elementAt(0);
				for (i = 0; i < v.size(); i++) {
					hg = (hGroup) v.elementAt(i);
					if (user.equals(nick)) {
						hg.g_receive_set_video(group, 0);
					} else {//informam aplicatia ca utilizatorul a pornit modulul video pe un anumit grup
						hg.g_user_change_video_state(group, user, SC.WATCH_OFF);
					}
				}
				i_log("Video off user is " + user);
				break;
			case SC.GRP_Audio_On_Ok:
				i_log("[client]<-SC.GRP_Audio_On_Ok");
				statistics.add(lastMessageSize, Statistics.DOWN_SYSTEM);
				user = (String) go.getText();
				i_log("User is " + user);
				user = (String) go.getText();
				if (user == null)
					return;
				if (user.equals(nick)) {
					for (i = 0; i < v.size(); i++) {
						hg = (hGroup) v.elementAt(i);
						hg.g_receive_set_audio(group, 1);
					}
				}
				i_log("User is " + user);
				break;
			case SC.GRP_Audio_Off_Ok:
				i_log("<-SC.GRP_Audio_Off_Ok");
				statistics.add(lastMessageSize, Statistics.DOWN_SYSTEM);
				user = (String) go.getText();
				i_log("Audio off user is " + user);
				if (user.equals(nick)) {
					for (i = 0; i < v.size(); i++) {
						hg = (hGroup) v.elementAt(i);
						hg.g_receive_set_audio(group, 0);
					}
				}
				break;
			case SC.GRP_ESSCommand:
			case SC.GRP_ESSStatus:
				if (gpm.getMsgId() == SC.GRP_ESSStatus)
					i_log("<-SC.GRP_ESSStatus ");
				else
					i_log("<-SC.GRP_ESSCommand ");
				statistics.add(lastMessageSize, Statistics.DOWN_SYSTEM);
				String essc = go.getText();
				for (i = 0; i < v.size(); i++) {
					hg = (hGroup) v.elementAt(i);
					hg.g_receive_user_event(group, user, gpm.getMsgId(), essc);
				}//for
				break;
			case -1:
				//must change it to a valid id.
				i_log("<-got user groups ");
				statistics.add(lastMessageSize, Statistics.DOWN_SYSTEM);
				Vector vu = (Vector) go.getObject();
				for (i = 0; i < vu.size(); i++) {
					String username = (String) vu.elementAt(i);
					for (int j = 0; j < v.size(); j++) {
						hg = (hGroup) v.elementAt(j);
						hg.g_user_join(group, username);
					}//for
				}//for
				break;
			default:
				//messaje whiteboard
				if (gpm.getMsgId() >= SC.GRP_Clear && gpm.getMsgId() < SC.GRP_Last_Grp_Message) {
					int id = gpm.getMsgId();
					i_log("<-Whiteboard message id " + id);
					statistics.add(lastMessageSize, Statistics.DOWN_WHITEBOARD);
					//by Qurtach: AM HOTARAT SA LAS SA TREACA TOATE MESAJELE
					Rect r = new Rect();
					r.x1 = go.getX1();
					r.y1 = go.getY1();
					r.x2 = go.getX2();
					r.y2 = go.getY2();
					for (int j = 0; j < v.size(); j++) {
						hg = (hGroup) v.elementAt(j);
						hg.g_receive_whiteboard_message(group, user, id, r, go.getColor(), go.getWidth(), go.getText(), go.getObject());
					}
				}
				//alte mesaje user-defined
				else {
					i_log("<- User_Defined_Group_Message");
					statistics.add(lastMessageSize, Statistics.DOWN_SYSTEM);
					Object info = go.getObject();
					for (i = 0; i < v.size(); i++) {
						hg = (hGroup) v.elementAt(i);
						hg.g_receive_user_event(group, user, gpm.getMsgId(), info);
					}//for
				}
		}
	}//doGroupMessage

	public final void g_send_text(String group, String text) {
		i_log("->GRP_Text_Message");
		GroupObject go = new GroupObject(group, text);
		GRPMessage gpm = new GRPMessage(SC.GRP_Text_Message, go);
		try {
			int amount = addMessage(gpm);
			statistics.add(amount, Statistics.UP_CHAT);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while sending text message", errorcode);
			ex.printStackTrace();
		}
	}

	public final void g_send_ESS_message(String group, String text) {
		i_log("->GRP_ESS_Message");
		GroupObject go = new GroupObject(group, text);
		GRPMessage gpm = new GRPMessage(SC.GRP_ESSCommand, go);
		try {
			int amount = addMessage(gpm);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while sending ESS message", errorcode);
			ex.printStackTrace();
		}
	}

	public final void g_send_lock_whiteboard(String group) {
		i_log("->GRP_Lock_Whiteboard");
		GroupObject go = new GroupObject(group);
		GRPMessage gpm = new GRPMessage(SC.GRP_Lock_Screen, go);
		try {
			int amount = addMessage(gpm);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while sending lock whiteboard message", errorcode);
			ex.printStackTrace();
		}
	}

	public final void g_send_unlock_whiteboard(String group) {
		i_log("->GRP_UNLock_Whiteboard");
		GroupObject go = new GroupObject(group);
		GRPMessage gpm = new GRPMessage(SC.GRP_Unlock_Screen, go);
		try {
			int amount = addMessage(gpm);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while unlock whiteboard message", errorcode);
			ex.printStackTrace();
		}
	}

	public final void g_send_whiteboard_message(String groupname, int type, Rect rect, Color color, int thickness, String text, Object freehand) {
		i_log("->WhiteboardMessage id " + type);
		GroupObject go = new GroupObject(groupname, rect.x1, rect.y1, rect.x2, rect.y2, color, thickness);
		go.setText(text);
		go.setObject(freehand);
		GRPMessage gpm = new GRPMessage(type, go);
		try {
			int amount = addMessage(gpm);
			statistics.add(amount, Statistics.UP_WHITEBOARD);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while sending whiteboard message", errorcode);
			ex.printStackTrace();
		}
	}

	public final void g_send_user_event(String group, int id, Object data) {
		i_log("->GRP_User_Event "+ group);
		GroupObject go = new GroupObject(group);
		go.setObject(data);
		GRPMessage gpm = new GRPMessage(id, go);
		try {
			int amount = addMessage(gpm);
			statistics.add(amount, Statistics.UP_SYSTEM);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while sending user event", errorcode);
			ex.printStackTrace();
		}
	}

	public void run() {
		try {
			errorcode = -1;
			socket = new Socket(host, port);
			i_log("Sending client version...");
			socket.getOutputStream().write(version.getBytes());
			statistics.add(version.length(), Statistics.UP_SYSTEM);
			i_log("Creating Object writer and reader...");
			msgin = new MessageReader(socket);
			msgout = new MessageSender(socket);
			SYSMessage sms = (SYSMessage) nextMessage();
			statistics.add(lastMessageSize, Statistics.DOWN_SYSTEM);
			if (sms.getMsgId() != SC.SYS_Connection_Ok) {
				if (sms.getData() == null) {
					r_communication_error(null, errorcode);
				} else if (sms.getData() instanceof ERRMessage) {
					((ERRMessage) sms.getData()).getErrId();
				}
			}
			//add here connect sequence
			sms = null;
			sms = new SYSMessage(SC.SYS_My_Id_Is, nick + ":" + pass + ":" + name + ":" + desc);
			int amount = addMessage(sms);
			statistics.add(amount, Statistics.UP_SYSTEM);
			Message msg = nextMessage();
			if (msg instanceof ERRMessage)
				throw new Exception(msg.getData().toString());
			sms = (SYSMessage) msg;
			statistics.add(lastMessageSize, Statistics.DOWN_SYSTEM);
			if (sms.getMsgId() == SC.SYS_Identification_Ok) {
				i_log("Successfully connected to server.");
				connected = true;
				errorcode = 0;
				r_connected(nick, ""); //rigths is not sent by the server right now
			}
			if (sms.getMsgId() == SC.SYS_Nick_Changed) {
				this.nick = (String) sms.getData();
				i_log("Successfully connected to server, nick changed to " + nick);
				connected = true;
				errorcode = 0;
				r_connected(this.nick, "");//rigths is not sent by the server right now
			}
		} catch (Exception ex) {
			//there was a communication error;
			errorcode = 1;
			i_log("Unable to connect to server. Error is " + ex.getMessage());
			connected = false;
			r_communication_error("Unable to connect to server: " + ex.getMessage(), errorcode);
			s_disconnect();
			return;
		}

		//parse each next message.
		while (!stopp) {
			Message msg = null;
			try {
				IntHolder size = new IntHolder();
				msg = msgin.nextMessage(size);
				lastMessageSize = size.getValue();
			} catch (Exception ex) {
				//ex.printStackTrace();
				errorcode = -3;
				r_communication_error("Error while receiving message", errorcode);
				break;
			}
			if (msg.getType().equals("system")) {
				statistics.add(lastMessageSize, Statistics.DOWN_SYSTEM);
				doSysMessage((SYSMessage) msg);
			}
			if (msg.getType().equals("group"))
				doGroupMessage((GRPMessage) msg);
		}//while
	}//run

	//some of this methods are called by hGroup.

	//the actual version of server does not send the user list. we must ask it.
	//It SHOULD BE (and WILL BE) a group message for getting the users from a group
	//after a join. Right now just a shortcut.
	protected void iadd_video_user(String nick) {
		r_add_video_user(nick, 0);
	}

	private void ir_get_users_from_group(String groupname, Vector users) {
		GroupObject go = new GroupObject(groupname);
		go.setObject(users);
		GRPMessage gpm = new GRPMessage(-1, go);
		doGroupMessage(gpm);
		r_get_users_from_group(groupname, users);//call the implicit method.
	}

	public final String getNick() {
		return nick;
	}

	public final int getServerPort() {
		return port;
	}

	public final String getServerHost() {
		return host;
	}

	public final void videoOn(String group) {
		i_log("[client] ->videoOn( " + group + ")");
		GroupObject go = new GroupObject(group);
		GRPMessage gpm = new GRPMessage(SC.GRP_Video_On, go);
		try {
			addMessage(gpm);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while sending video on messasge", errorcode);
			ex.printStackTrace();
		}
		//s_set_band_out( 20000 );
	}

	public final void audioOn(String group) {
		i_log("[client] ->audioOn " + group);
		GroupObject go = new GroupObject(group);
		GRPMessage gpm = new GRPMessage(SC.GRP_Audio_On, go);
		try {
			addMessage(gpm);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while sending audio on messasge", errorcode);
			ex.printStackTrace();
		}
		//s_set_band_out( 10000 );
	}

	public final void videoOff(String group) {
		i_log("[client] ->videoOff " + group);
		GroupObject go = new GroupObject(group);
		GRPMessage gpm = new GRPMessage(SC.GRP_Video_Off, go);
		try {
			addMessage(gpm);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while sending video off messasge", errorcode);
			ex.printStackTrace();
		}
	}

	public final void audioOff(String group) {
		i_log("[client] audioOff " + group);
		GroupObject go = new GroupObject(group);
		GRPMessage gpm = new GRPMessage(SC.GRP_Audio_Off, go);
		try {
			addMessage(gpm);
		} catch (Exception ex) {
			errorcode = -2;
			r_communication_error("Error while sending audio off messasge", errorcode);
			ex.printStackTrace();
		}
	}

	public void dispatchAudioFrame(AudioFrame af) {
//        //i_log( "Dispatching audio frame ");
//        Enumeration e = audiolisteners.elements();
//        while( e.hasMoreElements() ){
//        //int i=0; i<audiolisteners.size(); i++ ){
//            hAudioListener al=(hAudioListener) e.nextElement();
//            al.play_audio_frame( af );
//        }//for
	}//dispatchAudioFrame

	public Vector getAudioListeners() {
		return videothread.getAudioListeners();
	}
}//HermixApi
