/**
 * User: Administrator
 * @Author: qurtach@intellisoft.ro
 * @Date: Jun 7, 2002
 * @Time: 11:16:44 AM
 */

package ro.intellisoft.intelliX.bookmarks;

import ro.intellisoft.intelliX.HermixLink;
import ro.intellisoft.intelliX.IntelliX;
import ro.intellisoft.intelliX.UI.MyButton;
import ro.intellisoft.whiteboard.Whiteboard;

import javax.swing.*;
import java.awt.*;

public class AudioBookmark extends JPanel implements BookmarkComponent{

	/**the parent*/
	protected BookmarkWindow parent = null;

	private MyButton playButton = new MyButton();
	private MyButton pauseButton = new MyButton();
	private MyButton stopButton = new MyButton();
	private SoundRepresentation sound = new SoundRepresentation();

	public AudioBookmark(BookmarkWindow bookmarkWindow){
		super(new FlowLayout(FlowLayout.LEFT));
		this.add(playButton);
		this.add(pauseButton);
		this.add(stopButton);
		this.add(sound);
		Whiteboard.setExactDimension(playButton, 24, 24);
		Whiteboard.setExactDimension(pauseButton, 24, 24);
		Whiteboard.setExactDimension(stopButton, 24, 24);
		playButton.setIcon(IntelliX.loadImageResource(this, "images/audioplay.gif"));
		pauseButton.setIcon(IntelliX.loadImageResource(this, "images/audiopause.gif"));
		stopButton.setIcon(IntelliX.loadImageResource(this, "images/audiostop.gif"));
	}

	/**tells if this component accept focus traversing when pressing PGUP/PGDN/UP/DN/LEFT/RIGHT*/
	public boolean acceptFocus() {
		return false;
	}

	public static final String AUDIO_COMPONENT = "AudioBookmark";
	public String getName(){
		return AUDIO_COMPONENT;
	}

	/**convert this component to a XML format in order to be saved*/
	public String getXMLRepresentation(int flag) {
		return "";
	}

	public void setEditable(boolean flag) {
	}
}


class SoundRepresentation extends JComponent{

	public SoundRepresentation(){
		Whiteboard.setExactDimension(this, 220, 28);
	}

	public void paint(Graphics g){
		g.setColor(Color.white);
		g.fillRect(1, 1, getWidth()-2, getHeight()-2);
		g.setColor(Color.black);
		g.drawRect(0, 0, getWidth()-1, getHeight()-1);
		g.setColor(Color.red);
		g.drawLine(1, getHeight()/2, getWidth()-2, getHeight()/2);
	}


}