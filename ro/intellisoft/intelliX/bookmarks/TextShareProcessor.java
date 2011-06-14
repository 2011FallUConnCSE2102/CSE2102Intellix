/**
 * User: Administrator
 * @Author: qurtach@intellisoft.ro
 * @Date: Jun 13, 2002
 * @Time: 1:01:16 PM
 */

package ro.intellisoft.intelliX.bookmarks;

import ro.intellisoft.intelliX.UI.GeneralEditorPane;
import ro.intellisoft.intelliX.UI.StatusBarProgressBar;
import ro.intellisoft.intelliX.HermixLink;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector;
import java.util.Hashtable;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.File;

import com.hermix.SC;

public class TextShareProcessor implements ActionListener{
	private GeneralEditorPane editor = null;
	private HermixLink hermixLink = null;
	private String group = null;
	private String moderator = null;
	private String RWOwner = null;
	private Timer t = null;
	private int tickCounter = 0;
	private int bookmarkUpdatesCounter = 0;
	private int bookmarkUpdatesSegment = 0;
	private Timer t_updateDemon = null;//only for the moderator/user with write right
	private boolean writeLockAquired = false;

	private static final String SET_FILE_SHARING_TEXT = "Set file sharing text";
	private static final String NEW_BOOKMARK_LINE = "Add new bookmark line";
	private static final String NEW_BOOKMARK_LINE_AND_OPEN = "Add new bookmark line and open";
	private static final String REMOVE_BOOKMARK_LINE = "Remove bookmark from line";
	private static final String NEW_BOOKMARK_COMPONENT = "Add new bookmark component at line";
	private static final String UPDATE_FILE_SHARING_TEXT = "Update file sharing text";
	private static final String UPDATE_TEXT_BOOKMARK_COMPONENT = "Update text bookmark component";
	private static final String UPDATE_WHITEBOARD_BOOKMARK_COMPONENT = "Update whiteboard bookmark component";
	private static final String UPDATE_BOOKMARK_LINES = "Update bookmark lines";
	private static final String WRITE_ACCES_REQUEST = "Read acces request";
	private static final String WRITE_ACCES_GRANTED = "Read acces granted";
	private static final String STOP_SHARE = "Stop share";

    public static TextShareProcessor createTextShareProcessor(GeneralEditorPane editor, HermixLink hermixLink){
		String group = "psh" + (int)(System.currentTimeMillis()%Integer.MAX_VALUE);
		CreatePrivateGroupDialog privateGroupDialog = new CreatePrivateGroupDialog(editor.getIDE().getMainFrame(), hermixLink.getUsers());
		privateGroupDialog.show();
		if (privateGroupDialog.isCancelPresed()){
			return null;
		} else {
			return new TextShareProcessor(editor, hermixLink, group, privateGroupDialog.getSelectedUsers());
		}
    }

	/**constructor for the moderator*/
	private TextShareProcessor(GeneralEditorPane editor, HermixLink hermixLink, String group, Vector users) {
		//start progressBar:
		editor.getIDE().getProgressBar().setValue(0);
		this.editor = editor;
		this.hermixLink = hermixLink;
		this.writeLockAquired = true;
		this.group = group;
		this.RWOwner = hermixLink.getNick();
		hermixLink.s_create_private_group(group, users);
		hermixLink.registerShareProcessor(group, this);
		t_updateDemon = new Timer(5000, this);
//		t_updateDemon.start();
	}

	/**constructor for the others*/
	public TextShareProcessor(GeneralEditorPane editor, HermixLink hermixLink, String moderator, String group) {
		this.editor = editor;
		this.hermixLink = hermixLink;
		this.group = group;
		this.moderator = moderator;
		this.writeLockAquired = false;
		this.RWOwner = moderator;
	}

	public boolean isWriteLockAquired(){
		return this.writeLockAquired;
	}

	public JFrame getParentFrame(){
		return editor.getIDE().getMainFrame();
	}

	public String getModerator(){
		return moderator;
	}

	public void r_join_group(){
		if (moderator != null){
			return;
		}
		//incepand de acum ar trebui sa astept cateva sec ca sa pot sa trimit infos
		t = new Timer(1000, this);
		//t.setRepeats(false);
		t.start();
		editor.getIDE().log("Sending file share to other users...");
	}

	public void g_user_join(String nick){
		System.out.println("new nick in text share = " + nick);
	}

	public void g_user_leave(String nick){
		System.out.println("nick out from text share = " + nick);
		if (nick.equals(RWOwner)){
			System.out.println("RW Owner has went out.");
			if (moderator == null){
				System.out.println("Taking the RW right...");
				RWOwner = hermixLink.getNick();
				editor.aquireBookmarkWriteLock();
			}
		}
	}

	public void handleBookmarkEvent(TextBookmark tb, int line, int idx){
		Vector objectToSend = new Vector();
		objectToSend.addElement(UPDATE_TEXT_BOOKMARK_COMPONENT);
		objectToSend.addElement(new Integer(line));
		objectToSend.addElement(new Integer(idx));
		objectToSend.addElement(tb.getText());
		hermixLink.g_send_user_event(group, SC.GRP_Text_Share_Command, objectToSend);
	}

	public void handleBookmarkEvent(WhiteboardBookmark tb, int line, int idx, ro.intellisoft.whiteboard.shapes.Figure f){
		Vector objectToSend = new Vector();
		objectToSend.addElement(UPDATE_WHITEBOARD_BOOKMARK_COMPONENT);
		objectToSend.addElement(new Integer(line));
		objectToSend.addElement(new Integer(idx));
		objectToSend.addElement(f.toVector());
		hermixLink.g_send_user_event(group, SC.GRP_Text_Share_Command, objectToSend);
	}

	public void requestRWright(){
		Vector objectToSend = new Vector();
		objectToSend.addElement(WRITE_ACCES_REQUEST);
		hermixLink.g_send_user_event(group, SC.GRP_Text_Share_Command, objectToSend);
	}

	public void stopSharing(){
		Vector objectToSend = new Vector();
		objectToSend.addElement(STOP_SHARE);
		hermixLink.g_send_user_event(group, SC.GRP_Text_Share_Command, objectToSend);
		hermixLink.s_leave_group(group);
	}

	public void exitShare(){
		if (moderator != null && writeLockAquired){
			acceptWriteAccessRequest(moderator);
		}
		hermixLink.unregisterShareProcessor(group);
		hermixLink.s_leave_group(group);
	}

	public void process(String nick, int id, Object data){
		Vector objReceived = (Vector)data;
		//	/*for debugging only!*/
		//	System.out.println("******************objReceived: "+objReceived.size()+" ***************");
		//	System.out.println("Category:(0) = " + objReceived.elementAt(0));
		//	for (int i = 0; i<objReceived.size(); i++){
		//		System.out.println(" "+i +"):" + objReceived.elementAt(i).getClass().getName());
		//	}
		//	/*for debugging only!*/
		if (moderator==null){
			//if I am the moderator:
			if (objReceived.elementAt(0).equals(WRITE_ACCES_GRANTED)){
				RWOwner = (String)objReceived.elementAt(1);
			}
			//and go on...
		}
		if (nick.equals(hermixLink.getNick())){
			return;
		}
		if (objReceived.elementAt(0).equals(SET_FILE_SHARING_TEXT)){
			//put this content on the editor:
			editor.setText((String)objReceived.elementAt(2));
			//test if share-path exists and if not, create it:
			java.io.File sharePath = new File(editor.getIDE().getProperties().getShareFolder());
			if (!sharePath.exists()){
				if (!sharePath.mkdir()){
					System.out.println("sharePath could not be created" + sharePath);
				}
			}
			//and save it:
			editor.saveAs(editor.getIDE().getProperties().getShareFolder()+"//"+objReceived.elementAt(1).toString());
			editor.getIDE().updateEditorTab(editor);
			editor.setReadOnly(true);
			editor.setRemoteFile(true);
		} else if (objReceived.elementAt(0).equals(UPDATE_FILE_SHARING_TEXT)){
			//the editor file content has changed, so let's send the content to others
			editor.setText((String)objReceived.elementAt(1));
		} else if (objReceived.elementAt(0).equals(UPDATE_BOOKMARK_LINES)){
			//the editor has changed the line numbers, update them at the remote
			editor.updateLineBookmarks(((Integer)objReceived.elementAt(1)).intValue(), ((Integer)objReceived.elementAt(2)).intValue());
		} else if (objReceived.elementAt(0).equals(REMOVE_BOOKMARK_LINE)){
			//must delete bookmark from line
			editor.removeBookmark(((Integer)objReceived.elementAt(1)).intValue());
		} else if (objReceived.elementAt(0).equals(STOP_SHARE)){
			//close this file editor
			System.out.println("objReceived = " + objReceived);
			hermixLink.s_leave_group(group);
			JOptionPane.showMessageDialog(editor, moderator + " has close this share.", "Closing  "+editor.getFileName()+" text share", JOptionPane.INFORMATION_MESSAGE);
			//each user may write on his bookmarks!!
			editor.aquireBookmarkWriteLock();
			editor.setReadOnly(false);
			editor.removeShareProcessor();
			hermixLink.unregisterShareProcessor(group);
		} else if (objReceived.elementAt(0).equals(NEW_BOOKMARK_COMPONENT)){
			//a new bookmark component has been added:
			BookmarkWindow bw = editor.getBookmarkForLine(((Integer)objReceived.elementAt(1)).intValue());
			if (bw != null){
				bw.createNewComponent((String)objReceived.elementAt(3),((Integer)objReceived.elementAt(2)).intValue());
			}
		} else if (objReceived.elementAt(0).equals(NEW_BOOKMARK_LINE)){
			// when sending the file, send the current bookamrk also:
			String XMLContent = "<?xml version=\"1.0\"?>\n<!-- IntelliX bookmark file --><bookmarks file-reference = \"-\" last-update = \"-\" >" +
					objReceived.elementAt(1) + "</bookmarks>";
			new ro.intellisoft.XML.XMLMiniParser().parse(XMLContent, new BookmarkXMLReader(editor));
		} else if (objReceived.elementAt(0).equals(UPDATE_TEXT_BOOKMARK_COMPONENT)){
			updateBookmark((Integer)objReceived.elementAt(1), (Integer)objReceived.elementAt(2), objReceived.elementAt(3));
		} else if (objReceived.elementAt(0).equals(UPDATE_WHITEBOARD_BOOKMARK_COMPONENT)){
			updateBookmark((Integer)objReceived.elementAt(1), (Integer)objReceived.elementAt(2), objReceived.elementAt(3));
		} else if (objReceived.elementAt(0).equals(WRITE_ACCES_GRANTED)){
			//only the user that requested the right will receive it!
			if (objReceived.elementAt(1).equals(hermixLink.getNick())){
				JOptionPane.showMessageDialog(editor.getIDE().getMainFrame(), "The write-access right has been granted.\nYou can now write on the bookmarks.", editor.getFileName(), JOptionPane.INFORMATION_MESSAGE);
				writeLockAquired = true;
				editor.aquireBookmarkWriteLock();
			}
		} else if (objReceived.elementAt(0).equals(WRITE_ACCES_REQUEST)){
			if (writeLockAquired){
				//ask user for the lock
				int res = JOptionPane.showConfirmDialog(editor.getIDE().getMainFrame(), nick + " has requested the write acces on this file bookmark", editor.getFileName(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (res == JOptionPane.YES_OPTION){
                    acceptWriteAccessRequest(nick);
				}
			}
		}
	}

	private void acceptWriteAccessRequest(String toWho){
		Vector objectToSend = new Vector();
		objectToSend.addElement(WRITE_ACCES_GRANTED);
		objectToSend.addElement(toWho);
		this.writeLockAquired = false;
		int[] lines = editor.getBookmarkLines();
		for (int i = 0; i < lines.length; i++) {
			BookmarkWindow bw = editor.getBookmarkForLine(lines[i]);
			if (bw != null){
				bw.setEditable(false);
			}
		}
		hermixLink.g_send_user_event(group, SC.GRP_Text_Share_Command, objectToSend);
	}

	/**Invoked when an action occurs.*/
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == t){
			tickCounter++;
			editor.getIDE().getProgressBar().setValue(tickCounter*10);
			if (tickCounter != 5){
				//must wait more
				return;
			}
			t.stop();
			t = null; //avoid second appel
			//5sec seconds expired
			Vector objectToSend = new Vector();
			objectToSend.addElement(SET_FILE_SHARING_TEXT);
			objectToSend.addElement(editor.getFileName());
			objectToSend.addElement(editor.getText());
			hermixLink.g_send_user_event(group, SC.GRP_Text_Share_Command, objectToSend);
			if (editor.hasBookmarks()){
				//iterez si trimit cate un mesaj pt fiecare bookmark
				int[] lines = editor.getBookmarkLines();
				if (lines.length == 0){
					editor.getIDE().getProgressBar().setValue(StatusBarProgressBar.RESET);
				} else {
					bookmarkUpdatesCounter = 0;
					bookmarkUpdatesSegment = 40/(lines.length);
					for (int i = 0; i < lines.length; i++) {
						if (editor.getBookmarkForLine(lines[i])==null){
							System.out.println("null?");
							continue;
						}
						System.out.println(">>Sending bookmarks lines");
						sendNewBookmarkWindow(editor.getBookmarkForLine(lines[i]), lines[i]);
					}
				}
				editor.getIDE().getProgressBar().setValue(60);
			}//if (editor.hasBookmarks()
			else {
				editor.getIDE().getProgressBar().setValue(StatusBarProgressBar.RESET);
			}
		}else if (event.getSource() == t_updateDemon){
			if (editor.hasBookmarks()){
				sendUpdateBookmarks();
			}//if (editor.hasBookmarks()
		}
	}//actionPerformed

	/**a new bookmark window has been added to the specified line*/
	public void sendNewBookmarkWindow(BookmarkWindow bw, int line){
		Vector objectToSend = new Vector();
		objectToSend.addElement(NEW_BOOKMARK_LINE);
		objectToSend.addElement(editor.getBookmarkForLine(line).getXMLRepresentation(-1));
		hermixLink.g_send_user_event(group, SC.GRP_Text_Share_Command, objectToSend);
	}

	/**removes a bookmark window from the specified line*/
	public void sendRemoveBookmarkWindow(int line){
		Vector objectToSend = new Vector();
		objectToSend.addElement(REMOVE_BOOKMARK_LINE);
		objectToSend.addElement(new Integer(line));
		hermixLink.g_send_user_event(group, SC.GRP_Text_Share_Command, objectToSend);
	}

	/**a new bookmark component has been added to the specified line*/
	public void sendNewBookmarkComponent(BookmarkComponent bc, int line, int idx){
		Vector objectToSend = new Vector();
		objectToSend.addElement(NEW_BOOKMARK_COMPONENT);
		objectToSend.addElement(new Integer(line));
		objectToSend.addElement(new Integer(idx));
		objectToSend.addElement(bc.getName());
		hermixLink.g_send_user_event(group, SC.GRP_Text_Share_Command, objectToSend);
	}


	private void sendUpdateBookmarks(){
		Vector objectToSend = new Vector();
		//iterez si trimit cate un mesaj pt fiecare bookmark
		int[] lines = editor.getBookmarkLines();
		for (int i = 0; i < lines.length; i++) {
			if (editor.getBookmarkForLine(lines[i]) == null) {
				System.out.println("null?");
				continue;
			}
			objectToSend = new Vector();
			objectToSend.addElement("Update file bookmark");
			objectToSend.addElement(editor.getBookmarkForLine(lines[i]).getXMLRepresentation(-1));
			hermixLink.g_send_user_event(group, SC.GRP_Text_Share_Command, objectToSend);
		}
	}

	public void updateFileContent(){
		if (moderator != null){
			//System.out.println("F********k!.......");
			return;
		}
		Vector objectToSend = new Vector();
		objectToSend.addElement(UPDATE_FILE_SHARING_TEXT);
		objectToSend.addElement(editor.getText());
		hermixLink.g_send_user_event(group, SC.GRP_Text_Share_Command, objectToSend);
	}

	public void updateLineBookmarks(int startingAtLine, int amount){
		if (moderator != null){
			return;
		} else {
			Vector objectToSend = new Vector();
			objectToSend.addElement(UPDATE_BOOKMARK_LINES);
			objectToSend.addElement(new Integer(startingAtLine));
			objectToSend.addElement(new Integer(amount));
			hermixLink.g_send_user_event(group, SC.GRP_Text_Share_Command, objectToSend);
		}
	}

	private void updateBookmark(Integer line, Integer idx, Object newValue){
		BookmarkWindow bw = editor.getBookmarkForLine(line.intValue());
		if (bw != null){
			bw.updateContent(idx.intValue(), newValue);
		}
	}

	public synchronized void countDownWhiteboards(){
		bookmarkUpdatesCounter ++;
		editor.getIDE().getProgressBar().setValue(60+bookmarkUpdatesSegment*bookmarkUpdatesCounter);
		if (60+bookmarkUpdatesSegment*bookmarkUpdatesCounter==100){
			editor.getIDE().getProgressBar().setValue(StatusBarProgressBar.RESET);
			editor.getIDE().log("File share sent to other users...");
		}
	}

}//class TextShareProcessor
