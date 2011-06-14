/**
 * User: Administrator
 * @Author: qurtach@intellisoft.ro
 * @Date: Jun 17, 2002
 * @Time: 3:35:44 PM
 */

package ro.intellisoft.whiteboard.event;

import ro.intellisoft.whiteboard.Whiteboard;
import ro.intellisoft.whiteboard.shapes.Figure;

import java.awt.*;

public class WhiteboardEvent extends AWTEvent{

	private Figure f = null;

	public WhiteboardEvent(Whiteboard target, int id, Figure f) {
		super(target, id);
		this.f = f;
	}

	public Figure getNewFigure() {
		return f;
	}
}
