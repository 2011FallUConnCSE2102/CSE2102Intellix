
package com.hermix;

/**
 * Clasa din API care se ocupa cu trimiterea/receptia mesajelor de grup.
 * pentru a trimite mesaje se foloseste de clasa HermixAPI.
 */


import java.awt.*;
import java.io.File;
import java.util.Vector;

public class hGroup {

	private HermixApi parent;

	public hGroup(HermixApi parent) {
		this.parent = parent;
	}

	public final HermixApi getParent() {
		return parent;
	}

	public void g_user_join(String groupname, String nick) {
	}

	public void g_user_leave(String groupname, String nick) {
	}

	public void g_send_text(String groupname, String text) {
		parent.g_send_text(groupname, text);
	}

	public void g_send_ESS_message(String groupname, String text) {
		parent.g_send_ESS_message(groupname, text);
	}

	public void g_receive_text(String groupname, String nick, String text) {
	}

	public void g_send_whiteboard_message(String groupname, int type, Rect rect, Color color, int thickness, String text, Object freehand) {
		parent.g_send_whiteboard_message(groupname, type, rect, color, thickness, text, freehand);
	}

	public void g_receive_whiteboard_message(String groupname, String nick, int type, Rect rect, Color color, int thickness, String text, Object freehand) {
	}

	public void g_send_user_event(String groupname, int id, Object data) {
		parent.g_send_user_event(groupname, id, data);
	}

	public void g_receive_user_event(String groupname, String nick, int id, Object data) {
	}

	public void g_send_lock_whiteboard(String groupname, int state) {
	}

	public void g_receive_lock_whiteboard(String groupname, String nick, int state) {
	}

	public void g_send_set_audio(String groupname, int state) {
		if (state == 0) {
			parent.audioOff(groupname);
		}
		if (state == 1) {
			parent.audioOn(groupname);
		}
	}

	public void g_receive_set_audio(String groupname, int state) {
	}

	public void g_send_set_video(String groupname, int state) {
		if (state == 1) {
			parent.videoOn(groupname);
		} else {
			parent.videoOff(groupname);
		}
	}

	public void g_receive_set_video(String groupname, int state) {
	}

	public void g_send_file(String groupname, File file, int operation, int x, int y) {
	}

	public void g_send_get_file_history(String groupname, int count) {
	}

	public void g_receive_get_file_history(String groupname, String nick, Vector files) {
	}

	public void g_send_get_file(String groupname, String fileid) {
	}

	public void g_send_get_file_info(String groupname, String fileid) {
	}

	public void g_receive_get_file_info(String groupname, String nick, String fileid, Vector fileinfo) {
	}

	public void g_received_file(String group, String nick, String name, String id, Vector info) {
	}

	public void g_cancel_file_transfer(String group, String filename, String fileid) {
	}

	Vector g_get_file_transfers(String group) {
		return null;        //not implemented yet;
	}

	//by Qurtach:
	public void g_user_change_video_state(String group, String user, boolean state) {
	}
}