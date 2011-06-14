/**
 * User: Administrator
 * @Author: qurtach@intellisoft.ro
 * @Date: Jun 7, 2002
 * @Time: 11:16:44 AM
 */

package ro.intellisoft.intelliX.bookmarks;

import ro.intellisoft.intelliX.HermixLink;
import ro.intellisoft.whiteboard.Whiteboard;
import ro.intellisoft.whiteboard.SVGLoader;
import ro.intellisoft.whiteboard.event.WhiteboardEvent;
import ro.intellisoft.whiteboard.shapes.Figure;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class WhiteboardBookmark extends JPanel implements BookmarkComponent, ActionListener{

	/**the parent*/
	protected BookmarkWindow parent = null;

	/**the whiteboard on which we'll put the real information*/
	private Whiteboard wb = null;

	/**the source file */
	private String src = null;

	private boolean imageAlone = false;

	/**Constructor called when a blank whiteboard is to be constructed*/
	public WhiteboardBookmark(BookmarkWindow bookmarkWindow, boolean editable){
		super(new BorderLayout());
		parent = bookmarkWindow;
		init();
		wb.setEditable(editable);
	}

	/**Constructor called when a single image is put on the wb*/
	public WhiteboardBookmark(BookmarkWindow bookmarkWindow, String fileToLoad){
		super(new BorderLayout());
		parent = bookmarkWindow;
		this.src = fileToLoad;
		init();
		putImage(fileToLoad);
		wb.setEditable(false);
		imageAlone = true;
	}

	/**Constructor called when a file must pe pased in order to show the image on wb*/
	public WhiteboardBookmark(BookmarkWindow bookmarkWindow, boolean editable, String fileToLoad){
		super(new BorderLayout());
		parent = bookmarkWindow;
		this.src = fileToLoad;
		init();
		wb.setEditable(editable);
		processFile(fileToLoad);
	}

	private void init(){
		wb = new Whiteboard((int)(Math.random()*Integer.MAX_VALUE), ".");
		wb.setWbSize(new Dimension(290, 270));
		JScrollPane scrollPane = new JScrollPane(wb, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		wb.setPreferredSize(new Dimension(290, 270));
		wb.setSize(new Dimension(290, 270));
		wb.setStatus(Whiteboard.IDLE);
		this.add(scrollPane, BorderLayout.CENTER);
		this.add(wb.getToolbarConfigPane(), BorderLayout.NORTH);
		this.add(wb.getToolbarZoomPane(), BorderLayout.SOUTH);
		wb.getToolbarZoomPane().setFloatable(false);
		this.add(wb.getToolbarPane(), BorderLayout.WEST);
		wb.getToolbarPane().setOrientation(JToolBar.VERTICAL);
 	}

	/**tells if this component accept focus traversing when pressing PGUP/PGDN/UP/DN/LEFT/RIGHT*/
	public boolean acceptFocus() {
		return false;
	}

	public static final String WHITEBOARD_COMPONENT = "WhiteboardBookmark";
	public String getName(){
		return WHITEBOARD_COMPONENT;
	}

	public Whiteboard getWhiteboard() {
		return wb;
	}

	public void setEditable(boolean flag){
		wb.setEditable(flag);
	}

	/**convert this component to a XML format in order to be saved*/
	public String getXMLRepresentation(int flag) {
		if (flag == -1){
			int timeElapse = (int)(Math.random()*3000);
			System.out.println("timeElapse = " + timeElapse);
			new Timer(timeElapse, this).start();//start in max 3 secs
			return "\t\t<link type = \"image/svg\" src = \"inline\" time-stamp = \""+ System.currentTimeMillis() + "\" ><svg></svg></link>\n";
		}
		if (imageAlone){
			return "\t\t<link type = \"image/jpg\" src = \"" + src + "\" time-stamp = \""+ System.currentTimeMillis() + "\" />\n";
		} else {
			return "\t\t<link type = \"image/svg\" src = \"inline\" time-stamp = \""+ System.currentTimeMillis() + "\" >"+
					wb.getXMLRepresentation("\t\t\t") +"\t\t</link>\n";
		} //else return "";
	}
	/**
	 * The method that inserts a new figure in the whiteboard.<br>
	 * This method is used when figures are stored in the .bmk file.
	 */
	public void addFigure(Figure f){
		wb.add(f);
	}

	/**Put an image on the whiteboard. Resize teh image so as to fit the wb size.*/
	public void putImage(String filename){
		long UID = System.currentTimeMillis();
		wb.add(new ro.intellisoft.whiteboard.shapes.Image(0, 0, (int)wb.getWbSize().getWidth()-1, (int)wb.getWbSize().getHeight()-1, UID));
		wb.setImage(UID, Toolkit.getDefaultToolkit().createImage(filename));
	}

	/**Method that reads a SVG file into the whiteboard...*/
	public void processFile(String fileToReadFrom){
		new SVGLoader(wb, new java.io.File(fileToReadFrom), wb.getGroup()).start();
	}

	/**Invoked when an action occurs.*/
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof Timer){
			Timer t = (Timer)e.getSource();
			t.stop();
			for (int i=0; i<wb.getFigureCount(); i++){
				parent.actionPerformed(new WhiteboardEvent(wb, 0, wb.getFigureAt(i)));
			}
			parent.getEditor().getShareProcessor().countDownWhiteboards();
		}
	}
}
