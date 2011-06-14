
package ro.intellisoft.intelliX;

/*
 * User: Administrator
 * Date: Mar 21, 2002
 * Time: 10:27:54 AM
 */

import com.hermix.HermixApi;
import com.hermix.SC;
import com.hermix.hGroup;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Enumeration;

import ro.intellisoft.intelliX.bookmarks.TextShareProcessor;

public class HermixLink extends HermixApi {
	private IntelliX parent = null;
	private Broadcast broadcast = new Broadcast(this);
	private HermixGroupHandler hg = null;
	private final Hashtable users = new Hashtable();
	private final Hashtable textShareProcessors = new Hashtable();

	private String currentGroup = null;

	public String getCurrentGroup() {
		return currentGroup;
	}

	public IntelliX getIDE() {
		return parent;
	}

	public HermixLink(IntelliX parent) {
		this.parent = parent;
		hg = new HermixGroupHandler(this);
	}

	public void r_change_nick(String aNick, String oldNick) {
		parent.r_change_nick(aNick, oldNick);
		Object u = users.remove(oldNick);
		if (u!=null){
			((User)u).changeNick(aNick);
			users.put(aNick, u);
		}
	}

	public void r_wake_up(String nick) {
		parent.r_wake_up(nick);
	}

	public void r_connected(String newNick, String rights) {
		parent.r_connected(newNick, rights);
		users.clear();
	}

	public void r_communication_error(int code) {
		parent.r_communication_error("", code);
	}

	public void r_communication_error(String errMsg, int code) {
		parent.r_communication_error(errMsg, code);
	}

	public void r_get_users_from_group(String groupname, Vector users) {
		for (int i = 0; i < users.size(); i++)
			parent.r_insert_user(users.elementAt(i).toString());
	}

	public void r_join_group(String groupname) {
		TextShareProcessor processor = (TextShareProcessor)textShareProcessors.get(groupname);
		if (processor != null){
			add_group_listener(groupname, hg);
			//pass this message to the registered processor
			processor.r_join_group();
			return;
		}
		parent.r_join_group(groupname);
		add_group_listener(groupname, hg);
		if (currentGroup != null) {
			//do dot use any other group
			s_leave_group(currentGroup);
			videoOff(groupname);
		}
		currentGroup = groupname;
		videoOn(groupname);
	}

	public void r_invite_join_private_group(String privateGroup, String owner){
		s_join_group(privateGroup);
		parent.joinNewTextShare(privateGroup, owner);
	}

	public void g_user_join(String groupName, String nick) {
		TextShareProcessor processor = (TextShareProcessor)textShareProcessors.get(groupName);
		if (processor != null){
			//pass this message to the registered processor
			processor.g_user_join(nick);
			return;
		}
		if (groupName.equals(currentGroup)){
			parent.r_insert_user(nick);
			if (users.get(nick) != null){
				return;//'cause this user is already in
			}
			if (nick.equals(getNick())){
				users.put(nick, new User(parent, nick, "", ""));
			} else {
				users.put(nick, new User(parent, nick, "N/A", "usual user"));
			}
		}
	}

	public void g_user_leave(String groupName, String nick) {
		TextShareProcessor processor = (TextShareProcessor)textShareProcessors.get(groupName);
		if (processor != null){
			//pass this message to the registered processor
			processor.g_user_leave(nick);
			return;
		}
		if (groupName.equals(currentGroup)){
			parent.r_remove_user(nick);
			users.remove(nick);
		}
	}

	public void g_receive_text(String groupName, String nick, String text) {
		if (groupName.equals(currentGroup))
			parent.g_receive_text(nick, text);
	}

	public void g_send_text(String text) {
		hg.g_send_text(currentGroup, text);
	}

	public void r_add_video_user(String nick, int id) {
		Object u = users.get(nick);
		if (u != null){
			((User)u).updateBroadcastState(true);
			parent.updateUserList();
		} //else throw new UserNotFoundException(nick);
	}

	public void r_remove_video_user(String nick, int id) {
		Object u = users.get(nick);
		if (u != null){
			parent.updateUserList();
			((User)u).updateBroadcastState(false);
		} //else throw new UserNotFoundException(nick);
	}

	/**returns a vector with all current users*/
	public Vector getUsers(){
		Vector v = new Vector();
		Enumeration usrList = users.keys();
		while (usrList.hasMoreElements()) {
			v.addElement(usrList.nextElement());
		}
		return v;
	}


	/**call this to start audio broadcast*/
	public void startBroadcast(){
		broadcast.start();
	}

	/**call this to stop audio broadcast*/
	public void stopBroadcast(){
		broadcast.stopThread();
	}

	public Broadcast getBroadcast() {
		return broadcast;
	}


	public User getUserByName(String name){
		return (User)users.get(name);
	}

	public void removeAllListeners(){
		//delete all wanted audio users
		Enumeration usrSet = users.keys();
		while (usrSet.hasMoreElements()) {
			Object nick = usrSet.nextElement();
			User user = (User) users.get(nick);
			user.enableAudio(false);
		}
		sendNewAudioList();
	}

	public void g_receive_user_message(int id, Object data, String exp) {
		if (id == SC.GRP_Text_Share_Command) {
			try {
				Vector command = (Vector) data;
				int subCommand = ((Integer) command.elementAt(0)).intValue();
				long uid = ((Long) command.elementAt(1)).longValue();
				String dest = (String) command.elementAt(2);
				String text = (String) command.elementAt(3);
			} catch (Exception e) {
				//do nothing if errors apper
				r_communication_error("Text Sharing Error: Invalid arguments", -id);
			}
		}
	}//g_receive_user_message

	/**sends the current list wanted audioUsers*/
	public void sendNewAudioList() {
		Vector unwantedAudioUsers = new Vector();
		Enumeration usrs = users.keys();
		while (usrs.hasMoreElements()) {
			String nick = usrs.nextElement().toString();
			User u = (User) users.get(nick);
			int state = u.getUserStatus();
			if ((state == User.UNWANTED_AUDIO_USER) || (state == User.USER_IS_CURRENT_USER)){
				unwantedAudioUsers.addElement(nick);
			}
		}
		s_deny_video_user(unwantedAudioUsers);
	}//sendNewAudioList

	/**now we could send the message from a private group to a text share processor*/
	public void registerShareProcessor(String group, TextShareProcessor processor){
		textShareProcessors.put(group, processor);
	}

	/**unregister this processor*/
	public void unregisterShareProcessor(String group){
		textShareProcessors.remove(group);
	}

	TextShareProcessor getProcessorForGroup(String group){
		return (TextShareProcessor)textShareProcessors.get(group);
	}

}

class HermixGroupHandler extends hGroup {
	private HermixLink hermixLink;

	public HermixGroupHandler(HermixLink ha) {
		super(ha);
		this.hermixLink = ha;
	}

	public void g_user_join(String groupName, String nick) {
		hermixLink.g_user_join(groupName, nick);
	}

	public void g_user_leave(String groupName, String nick) {
		hermixLink.g_user_leave(groupName, nick);
	}

	public void g_receive_text(String groupname, String nick, String text) {
		hermixLink.g_receive_text(groupname, nick, text);
	}

	public void g_receive_user_event(String groupname, String nick, int id, Object data) {
		TextShareProcessor processor = hermixLink.getProcessorForGroup(groupname);
		if (processor != null){
			processor.process(nick, id, data);
			return;
		}
		hermixLink.g_receive_user_message(id, data, nick);
	}

	public void g_receive_set_video(String groupName, int state) {
		if (state == 1){
			hermixLink.getIDE().log("Audio module started on "+ groupName);
		} else {
			hermixLink.getIDE().log("Audio module stopped on "+ groupName);
			if (hermixLink.getBroadcast() != null)
				hermixLink.getBroadcast().stopThread();
			hermixLink.stopVideoThread();
			hermixLink.removeAllListeners();
			hermixLink.stopBroadcast();
		}
	}//g_receive_set_video
}//HermixGroupHandler
