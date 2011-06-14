
package ro.intellisoft.whiteboard.shapes;

import com.hermix.HermixApi;

import java.util.Vector;
import java.awt.*;

/**
 * <B>Title:        </B>Eraser<br>
 * <B>Description:  </B>Clasa care abstactizeaza stergerea unei figuri<br>
 * <B>Copyright:    </B>Copyright (c) 2001 <br>
 * <B>Company:      </B>Intellisoft <br>
 * @author Ovidiu Maxiniuc
 * @version 2.0
 */


public class Eraser extends Figure {

	public Eraser(long UID) {
		super(-1, -1, -1, -1, -1, -1, null, 0);
		type = com.hermix.SC.GRP_Delete_Figure;
		this.UID = UID;
	}

	public int contains(int x, int y) {
		return OUT_OF_BOUNDS;
	}

	public void send(HermixApi sender, String group) {
		java.util.Vector v = new java.util.Vector(2, 0);
		v.add(new Long(UID));
		sender.g_send_whiteboard_message(group, type, new com.hermix.Rect(),
										 null, -1, "", v);
	}

	public String toSVG(String prefix) {
		return "";
	}

	public Vector toVector() {
		Vector v = new Vector();
		v.addElement(new Integer(type));
		v.addElement(new Long(getUID()));
		return v;
	}

	public void updateToolConfig(ro.intellisoft.whiteboard.WBListener listener,
								 ro.intellisoft.whiteboard.Whiteboard wb) {
	}

	public void draw(java.awt.Graphics2D g) {
	}
}