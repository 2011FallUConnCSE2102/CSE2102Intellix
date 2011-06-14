/**
 * Class that abstractize a project
 * User: Administrator
 * @Author: qurtach@intellisoft.ro
 * @Date: May 9, 2002
 * @Time: 4:46:51 PM
 */

package ro.intellisoft.intelliX;

import jeditor.JavaContext;
import jeditor.JavaEditorKit;
import jeditor.Token;
import ro.intellisoft.intelliX.UI.GeneralEditorPane;
import ro.intellisoft.intelliX.UI.OpenFilesDialog;

import javax.swing.*;
import javax.swing.text.EditorKit;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class IntelliXProperties {
	protected IntelliX IDE = null;

	/**Stores opened files and current files. These may or may not be contained in rootPaths!*/
	protected Vector openFiles = new Vector();

	/**Stores all known Hermix servers*/
	protected String[] hermixServers = {};

	/**Stores all known Hermix ports*/
	protected String[] hermixPorts = {"10000"};

	/**Stores user's default info for Hermix connection*/
	protected String[] userInfo = {"", "", "", ""};

	/**Stores all the highlights for the known file types*/
	protected Hashtable highlight = new Hashtable();

	protected String shareFolder = "$hare$";

	private int unnamedFiles = 0;

	private static final int DEAULT_FONT_SIZE = 12;
	private static final String DEAULT_FONT_FACE = "Courier New";
	private static final Color DEFAULT_COLOR_COMMENT = new Color(132, 130, 132);
	private static final Color DEFAULT_COLOR_KEYWORD = new Color(0, 0, 132);
	private static final Color DEFAULT_COLOR_STRING = new Color(132, 0, 0);
	private static final Color DEFAULT_COLOR_DEFAULT = new Color(0, 0, 0);
	private static final Color DEFAULT_COLOR_BACKGROUND = new Color(255, 255, 255);

	/**this is a hashtable that contains for every file in the project a
	 * symbol table with all symbols.*/
	private Hashtable symbolTables = new Hashtable();

	public void updateUserInfo(String[] newData){
		userInfo = newData;
	}

	/**returns thefolder where the shares are stored.*/
	public String getShareFolder() {
		return shareFolder;
	}

	public void addHermixServer(String server) {
		for (int i = 0; i < hermixServers.length; i++) {
			if (hermixServers[i].equals(server)) {
				//server already in list
				return;
			}
		}
		//server not in list!
		String[] oldServerList = hermixServers;
		hermixServers = new String[oldServerList.length + 1];
		hermixServers[0] = server;
		for (int i = 0; i < oldServerList.length; i++) {
			hermixServers[i + 1] = oldServerList[i];
		}
	}

	/**opens in editors all the files that we read about... using another thread*/
	public void openFiles(final OpenFilesDialog openFilesDialog) {
		new Thread() {
			public void run() {
				for (int i = 0; i < openFiles.size(); i++) {
					String filename = (String) openFiles.elementAt(i);
					openFilesDialog.setFile(filename);
					String fileType = GeneralEditorPane.getTypeByExtension(filename);
					GeneralEditorPane newEditor = new GeneralEditorPane(IDE, fileType, filename);
					IDE.addEditorPane(newEditor);
				}
				openFilesDialog.end();
			}
		}.start();
	}

	/** Default constructor */
	public IntelliXProperties(IntelliX parent) {
		this.IDE = parent;

		try {
			// read properties from registry
			Preferences pref = Preferences.userRoot();
			//if node does not exists (first time???)
			pref = pref.node("IntelliX");
			readServerProps(pref.node("server"));
			readUserProps(pref.node("user"));
			readOpenFilesProps(pref.node("openfiles"));
		} catch (BackingStoreException e) {
		}
	}


	private void readServerProps(Preferences pref) throws BackingStoreException {
		String[] keys = pref.keys();
		String[] servList = new String[keys.length];
		for (int i = 0; i < keys.length; i++) {
			servList[i] = pref.get(keys[i], "");
		}
		this.hermixServers = servList;
	}

	private void saveServerProps(Preferences pref) throws BackingStoreException {
		pref.clear();
		for (int i = 0; i < hermixServers.length; i++) {
			pref.put("s" + i, hermixServers[i]);
		}
	}

	private void readUserProps(Preferences pref) throws BackingStoreException {
		userInfo[0] = pref.get("nick", "guest");
		userInfo[1] = pref.get("pass", "guest");
		userInfo[2] = pref.get("desc", "");
		userInfo[3] = pref.get("realName", "");
	}

	private void saveUserProps(Preferences pref) throws BackingStoreException {
		pref.put("nick", userInfo[0]);
		pref.put("pass", userInfo[1]);
		pref.put("desc", userInfo[2]);
		pref.put("realName", userInfo[3]);
	}

	private void readOpenFilesProps(Preferences pref) throws BackingStoreException {
		String[] keys = pref.keys();
		String[] fileList = new String[keys.length];
		for (int i = 0; i < keys.length; i++) {
			openFiles.add(pref.get(keys[i], ""));
		}
	}

	private void saveOpenFilesProps(Preferences pref) throws BackingStoreException {
		pref.clear();
		for (int i = 0; i < openFiles.size(); i++) {
			pref.put("f" + i, (String)openFiles.elementAt(i));
		}
	}

	public EditorKit getEditorKit(String defaultHighlight, JEditorPane newEditor) {
		if (defaultHighlight.equals("java")) {
			//set up default UI:
			JavaEditorKit kit = new JavaEditorKit();
			Color commentColor = DEFAULT_COLOR_COMMENT;
			Color keywordColor = DEFAULT_COLOR_KEYWORD;
			Color stringColor = DEFAULT_COLOR_STRING;
			Color defaultColor = DEFAULT_COLOR_DEFAULT;
			Color backgroundColor = DEFAULT_COLOR_BACKGROUND;
			String fontFace = DEAULT_FONT_FACE;
			int fontSize = DEAULT_FONT_SIZE;
			//set up UI if readed:
			if (highlight.get("file-type:java") != null) {
				Hashtable javaHighlight = (Hashtable) highlight.get("file-type:java");
				if (javaHighlight.get("default") != null) {
					defaultColor = (Color) javaHighlight.get("default");
				}
				if (javaHighlight.get("comment") != null) {
					commentColor = (Color) javaHighlight.get("comment");
				}
				if (javaHighlight.get("keyword") != null) {
					keywordColor = (Color) javaHighlight.get("keyword");
				}
				if (javaHighlight.get("string") != null) {
					stringColor = (Color) javaHighlight.get("string");
				}
				if (javaHighlight.get("background") != null) {
					backgroundColor = (Color) javaHighlight.get("background");
				}
				if (javaHighlight.get("font-face") != null) {
					fontFace = javaHighlight.get("font-face").toString();
				}
				if (javaHighlight.get("font-size") != null) {
					fontSize = Integer.parseInt(javaHighlight.get("font-size").toString());
				}
			}
			newEditor.setEditorKitForContentType("text/java", kit);
			newEditor.setContentType("text/java");
			newEditor.setBackground(backgroundColor);
			newEditor.setFont(new Font(fontFace, 0, fontSize));
			newEditor.setForeground(defaultColor);
			newEditor.setEditable(true);
			newEditor.setDragEnabled(true);
			JavaContext styles = kit.getStylePreferences();
			Style s;
			s = styles.getStyleForScanValue(Token.IDENT.getScanValue());
			StyleConstants.setForeground(s, defaultColor);
			s = styles.getStyleForScanValue(Token.COMMENT.getScanValue());
			StyleConstants.setForeground(s, commentColor);
			StyleConstants.setItalic(s, true);
			s = styles.getStyleForScanValue(Token.STRINGVAL.getScanValue());
			StyleConstants.setForeground(s, stringColor);
			for (int code = 70; code <= 130; code++) {
				s = styles.getStyleForScanValue(code);
				if (s != null) {
					StyleConstants.setForeground(s, keywordColor);
					StyleConstants.setBold(s, true);
				}
			}
			s = styles.getStyleForScanValue(Token.NEW.getScanValue());
			StyleConstants.setForeground(s, keywordColor);
			StyleConstants.setBold(s, true);
			s = styles.getStyleForScanValue(Token.GOTO.getScanValue());
			StyleConstants.setForeground(s, Color.red);
			StyleConstants.setBold(s, true);
			s = styles.getStyleForScanValue(Token.INSTANCEOF.getScanValue());
			StyleConstants.setForeground(s, keywordColor);
			StyleConstants.setBold(s, true);
			s = styles.getStyleForScanValue(Token.THROWS.getScanValue());
			StyleConstants.setForeground(s, keywordColor);
			StyleConstants.setBold(s, true);

			s = styles.getStyleForScanValue(Token.STRINGVAL.getScanValue());
			StyleConstants.setForeground(s, stringColor);
			newEditor.getDocument().putProperty(PlainDocument.tabSizeAttribute, new Integer(4));
			return kit;
		} else if (highlight.get("file-type:" + defaultHighlight.toLowerCase()) != null) {
			Hashtable currentHighlight = (Hashtable) highlight.get("file-type:" + defaultHighlight.toLowerCase());
			Color defaultColor = DEFAULT_COLOR_DEFAULT;
			Color backgroundColor = DEFAULT_COLOR_BACKGROUND;
			String fontFace = DEAULT_FONT_FACE;
			int fontSize = DEAULT_FONT_SIZE;
			if (currentHighlight.get("default") != null) {
				defaultColor = (Color) currentHighlight.get("default");
			}
			if (currentHighlight.get("background") != null) {
				backgroundColor = (Color) currentHighlight.get("background");
			}
			if (currentHighlight.get("font-face") != null) {
				fontFace = currentHighlight.get("font-face").toString();
			}
			if (currentHighlight.get("font-size") != null) {
				fontSize = Integer.parseInt(currentHighlight.get("font-size").toString());
			}
			newEditor.setBackground(backgroundColor);
			newEditor.setFont(new Font(fontFace, Font.PLAIN, fontSize));
			newEditor.setForeground(defaultColor);
			return newEditor.getEditorKit();
		} else {
			newEditor.setBackground(DEFAULT_COLOR_BACKGROUND);
			newEditor.setFont(new Font(DEAULT_FONT_FACE, Font.PLAIN, DEAULT_FONT_SIZE));
			newEditor.setForeground(DEFAULT_COLOR_DEFAULT);
			return newEditor.getEditorKit();
		}
	}

	public void removeAllOpenFiles() {
		openFiles.clear();
	}

	public void addFileOpen(String filename) {
		if (filename.indexOf(getShareFolder())==-1){
			openFiles.add(filename);
		}//else do not remember share files
	}

	public void removeFileOpen(String filename) {
		openFiles.remove(filename);
	}

	/**counts unn*/
	public String getNextUnnamedFilename() {
		unnamedFiles++;
		return "(noname" + unnamedFiles + ")";
	}

	public Hashtable getSymbolTables() {
		return symbolTables;
	}

	public void save() {
		try {
			// write properties to registry
			Preferences pref = Preferences.userRoot();
			//if node does not exists (first time???)
			pref = pref.node("IntelliX");
			saveServerProps(pref.node("server"));
			saveUserProps(pref.node("user"));
			saveOpenFilesProps(pref.node("openfiles"));
		} catch (BackingStoreException e) {
		}

	}

}//end of IntelliXProperties
