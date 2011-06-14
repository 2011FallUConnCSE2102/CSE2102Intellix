/**
 * User: Administrator
 * @Author: qurtach@intellisoft.ro
 * @Date: Jun 6, 2002
 * @Time: 3:03:49 PM
 */

package ro.intellisoft.intelliX.bookmarks;

import ro.intellisoft.intelliX.HermixLink;
import ro.intellisoft.intelliX.IntelliX;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.*;


public class TextBookmark extends JEditorPane implements BookmarkComponent, KeyListener{

	/**the parent*/
	protected BookmarkWindow parent = null;

	/**memorize the position when pressing a change component focus key*/
	private int lastPosition = -2;
	/**also memorize the keycode of this event*/
	private int lastMovementKeyPressed = -1;

	public TextBookmark(BookmarkWindow bookmarkWindow, IntelliX IDE){
		IDE.getProperties().getEditorKit("java", this);
		this.addKeyListener(this);
		parent = bookmarkWindow;
	}

	public static final String TEXT_COMPONENT = "TextBookmark";
	public String getName(){
		return TEXT_COMPONENT;
	}

	/**tells if this component accept focus traversing when pressing PGUP/PGDN/UP/DN/LEFT/RIGHT*/
	public boolean acceptFocus() {
		//always accept focus!
		requestFocusInWindow();
		return true;
	}

	/**
	 * Returns the preferred size for the <code>JEditorPane</code>.
	 * The preferred size for <code>JEditorPane</code> is slightly altered
	 * from the preferred size of the superclass.  If the size
	 * of the viewport has become smaller than the minimum size
	 * of the component, the scrollable definition for tracking
	 * width or height will turn to false.  The default viewport
	 * layout will give the preferred size, and that is not desired
	 * in the case where the scrollable is tracking.  In that case
	 * the <em>normal</em> preferred size is adjusted to the
	 * minimum size.  This allows things like HTML tables to
	 * shrink down to their minimum size and then be laid out at
	 * their minimum size, refusing to shrink any further.
	 *
	 * @return a <code>Dimension</code> containing the preferred size
	 */
	public Dimension getPreferredSize() {
		return new Dimension(334, (int)super.getPreferredSize().getHeight());
	}

	/**convert this component to a XML format in order to be saved*/
	public String getXMLRepresentation(int flag) {
		StringBuffer sb = new StringBuffer("\t\t<link type = \"annotation\" time-stamp = \""+System.currentTimeMillis()+"\" >\n");
        for (int i = 0; i < this.getDocument().getDefaultRootElement().getElementCount(); i++){
			int start = this.getDocument().getDefaultRootElement().getElement(i).getStartOffset();
			int end = this.getDocument().getDefaultRootElement().getElement(i).getEndOffset();
			try {
				sb.append("\t\t\t<line remark = \"" + this.getDocument().getText(start, end-start-1) + "\" />\n");
			} catch (BadLocationException e) {}
		}
		sb.append("\t\t</link>\n");
		return sb.toString();
	}

	/**Method that adds (appends) a line into this text*/
	public void addLine(String aLine){
		String newline = getText().length()==0 ? "": "\n";
		try {
			this.getDocument().insertString(this.getDocument().getLength(), newline + aLine, null);
		} catch (BadLocationException e) {
			this.setText(this.getText()+ newline + aLine);
		}
	}

	/**Invoked when a key has been typed.*/
	public void keyTyped(KeyEvent e) {}

	/**Invoked when a key has been released.*/
	public void keyReleased(KeyEvent e) {
		if (lastPosition == getCaretPosition() && lastMovementKeyPressed == e.getKeyCode()){
			//could not move: so pass focus up or down:
			if (lastMovementKeyPressed == KeyEvent.VK_PAGE_UP || e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_LEFT){
				//try moving up:
				parent.giveFocus(this, BookmarkWindow.UP);
			} else {
				//try moving down:
				parent.giveFocus(this, BookmarkWindow.DOWN);
			}
		}
		lastPosition = -1;
		lastMovementKeyPressed = -2;
		parent.handleChangeEvent(this);
	}

	/**Invoked when a key has been pressed.*/
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_PAGE_UP || e.getKeyCode() == KeyEvent.VK_PAGE_DOWN
					|| e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN
					|| e.getKeyCode() == KeyEvent.VK_LEFT|| e.getKeyCode() == KeyEvent.VK_RIGHT){
			//memorize the current position
			lastPosition = getCaretPosition();
			lastMovementKeyPressed = e.getKeyCode();
		}
	}
}
