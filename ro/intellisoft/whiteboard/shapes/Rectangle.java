
package ro.intellisoft.whiteboard.shapes;


import com.hermix.*;
import ro.intellisoft.whiteboard.WBListener;
import ro.intellisoft.whiteboard.Whiteboard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Vector;

/**
 * <B>Title:        </B>Rectangle Figure <br>
 * <B>Description:  </B>Abstractizarea unei figuri in forma de dreptunghi<br>
 * <B>Copyright:    </B>Copyright (c) 2001 <br>
 * <B>Company:      </B>Intellisoft <br>
 * @author Ovidiu Maxiniuc
 * @version 2.0
 */


public class Rectangle extends Figure {

	/**
	 * daca dreptunghiul este 'plin' atunci il coloram cu culoarea asta
	 * altfel culoarea este pe null si rectangelu' gol
	 */
	protected Color fillColor = null;

	/**
	 * Metoda de setare a culorii de fill.
	 */
	public void setFillColor(Color fill) {
		this.fillColor = fill;
	}

	/**
	 *  Construnctorul initializeaza elementele comune
	 */
	public Rectangle(int x1, int y1, int x2, int y2, int size, int caract, Color foreground, Color fillColor, int hc) {
		super(x1, y1, x2, y2, size, caract, foreground, hc);
		this.fillColor = fillColor;
		type = SC.GRP_Rectangle;
		jmiFigure.setText("Rectangle#" + Integer.toHexString((int) UID));
	}

	/**
	 *  Construnctorul initializeaza elementele comune. <br>
	 *  Obiectul este creat in alta parte. Deci putem sa setam UID'ul.
	 */
	public Rectangle(int x1, int y1, int x2, int y2, int size, int caract, Color foreground, Color fillColor, long UID) {
		super(x1, y1, x2, y2, size, caract, foreground, 0);
		this.fillColor = fillColor;
		type = SC.GRP_Rectangle;
		this.UID = UID;
		jmiFigure.setText("Rectangle#" + Integer.toHexString((int) UID));
	}


	public int contains(int x, int y) {
		//daca (x, y) e in afara granitelor dreptunghiului e clar
		if (x + size / 2 + ERROR_LIMIT < Math.min(x1, x2) || x - size / 2 - ERROR_LIMIT > Math.max(x1, x2) || y + size / 2 + ERROR_LIMIT < Math.min(y1, y2) || y - size / 2 - ERROR_LIMIT > Math.max(y1, y2))
			return OUT_OF_BOUNDS;

		int value2return = OUT_OF_BOUNDS;
		//verificam colturile:
		int corner = getCorner(x, y);
		if (corner != OUT_OF_BOUNDS)
			value2return |= corner;
		// daca e colorat, putem sa il 'apucam' si din interior
		if (fillColor != null)
			return value2return | FIGURE_HIT;
		//daca nu e colorat, si suntem in interior, iar e clar:
		if (x - size / 2 - ERROR_LIMIT > Math.min(x1, x2) && x + size / 2 + ERROR_LIMIT < Math.max(x1, x2) && y - size / 2 - ERROR_LIMIT > Math.min(y1, y2) && y + size / 2 + ERROR_LIMIT < Math.max(y1, y2))
			return value2return | INSIDE_BOUNDS;
		//a ramas cazul in care dreptunghiul e gol si suntem pe chenar:
		return value2return | FIGURE_HIT;
	}

	public String toSVG(String prefix) {
		return prefix+"<!-- simple rectangle -->\n" + prefix+"<rect x=\"" + Math.min(x1, x2) + "\" y=\"" + Math.min(y1, y2) + "\"" + " width=\"" + Math.abs(x2 - x1) + "\" height=\"" + Math.abs(y2 - y1) + "\"" + (getStyle(ROUNDED_CORNER) == ROUNDED_CORNER?
				" rx=\"" + (Math.min(getHeight(), getWidth()) / 4 + size) + "\" ry=\"" + (Math.min(getHeight(), getWidth()) / 4 + size) + "\"":"") + " style=\"stroke-width:" + size + "; fill:" + (fillColor == null? "none": "#" + Integer.toHexString(fillColor.getRGB()).substring(2, 8)) + "; stroke:#" + Integer.toHexString(foreground.getRGB()).substring(2, 8) + (getStyle(LINE_STYLE_MASK) == DOTTED_LINE? "; stroke-dasharray:" + size:
				(getStyle(LINE_STYLE_MASK) == DASHED_LINE? "; stroke-dasharray:" + (2 + size * 2):"")) + "\" />\n";

	}

	public Vector toVector() {
		Vector v = new Vector();
		v.addElement(new Integer(type));
		v.addElement(new Long(getUID()));
		v.addElement(new Point(x1, y1));
		v.addElement(new Point(x2, y2));
		v.addElement(new Integer(caract));
		v.addElement(new Integer(size));
		v.addElement(foreground);
		v.addElement(fillColor);
		return v;
	}

	public void send(HermixApi sender, String group) {
		Rect r = new Rect();
		r.x1 = Math.min(x1, x2);
		r.y1 = Math.min(y1, y2);
		r.x2 = r.x1 + this.getWidth();
		r.y2 = r.y1 + this.getHeight();
		java.util.Vector v = new java.util.Vector(3, 1);
		v.add(new Long(getUID()));
		v.add(new Integer(caract));
		sender.g_send_whiteboard_message(group, type, r, foreground, size, fillColor == null?null:fillColor.getRGB() + "", v);
	}

	public void draw(Graphics2D g) {
		//alegem stilul de linie cu care vom desena:
		switch (getStyle(super.LINE_STYLE_MASK)) {
			case DASHED_LINE:
				float dash1[] = {10.0f};
				g.setStroke(new BasicStroke(this.size, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f));
				break;
			case DOTTED_LINE:
				float dot1[] = {2.0f};
				g.setStroke(new BasicStroke(this.size, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dot1, 0.0f));
				break;
			default:
				g.setStroke(new BasicStroke(this.size));
		}
		//daca dreptunghiul este 'plin' atunci desenam dintai interorul:
		if (fillColor != null) {
			g.setPaint(fillColor);
			if (getStyle(ROUNDED_CORNER) == ROUNDED_CORNER) {
				g.fillRoundRect(x1 - size / 2, y1 - size / 2, getWidth() + size, getHeight() + size, Math.min(getHeight(), getWidth()) / 4 + size, Math.min(getHeight(), getWidth()) / 4 + size);
			} else
				g.fillRect(x1, y1, getWidth(), getHeight());
		}

		//culoarea si grosimea liniei
		g.setPaint(foreground);
		if (getStyle(ROUNDED_CORNER) == ROUNDED_CORNER)
			g.drawRoundRect(x1, y1, getWidth(), getHeight(), Math.min(getHeight(), getWidth()) / 4, Math.min(getHeight(), getWidth()) / 4);
		else
			g.drawRect(x1, y1, getWidth(), getHeight());
	}//draw

	public void updateToolConfig(WBListener listener, Whiteboard wb) {
		listener.actionPerformed(new ActionEvent(wb.jtbRectangle, ActionEvent.ACTION_PERFORMED, "JToggleButtonChanged"));
		wb.jtbRectangle.setSelected(true);

		if ((getStyle(LINE_STYLE_MASK) & DOTTED_LINE) != 0)
			wb.jcbLineStyleChooser.setSelectedItem("Dotted");
		else if ((getStyle(LINE_STYLE_MASK) & DASHED_LINE) != 0)
			wb.jcbLineStyleChooser.setSelectedItem("Dashed");
		else
			wb.jcbLineStyleChooser.setSelectedItem("Solid");

		wb.jtbRoundedCorner.setSelected(getStyle(CORNER_MASK) == ROUNDED_CORNER);

		if (fillColor == null) {
			wb.jtbFillFigures.setSelected(false);
			wb.jbFillColor.setEnabled(false);
		} else {
			wb.jtbFillFigures.setSelected(true);
			wb.jbFillColor.setEnabled(true);
			wb.jbFillColor.setForeground(fillColor);
			wb.fillColor = fillColor;
		}
		wb.jbForegroundColor.setForeground(foreground);
		wb.jcbLineSizeChooser.setSelectedItem("" + size);
	}

}//Rectangle