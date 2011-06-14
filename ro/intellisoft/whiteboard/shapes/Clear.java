
package ro.intellisoft.whiteboard.shapes;

import com.hermix.Rect;
import com.hermix.SC;
import com.hermix.HermixApi;

import java.util.Vector;
import java.awt.*;

/**
 * <B>Title:        </B>Bakground Figure <br>
 * <B>Description:  </B>Clasa care abstactizeaza o fundalul pe care se deseneaza.
 *      Este folosita numai prentru comunicatii<br>
 * <B>Copyright:    </B>Copyright (c) 2001 <br>
 * <B>Company:      </B>Intellisoft <br>
 * @author Ovidiu Maxiniuc
 * @version 2.0
 */

public class Clear extends Figure {

	public Clear() {
		super(-1, -1, -1, -1, -1, -1, null, -1);
	}

	public int contains(int x, int y) {
		return OUT_OF_BOUNDS;
	}

	public void send(HermixApi sender, String group) {
		sender.g_send_whiteboard_message(group, SC.GRP_Clear, new Rect(),
										 java.awt.Color.white, -1, null, null);
	}

	public String toSVG(String prefix) {
		return "";
	}

	public Vector toVector() {
		Vector v = new Vector();
		v.addElement(new Integer(type));
		v.addElement(new Long(0));
		return v;
	}


	public void updateToolConfig(ro.intellisoft.whiteboard.WBListener listener,
								 ro.intellisoft.whiteboard.Whiteboard wb) {
	}

	public void draw(java.awt.Graphics2D g) {
	}
}