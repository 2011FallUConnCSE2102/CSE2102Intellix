/**
 * @Author: qurtach@intellisoft.ro
 * @Date: Jun 5, 2002
 * @Time: 12:39:30 PM
 */

package ro.intellisoft.intelliX.UI;

import javax.swing.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.util.Vector;

public class QuickAccessPanel extends JComponent implements MouseListener{

	/**the parent component*/
	private GeneralEditorPane parent = null;

	/**the height of the text. in order to paint line numbers for example.*/
	private int textHeight = 14;//default Courier New 12pt

	/**highlighted line (crt line)*/
	private int crtLine = -1;

	private final Vector bookmarkLines = new Vector();

	/**just a constructor with a GeneralEditorPane parent*/
	public QuickAccessPanel(GeneralEditorPane parent) {
		this.parent = parent;
		this.setMinimumSize(new Dimension(16, 1));
		this.setPreferredSize(new Dimension(16, 16));
		this.addMouseListener(this);
	}

	public void paint(Graphics g){
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, getWidth()-1, getHeight()-1);
		//draw the bookmark links:
		for (int i = 0; i < bookmarkLines.size(); i++) {
			int line = ((Integer)bookmarkLines.elementAt(i)).intValue();
			g.setColor(Color.cyan);
			g.fillOval(2, line*textHeight-10, 11, 11);
			g.setColor(Color.blue);
			g.drawOval(2, line*textHeight-10, 10, 10);
		}
		//draw the crt Line:
		g.setColor(Color.black);
		g.drawLine(1, (crtLine-1)*textHeight+5, 13, crtLine*textHeight-textHeight/2+3);
		g.drawLine(1, crtLine*textHeight+1, 13, crtLine*textHeight-textHeight/2+3);
		g.drawLine(1, (crtLine-1)*textHeight+5, 6, crtLine*textHeight-textHeight/2+3);
		g.drawLine(1, crtLine*textHeight+1, 6, crtLine*textHeight-textHeight/2+3);
		//draw other things
		g.setColor(Color.gray);
		g.drawRect(getWidth()-1, 0, getWidth()-1, getHeight());
	}

	public void highlightLine(int aLine){
		this.crtLine = aLine;
		this.repaint();
	}

	/**
	 * adds a line that will be drawn on this component.
	 * Also the parent will be notified about any click on such line!
	 */
	public void addBookmarkLink(int line){
		Integer newLineBookmark = new Integer(line);
		if (!bookmarkLines.contains(newLineBookmark)){
			bookmarkLines.addElement(newLineBookmark);
		}
		this.repaint();
	}

	/** removes a line that contains a bookmark */
	public void removeBookmarkLink(int line){
		bookmarkLines.remove(new Integer(line));
		this.repaint();
	}

	public void setTextHeight(int textHeight) {
		if (textHeight <1){
			return;
		}
		this.textHeight = (int)(textHeight*1.5-1);
	}
	////////////////////////////////////////////////////
	//// Listener implementations:

	/**Invoked when the mouse button has been clicked (pressed and released) on a component.*/
	public void mouseClicked(MouseEvent e) {
		int crtLine = e.getY()/textHeight + 1;
		if (bookmarkLines.contains(new Integer(crtLine))){
			parent.handleBookmarkEvent(e, crtLine);
			return;//?
		}
		if (e.getButton() == MouseEvent.BUTTON1){
			highlightLine(e.getY()/textHeight + 1);
			parent.setCaretLine(e.getY()/textHeight);
		}
	}

	/**Invoked when the mouse enters a component.*/
	public void mouseEntered(MouseEvent e) {}

	/**Invoked when the mouse exits a component.*/
	public void mouseExited(MouseEvent e) {}

	/**Invoked when a mouse button has been pressed on a component.*/
	public void mousePressed(MouseEvent e) {}

	/**Invoked when a mouse button has been released on a component.*/
	public void mouseReleased(MouseEvent e) {}
}
