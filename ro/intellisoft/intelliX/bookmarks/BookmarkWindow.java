/**
 * User: Administrator
 * @Author: qurtach@intellisoft.ro
 * @Date: Jun 6, 2002
 * @Time: 3:39:59 PM
 */

package ro.intellisoft.intelliX.bookmarks;

import ro.intellisoft.intelliX.UI.GeneralEditorPane;
import ro.intellisoft.intelliX.UI.MyButton;
import ro.intellisoft.intelliX.IntelliX;
import ro.intellisoft.whiteboard.event.WhiteboardListener;
import ro.intellisoft.whiteboard.event.WhiteboardEvent;
import ro.intellisoft.whiteboard.Whiteboard;
import ro.intellisoft.whiteboard.shapes.Figure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector;

public class BookmarkWindow extends JDialog implements WhiteboardListener, WindowListener, ActionListener{

	private int line = 0;
	private String title = "";

	private GeneralEditorPane parent = null;
	private JComponent qcomponentToAdd = new JPanel(new GridBagLayout());
	private GridBagConstraints gbc = new GridBagConstraints(GridBagConstraints.REMAINDER,
				GridBagConstraints.RELATIVE, 1, 1, 300, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0);
	private final java.util.Vector subComponents = new java.util.Vector();

	private MyButton closeButton = new MyButton();
	private MyButton addNewTextButton = new MyButton();
	private MyButton addNewWhiteboardButton = new MyButton();
	private MyButton RWRequestButton = new MyButton();
	private IntelliX IDE = null;
	private boolean editEnabled = false;
	private JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	public static final int UP = 0;
	public static final int DOWN = 1;

	public BookmarkWindow(GeneralEditorPane parent, IntelliX IDE, String title, int line, boolean editEnabled) {
		super(IDE.getMainFrame(), title);
		this.parent = parent;
		this.title = title;
		this.line = line;
		this.IDE = IDE;
		this.editEnabled = editEnabled;
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(qcomponentToAdd);
		this.setModal(false);
		this.setSize(360, 330);
		this.addWindowListener(this);
		this.setResizable(false);
		this.setLocationRelativeTo(parent);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.add(addNewTextButton);
		buttonPanel.add(addNewWhiteboardButton);
		buttonPanel.add(RWRequestButton);
		buttonPanel.add(closeButton);
		ro.intellisoft.whiteboard.Whiteboard.setExactDimension(addNewTextButton, 58, 24);
		ro.intellisoft.whiteboard.Whiteboard.setExactDimension(addNewWhiteboardButton, 58, 24);
		ro.intellisoft.whiteboard.Whiteboard.setExactDimension(closeButton, 58, 24);
		ro.intellisoft.whiteboard.Whiteboard.setExactDimension(RWRequestButton, 58, 24);
		addNewTextButton.setIcon(IntelliX.loadImageResource(this,"images/bookmarkaddtext.gif"));
		addNewWhiteboardButton.setIcon(IntelliX.loadImageResource(this,"images/bookmarkaddwhiteboard.gif"));
		RWRequestButton.setIcon(IntelliX.loadImageResource(this,"images/requestWriteAccess.gif"));
		closeButton.setIcon(IntelliX.loadImageResource(this,"images/closewindow.gif"));
		addNewTextButton.addActionListener(this);
		addNewWhiteboardButton.addActionListener(this);
		closeButton.addActionListener(this);
		RWRequestButton.addActionListener(this);

		addNewTextButton.setEnabled(editEnabled);
		addNewWhiteboardButton.setEnabled(editEnabled);
		RWRequestButton.setEnabled(!editEnabled);
	}

	public void addComponent(JComponent component, boolean alsoSend) {
		subComponents.addElement(component);
		if (component instanceof WhiteboardBookmark){
			WhiteboardBookmark wb = (WhiteboardBookmark)component;
			wb.parent = this;
			wb.setEditable(this.editEnabled);
			wb.getWhiteboard().addWhiteboardListener(this);
		} else if (component instanceof TextBookmark){
			((TextBookmark)component).parent = this;
			((TextBookmark)component).setEditable(this.editEnabled);
		} else if (component instanceof AudioBookmark){
			((AudioBookmark)component).parent = this;
		} //else ***, exception should be thrown
		component.setBorder(BorderFactory.createLineBorder(Color.gray, 1));
		qcomponentToAdd.add(component, gbc);
		JPanel temp = new JPanel(new BorderLayout(0, 0));
		this.validate();
		if (alsoSend){
			//first make this scroll so as the last component should be visible:
			JComponent panel = (JComponent)scrollPane.getViewport().getView();
			panel.scrollRectToVisible(new Rectangle(panel.getWidth()-10, panel.getHeight()-10, panel.getWidth()-1, panel.getHeight()-1));
			//and send this component to the other users from this private group:
			parent.handleNewBookmarkEvent(component, line, subComponents.indexOf(component));
		}
	}

	public void giveFocus(JComponent who, int direction) {
		for (int i = 0; i < subComponents.size(); i++) {
			JComponent c = (JComponent) subComponents.elementAt(i);
			if (c == who) {
				if (direction == BookmarkWindow.UP) {
					for (int j = i-1; j >= 0; j--) {
						if (((BookmarkComponent)subComponents.elementAt(j)).acceptFocus()){
							return;
							//focus is already passed. Nothing to do...
						}
					}
				} else {
					for (int j = i+1; j < subComponents.size(); j++) {
						if (((BookmarkComponent)subComponents.elementAt(j)).acceptFocus()){
							return;
							//focus is already passed. Nothing to do...
						}
					}
				}
				return;
				//if reached this line then no component has accepted the focus :-p
			}//if found who among the known subcomponents
		}//iterate known subcomponents
	} //giveFocus

	/**handles the event of a bookmark*/
	public void handleChangeEvent(TextBookmark tb){
		for (int i = 0; i < subComponents.size(); i++) {
			if (tb == subComponents.elementAt(i)){
				parent.handleBookmarkEvent(tb, line, i);
                break;
			}
		}
	}

	public String getXMLRepresentation(int flag){
        StringBuffer sb = new StringBuffer("\t<bookmark line =\"" + line + "\" description =\"" + title + " \">\n");
		for (int i = 0; i < subComponents.size(); i++) {
			BookmarkComponent bc = (BookmarkComponent) subComponents.elementAt(i);
			sb.append(bc.getXMLRepresentation(flag));
		}
		sb.append("\t</bookmark>");
		return sb.toString();
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getLine() {
		return line;
	}

	public void clear(){
		subComponents.clear();
		qcomponentToAdd.removeAll();
	}

	public void createNewComponent(String componentType, int idx){
		System.out.println("componentType = " + componentType);
		if (subComponents.size() != idx){
			System.out.println("Wrong index: " + idx + " should be " + subComponents.size());
		}
        if (componentType.equals(TextBookmark.TEXT_COMPONENT)){
			this.addComponent(new TextBookmark(this, IDE), false);
		} else if (componentType.equals(WhiteboardBookmark.WHITEBOARD_COMPONENT)){
			this.addComponent(new WhiteboardBookmark(this, true), false);
		} else if (componentType.equals(AudioBookmark.AUDIO_COMPONENT)){
        }
	}

    public void setEditable(boolean flag){
		editEnabled = flag;
		for (int i = 0; i < subComponents.size(); i++) {
			((BookmarkComponent) subComponents.elementAt(i)).setEditable(flag);
		}
		addNewTextButton.setEnabled(flag);
		addNewWhiteboardButton.setEnabled(flag);
		RWRequestButton.setEnabled(!flag);
    }

	public String getBookmarkTitle() {
		return title;
	}

	/**Invoked the first time a window is made visible.*/
	public void windowOpened(WindowEvent e) {}

	/**Invoked when the Window is set to be the active Window. */
	public void windowActivated(WindowEvent e) {}

	/**Invoked when a window has been closed as the result of calling dispose on the window.*/
	public void windowClosed(WindowEvent e) {}

	/**Invoked when the user attempts to close the window from the window's system menu.*/
	public void windowClosing(WindowEvent e) {
		parent.removeBookmarkWindow(this);
		this.dispose();
	}

	/**Invoked when a Window is no longer the active Window. */
	public void windowDeactivated(WindowEvent e) {}

	/**Invoked when a window is changed from a minimized to a normal state.*/
	public void windowDeiconified(WindowEvent e) {}

	/**Invoked when a window is changed from a normal to a minimized state. */
	public void windowIconified(WindowEvent e) {
	}

	/**Invoked when an action occurs.*/
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == closeButton){
			parent.removeBookmarkWindow(this);
			this.dispose();
		} else if (e.getSource() == addNewTextButton){
			this.addComponent(new TextBookmark(this, IDE), true);
		} else if (e.getSource() == addNewWhiteboardButton){
			this.addComponent(new WhiteboardBookmark(this, true), true);
		} else if (e.getSource() == RWRequestButton){
			parent.requestRWright();
		}
	}

	public void actionPerformed(WhiteboardEvent we) {
        Whiteboard w = (Whiteboard)we.getSource();
		for (int i = 0; i < subComponents.size(); i++) {
			if (subComponents.elementAt(i) instanceof WhiteboardBookmark){
				if (((WhiteboardBookmark)subComponents.elementAt(i)).getWhiteboard() == w){
					parent.handleBookmarkEvent((WhiteboardBookmark)subComponents.elementAt(i), line, i, we.getNewFigure());
				}
			}
		}
	}

	public void updateContent(int idx, Object newValue){
		if (subComponents.elementAt(idx) instanceof TextBookmark){
			((TextBookmark)subComponents.elementAt(idx)).setText((String)newValue);
		} else if (subComponents.elementAt(idx) instanceof WhiteboardBookmark){
			Figure f = Figure.createFigureFromVector((Vector)newValue);
			if (f != null){
				((WhiteboardBookmark)subComponents.elementAt(idx)).getWhiteboard().add(f);
			}
		}
	}

	public GeneralEditorPane getEditor() {
		return parent;
	}
}
