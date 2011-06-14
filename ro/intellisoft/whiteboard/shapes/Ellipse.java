
package ro.intellisoft.whiteboard.shapes;

import com.hermix.Rect;
import com.hermix.SC;
import ro.intellisoft.whiteboard.WBListener;
import ro.intellisoft.whiteboard.Whiteboard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Vector;

/**
 * <B>Title:        </B>Ellipse Figure <br>
 * <B>Description:  </B>Clasa care abstactizeaza o ellipsa.<br>
 * <B>Copyright:    </B>Copyright (c) 2001 <br>
 * <B>Company:      </B>Intellisoft <br>
 * @author Ovidiu Maxiniuc
 * @version 2.0
 */

public class Ellipse extends Figure {

	/**
	 * Daca ellipsa este 'plina' atunci o coloram cu culoarea asta
	 * altfel culoarea este pe null si ellipsa goala
	 */
	protected Color fillColor = null;

	/**
	 * Metoda de setare a culorii de fill.
	 */
	public void setFillColor(Color fill) {
		this.fillColor = fill;
	}

	/**
	 *  Construnctorul initializeaza elementele comune.
	 */
	public Ellipse(int x1, int y1, int x2, int y2, int size, int caract,
				   Color foreground, Color fillColor, int hc) {
		super(x1, y1, x2, y2, size, caract, foreground, hc);
		this.fillColor = fillColor;
		type = SC.GRP_Circle;
		this.UID = UID;
		jmiFigure.setText("Ellipse#" + Integer.toHexString((int) UID));
	}

	/**
	 *  Construnctorul initializeaza elementele comune. <br>
	 *  Obiectul este creat in alta parte. Deci putem sa setam UID'ul.
	 */
	public Ellipse(int x1, int y1, int x2, int y2, int size, int caract,
				   Color foreground, Color fillColor, long UID) {
		super(x1, y1, x2, y2, size, caract, foreground, 0);
		this.fillColor = fillColor;
		type = SC.GRP_Circle;
		this.UID = UID;
		jmiFigure.setText("Ellipse#" + Integer.toHexString((int) UID));

	}

	public int contains(int x, int y) {
		//daca (x, y) e in afara granitelor dreptunghiului e clar
		if (x + size / 2 + ERROR_LIMIT < Math.min(x1, x2) ||
				x - size / 2 - ERROR_LIMIT > Math.max(x1, x2) ||
				y + size / 2 + ERROR_LIMIT < Math.min(y1, y2) ||
				y - size / 2 - ERROR_LIMIT > Math.max(y1, y2))
			return OUT_OF_BOUNDS;

		int value2return = OUT_OF_BOUNDS;

		//verificam colturile:
		int corner = getCorner(x, y);
		if (corner != OUT_OF_BOUNDS)
			value2return |= corner;

		//verificam daca (x, y) nu este in exterior:
		if (equation(x - (Math.min(x1, x2) + getWidth() / 2.0),
					 y - (Math.min(y1, y2) + getHeight() / 2.0),
					 getWidth() / 2.0 + ERROR_LIMIT,
					 getHeight() / 2.0 + ERROR_LIMIT) > 0)
			return value2return | INSIDE_BOUNDS;
		//daca e plina e bun orice punct din interior:
		if (fillColor != null || Math.abs(x1 - x2) <= 2 * ERROR_LIMIT ||
				Math.abs(y1 - y2) <= 2 * ERROR_LIMIT)
			return value2return | FIGURE_HIT;
		//daca nu e plina... vrerificam sa nu fiie in interior...
		if (equation(x - (Math.min(x1, x2) + getWidth() / 2.0),
					 y - (Math.min(y1, y2) + getHeight() / 2.0),
					 getWidth() / 2.0 - ERROR_LIMIT,
					 getHeight() / 2.0 - ERROR_LIMIT) <= 0)
			return value2return | INSIDE_BOUNDS;
		//daca am ajuns aici .. atunci e pe linie..
		return value2return | FIGURE_HIT;
	}

	public String toSVG(String prefix) {
		return prefix+"<!-- filled ellipse -->\n" +
				prefix+"<ellipse cx=\"" + (x1 + x2) / 2 + "\" cy=\"" + (y1 + y2) / 2 + "\"" +
				" rx=\"" + Math.abs(x2 - x1) / 2 + "\" ry=\"" + Math.abs(y2 - y1) / 2 + "\"" +
				" style=\"stroke-width:" + size + "; fill:" +
				(fillColor == null? "none": "#" +
				Integer.toHexString(fillColor.getRGB()).substring(2, 8)) +
				"; stroke:#" + Integer.toHexString(foreground.getRGB()).substring(2, 8) +
				(getStyle(LINE_STYLE_MASK) == DOTTED_LINE? "; stroke-dasharray:" + size:
				(getStyle(LINE_STYLE_MASK) == DASHED_LINE? "; stroke-dasharray:" +
				(2 + size * 2):"")) +
				"\" />\n";
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

	public void send(com.hermix.HermixApi sender, String group) {
		Rect r = new Rect();
		r.x1 = Math.min(x1, x2);
		r.y1 = Math.min(y1, y2);
		r.x2 = r.x1 + this.getWidth();
		r.y2 = r.y1 + this.getHeight();
		java.util.Vector v = new java.util.Vector(3, 1);
		v.add(new Long(getUID()));
		v.add(new Integer(caract));
		sender.g_send_whiteboard_message(group, type, r, foreground,
										 size, fillColor == null?null:fillColor.getRGB() + "", v);
	}

	public void draw(Graphics2D g) {
		//alegem stilul de linie cu care vom desena:
		switch (getStyle(super.LINE_STYLE_MASK)) {
			case DASHED_LINE:
				float dash1[] = {10.0f};
				g.setStroke(new BasicStroke(this.size, BasicStroke.CAP_BUTT,
											BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f));
				break;
			case DOTTED_LINE:
				float dot1[] = {2.0f};
				g.setStroke(new BasicStroke(this.size, BasicStroke.CAP_BUTT,
											BasicStroke.JOIN_MITER, 1.0f, dot1, 0.0f));
				break;
			default:
				g.setStroke(new BasicStroke(this.size));
		}
		//daca elipsa este 'plina' atunci desenam dintai interorul:
		if (fillColor != null) {
			g.setPaint(fillColor);
			g.fillOval(x1 - size / 2, y1 - size / 2, getWidth() + size, getHeight() + size);
		}

		//culoarea si grosimea liniei
		g.setPaint(foreground);
		g.drawOval(x1, y1, getWidth() + 1, getHeight() + 1);

	}//draw

	public void updateToolConfig(WBListener listener, Whiteboard wb) {
		listener.actionPerformed(new ActionEvent(wb.jtbEllipse,
												 ActionEvent.ACTION_PERFORMED, "JToggleButtonChanged"));
		wb.jtbEllipse.setSelected(true);

		if ((getStyle(LINE_STYLE_MASK) & DOTTED_LINE) != 0)
			wb.jcbLineStyleChooser.setSelectedItem("Dotted");
		else if ((getStyle(LINE_STYLE_MASK) & DASHED_LINE) != 0)
			wb.jcbLineStyleChooser.setSelectedItem("Dashed");
		else
			wb.jcbLineStyleChooser.setSelectedItem("Solid");

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


	/**
	 * Metoda care returneaza valoarea unui punct folosind equatia elipsei.<BR>
	 * Daca valoarea este pozitiva, punctul este in exteriorul elipsei,
	 * daca este negativa, punctul este in interior. <br>
	 * Utilizatrul este cel care ia in calcul coeficientul de eroare
	 *  definit in clasa Figure.
	 * @param (dx, dy) punctul pentru care calculam valoarea (trebuie translatat
	 *      astfel incat sa avem o elipsa cu centrul in origine)
	 * @param (rx, ry) razele elipsei cu centrul in origine pentru care se
	 *      calculeaza valoarea.
	 * @see Figure#ERROR_LIMIT
	 */
	private static final double equation(double dx, double dy, double rx, double ry) {
		if (rx * ry == 0)
			return 0;
		return dx * dx / (rx * rx) + dy * dy / (ry * ry) - 1.0;
	}//equation
}//Ellipse

