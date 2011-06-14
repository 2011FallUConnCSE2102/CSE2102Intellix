/**
 * User: Administrator
 * @Author: qurtach@intellisoft.ro
 * @Date: May 15, 2002
 * @Time: 1:09:29 PM
 */

package ro.intellisoft.intelliX.UI;

import JavaParser.TreeClass;
import JavaParser.symtab.SymbolTable;
import ro.intellisoft.intelliX.IntelliX;
import ro.intellisoft.intelliX.bookmarks.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This class is a JScrollPane that contains a JEditorPane.
 * It takes care of all the events that happens in this area.
 */
public class GeneralEditorPane extends JScrollPane implements ActionListener, KeyListener, CaretListener, DocumentListener {

	/**the content of the GeneralEditorPane is a panel with Border Layout*/
	private final JPanel contentPane = new JPanel(new BorderLayout());

	/**the component which displays bookmarks and other things*/
	private final QuickAccessPanel quickAccessPanel = new QuickAccessPanel(this);

	/**the editor that user interact with*/
	private final JEditorPane editor = new JEditorPane();

	/**the editor kit. this may be changed if file is "save as" and change type*/
	private EditorKit editorKit = editor.getEditorKit();

	/**tells if the file from editor was ever saved*/
	private boolean everSaved = false;

	/**tells if the file from editor was altered and needs to be saved*/
	private boolean altered = false;

	/**tells if are there any bookmarks present*/
	private boolean bookmarksPresent = false;

	/**tells if the bookmarks for this file was altered and needs to be saved*/
	private boolean bookmarksAltered = !false;

	/**tells whetever the user has the right to change this file's content*/
	private boolean readOnly = false;

	/**File that the content of the editor has been loaded from*/
	private File file = null;

	/**the type of the edited file. @see highlight, default text, ie. no highlight*/
	private String fileType = "txt";

	/**Temporary name. Unique. Used for new files and/or received from Hermix Server*/
	private String fileNameCandidate = null;

	/**The parent IntelliX IDE*/
	private IntelliX IDE = null;

	/**Colection of bookmarks*/
	private Hashtable bookmarks = new Hashtable();

	/**tells if the file is remote*/
	private boolean remoteFile = false;

	/**the Components that contains the structure view for this component*/
	private SymbolTable st = null;
	private TreeClass tc = null;

	private TextShareProcessor shareProcessor = null;

	/**
	 * A list with all bookmarkwindows opened.
	 * Need this in order to hide them when this Editor loses focus
	 * and show them up when focus is regain...
	 */
	private Vector openBookmarkWindows = new Vector();

	/**meniul care va fi afisat la click dreapta*/
	private JPopupMenu editorMenu = new JPopupMenu("Editor Menu");

	private JMenuItem closeMenu = new JMenuItem("Close");
	private JMenuItem compileMenu = new JMenuItem("Compile");
	private JMenuItem toggleTextShareMenu = new JMenuItem("Share this...");
	private JMenuItem requestWriteShareMenu = new JMenuItem("Request write acces on this share");
	private JMenuItem viewBookmarkMenu = new JMenuItem("View bookmark");
	private JMenuItem addBookmarkMenu = new JMenuItem("Add a bookmark");
	private JMenuItem removeBookmarkMenu = new JMenuItem("Remove this bookmark");

	//undo helpers
	protected UndoAction undoAction;
	protected RedoAction redoAction;
	protected UndoManager undo = new UndoManager();
	protected MyUndoableEditListener myUndoableEditListener = new MyUndoableEditListener();

	/**Constructor - call when I do not know yet the content*/
	public GeneralEditorPane(IntelliX IDE, String defaultHilight) {
		super(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.IDE = IDE;
		editorKit = IDE.getProperties().getEditorKit(defaultHilight, editor);
		fileNameCandidate = IDE.getProperties().getNextUnnamedFilename();
		init();
	}

	/**Constructor*/
	public GeneralEditorPane(IntelliX IDE, String defaultHilight, String pathToFile) {
		super(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.IDE = IDE;
		this.read(pathToFile);
		init();
	}

	/**called by any of the constructors*/
	private void init() {
		super.setViewportView(contentPane);
		contentPane.add(editor, BorderLayout.CENTER);
		contentPane.add(quickAccessPanel, BorderLayout.WEST);
		editor.addCaretListener(this);
		editor.addKeyListener(this);
		editor.getDocument().addDocumentListener(this);

		//set up
		st = (SymbolTable) IDE.getProperties().getSymbolTables().get(getAbsoluteFileName());
		if (st == null) {
			st = new SymbolTable();
			st.setFile(file);
			if (file != null) {
				System.err.println("   " + file.getAbsolutePath());
				try {
					JavaParser.JavaXref.parseFile(new FileInputStream(file), st);
				} catch (Exception e) {
					System.out.println("Exception while parsing java file: " + e);
				}
			}// else file == null ie. new file
			st.resolveTypes();
		}
		DefaultMutableTreeNode strNode = new DefaultMutableTreeNode(this.getFileName());
		st.report(strNode);
		tc = new TreeClass(strNode);
		IDE.addMouseListenersAndRenderer(tc.getTree());

		editor.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					int lastMouseX = e.getX();
					int lastMouseY = e.getY();
					setCaretLine(lastMouseY / 14);
					//tests if there is any bookmark at the current position
					int crtLine = getLineForPosition(editor.getText(), editor.getCaretPosition());
					boolean isBookmarkAtCrtPosition = bookmarks.containsKey(new Integer(crtLine));
					viewBookmarkMenu.setEnabled(isBookmarkAtCrtPosition);
					removeBookmarkMenu.setEnabled(isBookmarkAtCrtPosition && (shareProcessor == null || shareProcessor.isWriteLockAquired()));
					addBookmarkMenu.setEnabled(!isBookmarkAtCrtPosition && (shareProcessor == null || shareProcessor.isWriteLockAquired()));
					if (shareProcessor == null) {
						toggleTextShareMenu.setText("Share this...");
					} else if (shareProcessor.getModerator() == null) {
						//me is the moderator so I can close the sharing...
						toggleTextShareMenu.setText("Stop this share");
					} else {
						//not my share, but I can leave it out
						toggleTextShareMenu.setText("Leave this share");
					}
					if (shareProcessor == null || shareProcessor.isWriteLockAquired()) {
						requestWriteShareMenu.setEnabled(false);
					} else {
						requestWriteShareMenu.setEnabled(true);
					}

					editorMenu.show(e.getComponent(), lastMouseX, lastMouseY);//force to compute menu dimensions
					Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
					if (lastMouseX + editorMenu.getWidth() + e.getComponent().getLocationOnScreen().getX() > d.getWidth()) {
						lastMouseX = (int) d.getWidth() - editorMenu.getWidth() - (int) e.getComponent().getLocationOnScreen().getX();
					}
					if (lastMouseY + editorMenu.getHeight() + e.getComponent().getLocationOnScreen().getY() > d.getHeight()) {
						lastMouseY = (int) d.getHeight() - editorMenu.getHeight() - (int) e.getComponent().getLocationOnScreen().getY();
					}
					editorMenu.show(e.getComponent(), lastMouseX, lastMouseY);
				}
			}
		});
		editorMenu.add(closeMenu);
		closeMenu.addActionListener(this);
		editorMenu.addSeparator();
		editorMenu.add(compileMenu);
		editorMenu.add(toggleTextShareMenu);
		editorMenu.add(requestWriteShareMenu);
		compileMenu.setEnabled(false);
		compileMenu.addActionListener(this);
		toggleTextShareMenu.addActionListener(this);
		requestWriteShareMenu.addActionListener(this);
		editorMenu.addSeparator();
		editorMenu.add(viewBookmarkMenu);
		viewBookmarkMenu.addActionListener(this);
		editorMenu.add(addBookmarkMenu);
		addBookmarkMenu.addActionListener(this);
		editorMenu.add(removeBookmarkMenu);
		removeBookmarkMenu.addActionListener(this);

		undoAction = new UndoAction();
		redoAction = new RedoAction();
		Document document = editor.getDocument();
		document.addUndoableEditListener(myUndoableEditListener);

		//		if you need to point current line:
		editor.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				quickAccessPanel.highlightLine(getLineForPosition(editor.getText(), editor.getCaretPosition()));
			}
		});
	}

	public TextShareProcessor getShareProcessor() {
		return shareProcessor;
	}

	public TextShareProcessor createShareProcessor(String group, String moderator) {
		if (this.shareProcessor == null) {
			this.shareProcessor = new TextShareProcessor(this, IDE.getHermixLink(), moderator, group);
			editor.setEditable(false);
		}
		return this.shareProcessor;
	}

	public void read(String filename) {
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		file = new File(filename);
		fileNameCandidate = filename;
		//to do : repair null pointer
		fileType = GeneralEditorPane.getTypeByExtension(filename);
		editorKit = IDE.getProperties().getEditorKit(fileType, editor);
		if (file.exists() && file.canRead() && file.isFile()) {
			try {
				editor.read(new FileReader(filename), filename);
				final File bmkFile = new File(filename + ".bmk");
				if (bmkFile.exists() && bmkFile.isFile() && bmkFile.canRead()) {
					//start on a new thread:
					try {
						new ro.intellisoft.XML.XMLMiniParser().parse(bmkFile, new BookmarkXMLReader(getThis()));
						bookmarksPresent = true;
					} catch (Exception ex) {
						System.err.println("Exception reading bookmarks from " + bmkFile);
						//do nothing, in case of exception propeties should new-ed:
						//properties = new Hashtable();
					}
				}
				everSaved = true;
			} catch (IOException e) {
				IDE.getProperties().getEditorKit("txt", editor);
				editor.setText("File not found.\nContent lost.");
			}
		} // else do nothing, 'cause it's a bad path
		IDE.setIconForEditor(this, "images/javafileicon.gif");
		updateEditorSize();
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	public boolean isWriteLockAquired() {
		if (shareProcessor == null) {
			return true;
		}
		return shareProcessor.isWriteLockAquired();
	}

	public void handleBookmarkEvent(ComponentEvent e, int bookmarkLine) {
		if (e instanceof MouseEvent) {
			MouseEvent me = (MouseEvent) e;
			if (me.getButton() == MouseEvent.BUTTON3) {
				//rightClick!
			} else {
				//suppose normal click so:
				setCaretLine(bookmarkLine - 1);
			}
		} else {
			System.out.println("must show bmk @" + bookmarkLine + " with " + e);
		}
	}

	public void doSelectAllAction(Object source) {
		Action[] actionsArray = editor.getActions();
		for (int i = 0; i < actionsArray.length; i++) {
			if (DefaultEditorKit.selectAllAction.equals(actionsArray[i].getValue(Action.NAME))) {
				actionsArray[i].actionPerformed(new ActionEvent(source, 0, "SelectAll"));
			}
		}
	}

	public void doCopyAction(Object source) {
		Action[] actionsArray = editor.getActions();
		for (int i = 0; i < actionsArray.length; i++) {
			if (DefaultEditorKit.copyAction.equals(actionsArray[i].getValue(Action.NAME))) {
				actionsArray[i].actionPerformed(new ActionEvent(source, 0, "Copy"));
			}
		}
	}

	public void doCutAction(Object source) {
		Action[] actionsArray = editor.getActions();
		for (int i = 0; i < actionsArray.length; i++) {
			if (DefaultEditorKit.cutAction.equals(actionsArray[i].getValue(Action.NAME))) {
				actionsArray[i].actionPerformed(new ActionEvent(source, 0, "Cut"));
			}
		}
	}

	public void doPasteAction(Object source) {
		Action[] actionsArray = editor.getActions();
		for (int i = 0; i < actionsArray.length; i++) {
			if (DefaultEditorKit.pasteAction.equals(actionsArray[i].getValue(Action.NAME))) {
				actionsArray[i].actionPerformed(new ActionEvent(source, 0, "Paste"));
			}
		}
	}

	public void doRedoAction(Object source) {
	}

	public void doUndoAction(Object source) {
	}

	public void doGotoLine(){
		int lineCount = editor.getDocument().getDefaultRootElement().getElementCount();
		String res = JOptionPane.showInputDialog(IDE.getMainFrame(), "Line number (1 to " +lineCount+ "):","Go to line...", JOptionPane.QUESTION_MESSAGE);
		int line = -1;
		try {
			line = Integer.decode(res).intValue();
			if (line>lineCount){
				throw new BadLocationException("", line);
			}
		} catch (Exception ex){
			res = "";
		}
		if (res!=null && !res.equals("")){
			this.setCaretLine(line-1);
		}
	}

	/**handles an event at text bookmark from line line and (sub)component idx*/
	public void handleBookmarkEvent(TextBookmark tb, int line, int idx) {
		if (shareProcessor != null) {
			shareProcessor.handleBookmarkEvent(tb, line, idx);
		}
	}

	/**a new bookamrk has been added to the line <code>line</code>*/
	public void handleNewBookmarkEvent(JComponent component, int line, int idx) {
		if (shareProcessor != null) {
			shareProcessor.sendNewBookmarkComponent((BookmarkComponent) component, line, idx);
		}
	}

	public void requestRWright() {
		if (shareProcessor != null) {
			shareProcessor.requestRWright();
		}
	}

	/**handles an event at whiteboard bookmark from line line and (sub)component idx*/
	public void handleBookmarkEvent(WhiteboardBookmark wb, int line, int idx, ro.intellisoft.whiteboard.shapes.Figure f) {
		if (shareProcessor != null) {
			shareProcessor.handleBookmarkEvent(wb, line, idx, f);
		}
	}

	public void setReadOnly(boolean flag) {
		editor.setEditable(!flag);
		if (flag) {
			IDE.setIconForEditor(this, "images/lockedjavafile.gif");
		} else {
			IDE.setIconForEditor(this, "images/javafileicon.gif");
		}
	}

	public int[] getBookmarkLines() {
		int[] values = new int[bookmarks.size()];
		Enumeration lines = bookmarks.keys();
		for (int i = 0; i < values.length; i++) {
			values[i] = ((Integer) lines.nextElement()).intValue();
		}
		return values;
	}

	/**move caret to specified line in the editor pane*/
	public void setCaretLine(int lineNumber) {
		javax.swing.text.Element elem = editor.getDocument().getDefaultRootElement().getElement(lineNumber);
		if (elem != null) {
			editor.setCaretPosition(elem.getStartOffset());
		}
		editor.requestFocusInWindow();
	}

	/**query caret position in the editor pane*/
	public int getCaretPosition() {
		return editor.getCaretPosition();
	}

	/**move caret to specified position in the editor pane*/
	public void setCaretPosition(int newPosition) {
		editor.setCaretPosition(newPosition);
		editor.requestFocusInWindow();
	}

	/**returns the canonical path if posible*/
	public String getAbsoluteFileName() {
		if (file == null) {
			return fileNameCandidate;
		} else {
			String absFile = null;
			try {
				absFile = file.getCanonicalPath();
			} catch (IOException e) {
				absFile = file.getAbsolutePath();
			}
			return absFile;
		}
	}

	/**returns the "short" file name (ie. without path)*/
	public String getFileName() {
		if (file == null) {
			return fileNameCandidate;
		} else {
			return file.getName();
		}
	}

	/**returns true if any modifications appears in the editor pane*/
	public boolean isNotSaved() {
		return altered;
	}

	/**called only for hermix-received files!!!*/
	public void setText(String text) {
		editor.setText(text);
		updateEditorSize();
	}

	public boolean hasBookmarks() {
		return !bookmarks.isEmpty();
	}

	/**returns true if current file has been ever saved*/
	public boolean isEverSaved() {
		return everSaved;
	}

	/**saves to a temporary file!*/
	public void saveToTemporary(String tmpFile) {
		try {
			editor.write(new java.io.OutputStreamWriter(new java.io.FileOutputStream(new File(tmpFile))));
		} catch (IOException e) {
			//do nothing if ioexceptions appear
		}
	}

	/**handles a writing operation*/
	public void saveAs(String newFilename) {
		file = new File(newFilename);
		fileNameCandidate = file.getAbsolutePath();
		try {
			fileNameCandidate = file.getCanonicalPath();
		} catch (IOException e) {
			System.out.println("e = " + e);
		}
		fileType = GeneralEditorPane.getTypeByExtension(newFilename);
		save();
		String oldText = editor.getText();
		IDE.getProperties().getEditorKit(fileType, editor);
		editor.setText(oldText);
		//daca am salvat-o nu mai e nevoie sa fiu in read-only!
		setReadOnly(false);
	}

	private final GeneralEditorPane getThis() {
		return this;
	};

	/**handles a writing operation*/
	public void save() {
		if (file == null) {
			file = new File(fileNameCandidate);
		}
		try {
			//save file
			editor.write(new java.io.OutputStreamWriter(new java.io.FileOutputStream(file)));
			//update internal state to saved:
			altered = false;
			everSaved = true;
			IDE.setIconForEditor(getThis(), "images/javafileicon.gif");
			//now, if new bookmarks have been updated, save them too:
			if (bookmarksPresent && bookmarksAltered) {
				saveBookmarks(file.getCanonicalPath());
			}
		} catch (IOException e) {
			JOptionPane errorMsg = new JOptionPane("Could not save file " + file, JOptionPane.ERROR_MESSAGE, JOptionPane.OK_OPTION);
		}
		if (fileType.equals("java")) {
			//now update the symbol table and the StructureView:
			st = new SymbolTable();
			st.setFile(file);
			System.err.println("   " + file.getAbsolutePath());
			try {
				JavaParser.JavaXref.parseFile(new FileInputStream(file), st);
			} catch (Exception e) {
				System.out.println("Exception while parsing java file: " + e);
			}
			st.resolveTypes();
			DefaultMutableTreeNode strNode = new DefaultMutableTreeNode(getFileName());
			st.report(strNode);
			tc = new TreeClass(strNode);
			IDE.addMouseListenersAndRenderer(tc.getTree());
			IDE.setStructureView(tc.getTreeView());
		}
	}

	/**Invoked when a this component is the active editor.*/
	public void gainFocus() {
		for (int i = 0; i < openBookmarkWindows.size(); i++) {
			((BookmarkWindow) openBookmarkWindows.elementAt(i)).setVisible(true);
		}
		requestFocusInWindow();
		//trigger the caret position update method:
		editor.setCaretPosition(editor.getCaretPosition());
		//put the structure tree in IDE:
		IDE.setStructureView(tc.getTreeView());
	}

	/**Invoked when another component is the active editor.*/
	public void lostFocus() {
		for (int i = 0; i < openBookmarkWindows.size(); i++) {
			((BookmarkWindow) openBookmarkWindows.elementAt(i)).setVisible(false);
		}
	}

	public void closeAllBookmarksWindows() {
		int[] lines = getBookmarkLines();
		for (int i = 0; i < lines.length; i++) {
			getBookmarkForLine(lines[i]).dispose();
		}
	}

	/**removes this window form the list*/
	public void removeBookmarkWindow(BookmarkWindow bw) {
		openBookmarkWindows.remove(bw);
		//and saveBookmarks:
		if (everSaved) {
			saveBookmarks(fileNameCandidate);
		}
	}

	/**Save bookmarks (as XML) to <code>filename.bmk</code> file.*/
	private void saveBookmarks(final String filename) {
		IDE.log("Saving " + getFileName() + " ... ");
		IDE.getProgressBar().setValue(StatusBarProgressBar.INDETERMINABLE);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PrintStream stream = null;
				try {
					stream = new PrintStream(new FileOutputStream(new File(filename + ".bmk")));
				} catch (FileNotFoundException e) {
					System.err.println("Could not save bookmark(s) to text/xml file:" + filename + ".bmk");
					return;
				}

				stream.println("<?xml version=\"1.0\"?>\n<!-- IntelliX bookmark file -->");
				//TO DO: update class file and timestamp
				stream.print("<bookmarks file-reference = \"");
				stream.print(new File(filename).getName());
				stream.print("\" last-update = \"");
				stream.print(System.currentTimeMillis());
				stream.println("\" >");
				Enumeration keys = bookmarks.keys();
				while (keys.hasMoreElements()) {
					Object aKey = keys.nextElement();
					BookmarkWindow bookmark = (BookmarkWindow) bookmarks.get(aKey);
					stream.println(bookmark.getXMLRepresentation(0));
				}
				stream.println("</bookmarks>\n");
				stream.close();
			}
		});
		IDE.getProgressBar().setValue(StatusBarProgressBar.RESET);
	}

	public void updateSymbolTable(SymbolTable st) {
		this.st = st;
		DefaultMutableTreeNode strNode = new DefaultMutableTreeNode(this.getFileName());
		st.report(strNode);
		tc = new TreeClass(strNode);
		IDE.addMouseListenersAndRenderer(tc.getTree());
	}

	/**if is remote the bookmarks will be added as R/O right*/
	public boolean isRemoteFile() {
		return remoteFile;
	}

	public void setRemoteFile(boolean remoteFile) {
		this.remoteFile = remoteFile;
	}

	public String getFileType() {
		return fileType;
	}

	public IntelliX getIDE() {
		return IDE;
	}

	/**Invoked when an action occurs.*/
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == viewBookmarkMenu) {
			int crtLine = GeneralEditorPane.getLineForPosition(editor.getText(), editor.getCaretPosition());
			//now search for the closese bookmark;
			BookmarkWindow bw = getBookmarkForLine(crtLine);
			if (bw == null) {
				return;
				//sorry, no bookmark(s) at this line
			}
			openBookmarkWindows.addElement(bw);
			bw.setVisible(true);
		} else if (e.getSource() == removeBookmarkMenu) {
			int crtLine = GeneralEditorPane.getLineForPosition(editor.getText(), editor.getCaretPosition());
			removeBookmark(crtLine);
			if (shareProcessor != null) {
				shareProcessor.sendRemoveBookmarkWindow(crtLine);
			}
		} else if (e.getSource() == closeMenu) {
			//inchide fereastra; eventual salveaza inainte...
			IDE.closeCurrentSourceFile();
		} else if (e.getSource() == addBookmarkMenu) {
			int crtLine = GeneralEditorPane.getLineForPosition(editor.getText(), editor.getCaretPosition());
			String title = JOptionPane.showInputDialog(this, "Please enter a short description\nfor this annotation:", "Create new bookmark at line #" + crtLine, JOptionPane.QUESTION_MESSAGE);
			if (title == null || title.equals("")) {
				return;
			}
			quickAccessPanel.addBookmarkLink(crtLine);
			BookmarkWindow bw = new BookmarkWindow(this, IDE, title, crtLine, true);
			bookmarks.put(new Integer(crtLine), bw);
			bookmarksPresent = true;
			openBookmarkWindows.addElement(bw);
			bw.setVisible(true);
			if (shareProcessor != null) {
				shareProcessor.sendNewBookmarkWindow(bw, crtLine);
			}
		} else if (e.getSource() == compileMenu) {
			//do nothing yet
		} else if (e.getSource() == requestWriteShareMenu) {
			shareProcessor.requestRWright();
		} else if (e.getSource() == toggleTextShareMenu) {
			if (toggleTextShareMenu.getText().equals("Share this...")) {
				if (IDE.getHermixLink() == null) {
					JOptionPane.showMessageDialog(this, "Could not create a new text share because\nthere are no connection to a Hermix server!\nPlease connect first.", "Action error", JOptionPane.ERROR_MESSAGE);
				} else if (shareProcessor != null) {
					JOptionPane.showMessageDialog(this, "A text share for this file already exists", "Warning", JOptionPane.WARNING_MESSAGE);
				} else {
					shareProcessor = TextShareProcessor.createTextShareProcessor(this, IDE.getHermixLink());
				}
			} else if (toggleTextShareMenu.getText().equals("Stop this share")) {
				//stop share:
				shareProcessor.stopSharing();
				shareProcessor = null;
				//and re-aquire write lock:
				aquireBookmarkWriteLock();
			} else {
				shareProcessor.exitShare();
				shareProcessor = null;
				this.setReadOnly(false);
				aquireBookmarkWriteLock();
				//IDE.closeCurrentSourceFile();
				//leave share
			}
		}
	}

	/**called when the user leave the Hermix Server*/
	public void closeShare() {
		forwardWriteRights();
		setReadOnly(false);
	}

	public void forwardWriteRights() {
		if (shareProcessor == null) {
			//do nothing;
			return;
		} else if (shareProcessor.getModerator() == null) {
			//me is the moderator so I can close the sharing...
			toggleTextShareMenu.setText("Stop this share");
		} else {
			//not my share, but I can leave it out
			toggleTextShareMenu.setText("Leave this share");
		}
		actionPerformed(new ActionEvent(toggleTextShareMenu, 0, "fwd RW right"));
	}

	/**for ce aquire of the write lock on the bookmark*/
	public void aquireBookmarkWriteLock() {
		int[] lines = getBookmarkLines();
		for (int i = 0; i < lines.length; i++) {
			getBookmarkForLine(lines[i]).setEditable(true);
		}
	}

	public void removeShareProcessor() {
		shareProcessor = null;
	}


	/**called by the the share processor only*/
	public String getText() {
		return editor.getText();
	}

	/**
	 * Gives notification that an attribute or set of attributes changed.
	 * Never called since we are using only plain text.
	 */
	public void changedUpdate(DocumentEvent e) {
	}

	/**
	 * Gives notification that there was an insert into the document.  The
	 * range given by the DocumentEvent bounds the freshly inserted region.
	 * @param e the document event
	 */
	public void insertUpdate(DocumentEvent e) {
		alterFile();
		updateEditorSize();
		if (e instanceof  UndoableEdit){
			myUndoableEditListener.undoableEditHappened(new UndoableEditEvent(this, (UndoableEdit)e));
		}
	}

	/**
	 * Gives notification that a portion of the document has been removed.  The range is given
	 * in terms of what the view last saw (that is, before updating sticky positions).
	 * @param e the document event
	 */
	public void removeUpdate(DocumentEvent e) {
		alterFile();
		if (e instanceof  UndoableEdit){
			myUndoableEditListener.undoableEditHappened(new UndoableEditEvent(this, (UndoableEdit)e));
		}
	}

	/**Invoked when a key has been typed.
	 * See the class description for {@link KeyEvent} for a definition of a key typed event.*/
	public void keyTyped(KeyEvent e) {
		//when <ENTER> is pressed all bookmarks for lines below will be updated!
		if (!editor.isEditable()) {
			return;
		}
		if (e.getKeyChar() == KeyEvent.VK_ENTER) {
			updateLineBookmarks(getLineForPosition(editor.getText(), editor.getCaretPosition()) - 1, +1);
		}
	}

	/**Invoked when a key has been released.
	 * See the class description for {@link KeyEvent} for a definition of a key released event.*/
	public void keyReleased(KeyEvent e) {
		if (e.getModifiers() == KeyEvent.CTRL_MASK) {
			//CTRL (only modifier) has been pressed:
			//^A, ^C do not alter the file!, but ^Z, ^Y, ^V, ^X they do!
			switch (e.getKeyCode()) {
				case 'Z':
					if (undo.canUndo()){
						//undo.undo();
						undoAction.actionPerformed(new ActionEvent(e, 0, "UNDO"));
					}
					break;
				case 'Y':
					if (undo.canRedo()){
						//undo.redo();
						redoAction.actionPerformed(new ActionEvent(e, 0, "REDO"));
					}
					break;
				case 'V':
				case 'X':
					alterFile();
					break;
			}
		}
		//if you need to point current line:
		quickAccessPanel.highlightLine(getLineForPosition(editor.getText(), editor.getCaretPosition()));
	}

	public void updateEditorSize() {
		int minWidth = 800;
		final int charWidth = 7;
		for (int i = 0; i < editor.getDocument().getDefaultRootElement().getElementCount(); i++) {
			Element element = editor.getDocument().getDefaultRootElement().getElement(i);
			if ((element.getEndOffset() - element.getStartOffset()) * charWidth > minWidth) {
				minWidth = (element.getEndOffset() - element.getStartOffset()) * (charWidth + 1);
			}
		}
		editor.setPreferredSize(new Dimension(minWidth, 50 + 14 * editor.getDocument().getDefaultRootElement().getElementCount()));
	}

	/**Invoked when a key has been pressed.
	 * See the class description for {@link KeyEvent} for a definition of a key pressed event.*/
	public void keyPressed(KeyEvent e) {
		if (!editor.isEditable()) {
			return;
		}
		if (e.getKeyCode() == KeyEvent.VK_DELETE) {
			int crtLine = getLineForPosition(editor.getText(), editor.getCaretPosition());
			if (crtLine + 1 == getLineForPosition(editor.getText(), editor.getCaretPosition() + 1)) {
				//will delete a line
				updateLineBookmarks(crtLine + 1, -1);
			}
		} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			int crtLine = getLineForPosition(editor.getText(), editor.getCaretPosition());
			if (crtLine - 1 == getLineForPosition(editor.getText(), editor.getCaretPosition() - 1)) {
				//will delete a line
				updateLineBookmarks(crtLine, -1);
			}
		}
	}


	/**tells the IDE if file has been modified*/
	private void alterFile() {
		if (shareProcessor != null) {
			shareProcessor.updateFileContent();
		}
		if (altered) {
			//do nothing file already altered
			return;
		}
		altered = true;
		IDE.addAlteredFile(this);
		if (this.fileType.equals("java")) {
			IDE.setIconForEditor(this, "images/unsavedjavafileicon.gif");
		}
	}

	/**
	 * returns the bookmark for the specified line
	 * if there are no bookmarks at those line <code>null</code> will be returned
	 */
	public BookmarkWindow getBookmarkForLine(int line) {
		return (BookmarkWindow) bookmarks.get(new Integer(line));
	}


	/**
	 * updates the positions of the bookmarks when new line is inserted/removed
	 * all bookmarks that after the specified line (inclusiv) will be shifted with amout lines
	 * @param amount may be positive/negative
	 */
	public void updateLineBookmarks(int startingAtLine, int amount) {
		if (shareProcessor != null) {
			shareProcessor.updateLineBookmarks(startingAtLine, amount);
		}

		Hashtable oldBookmarks = bookmarks;
		Enumeration keys = oldBookmarks.keys();
		bookmarks = new Hashtable(oldBookmarks.size());
		while (keys.hasMoreElements()) {
			Integer aKey = (Integer) keys.nextElement();
			BookmarkWindow o = (BookmarkWindow) oldBookmarks.remove(aKey);
			if (aKey.intValue() >= startingAtLine) {
				quickAccessPanel.removeBookmarkLink(o.getLine());
				o.setLine(aKey.intValue() + amount);
				quickAccessPanel.addBookmarkLink(o.getLine());
			} //else do nothing
			bookmarks.put(new Integer(o.getLine()), o);
			bookmarksPresent =  true;
		}
		//assert oldBookmarks.isEmpty();
	}

	/**adds a bookmark for this file*/
	public void addBookmark(BookmarkWindow bw) {
		bookmarksPresent = true;
		bookmarks.put(new Integer(bw.getLine()), bw);
		quickAccessPanel.addBookmarkLink(bw.getLine());
		if (shareProcessor != null && !shareProcessor.isWriteLockAquired()) {
			///? no
			//bw.setVisible(true);
		}
	}

	/**removes a bookmark for this file*/
	public void removeBookmark(int line) {
		BookmarkWindow bw = getBookmarkForLine(line);
		if (bw == null) {
			return;
		}
		bw.dispose();
		bookmarks.remove(new Integer(line));
		quickAccessPanel.removeBookmarkLink(line);
		bookmarksPresent = !bookmarks.isEmpty();
		saveBookmarks(this.getAbsoluteFileName());
	}

	/**Called when the caret position is updated.*/
	public void caretUpdate(CaretEvent event) {
		int position = event.getDot();
		for (int i = 0; i < editor.getDocument().getDefaultRootElement().getElementCount(); i++) {
			Element element = editor.getDocument().getDefaultRootElement().getElement(i);
			if (position >= element.getStartOffset() && position < element.getEndOffset()) {
				IDE.setCaretPosition(i + 1, position - element.getStartOffset() + 1);
				return;
			}
		}
		IDE.setCaretPosition(-1, -1);
	}

	/**
	 * Extracts the file type using the file extension of the file name passed
	 * If cannot detect extension, returns "txt".
	 */
	public static String getTypeByExtension(String filename) {
		int i = filename.lastIndexOf('.');
		if (i < 0) {
			return "txt";
		} else {
			String extension = filename.substring(i + 1).toLowerCase();
			if (extension.startsWith("htm")) {
				return "html";
			} else if (extension.equals("svg") || extension.equals("xpr") || extension.equals("xpr") || extension.equals("bmk")) {
				return "xml";
			} else {
				return extension;
			}
		}

	}


	/**
	 * searches the line from the text for the <code>position</code>
	 * the first line in a file is line 1.
	 * the first position is 0;
	 * @return the line for the position and -1 if position is out of range of the String bounds
	 */
	public static int getLineForPosition(String s, int position) {
		int line = 0;
		int p = 0;
		int maxLen = s.length();
		if (position > maxLen) {
			return -1;
		}
		do {
			line++;
			int newP = s.indexOf(10, p);//10 == <EOLN>
			p = newP + 1;
			if (newP < 0) {
				return line;
				//and exit, of course!
			}
			if (s.charAt(newP - 1) == '\r') {
				position++;
			}
		} while (position >= p && position <= maxLen + 1);

		return line;
	}

////////////////////////////////////////////
////	Here starts the Undo support	////
////////////////////////////////////////////

	//This one listens for edits that can be undone.
	protected class MyUndoableEditListener implements UndoableEditListener {
		public void undoableEditHappened(UndoableEditEvent e) {
			//Remember the edit and update the menus.
			undo.addEdit(e.getEdit());
			undoAction.updateUndoState();
			redoAction.updateRedoState();
		}
	}


	class UndoAction extends AbstractAction {
		public UndoAction() {
			super("Undo");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			//System.out.println("UNDO: e = " + e);
			try {
				undo.undo();
			} catch (CannotUndoException ex) {
			}
			updateUndoState();
			redoAction.updateRedoState();
		}

		protected void updateUndoState() {
			if (undo.canUndo()) {
				setEnabled(true);
				putValue(Action.NAME, undo.getUndoPresentationName());
			} else {
				setEnabled(false);
				putValue(Action.NAME, "Undo");
			}
		}
	}

	class RedoAction extends AbstractAction {
		public RedoAction() {
			super("Redo");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			//System.out.println("REDO: e = " + e);
			try {
				undo.redo();
			} catch (CannotRedoException ex) {
			}
			updateRedoState();
			undoAction.updateUndoState();
		}

		protected void updateRedoState() {
			if (undo.canRedo()) {
				setEnabled(true);
				putValue(Action.NAME, undo.getRedoPresentationName());
			} else {
				setEnabled(false);
				putValue(Action.NAME, "Redo");
			}
		}
	}


}


