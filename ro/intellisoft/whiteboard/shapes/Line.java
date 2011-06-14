
package ro.intellisoft.whiteboard.shapes;


import com.hermix.*;
import ro.intellisoft.whiteboard.WBListener;
import ro.intellisoft.whiteboard.Whiteboard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Vector;

/**
 * <B>Title:        </B>Line Figure <br>
 * <B>Description:  </B>Abstractizarea unui segment de dreapta<br>
 * <B>Copyright:    </B>Copyright (c) 2001 <br>
 * <B>Company:      </B>Intellisoft <br>
 * @author Ovidiu Maxiniuc
 * @version 2.0
 */


public class Line extends Figure {

	/**
	 *  Construnctorul initializeaza elementele comune
	 */
	public Line(int x1, int y1, int x2, int y2, int size, int caract,
				Color foreground, int hc) {
		super(x1, y1, x2, y2, size, caract, foreground, hc);
		type = SC.GRP_Line;
		jmiFigure.setText("Line#" + Integer.toHexString((int) UID));
	}

	/**
	 *  Construnctorul initializeaza elementele comune. <br>
	 *  Obiectul este creat in alta parte. Deci putem sa setam UID'ul.
	 */
	public Line(int x1, int y1, int x2, int y2, int size, int caract,
				Color foreground, long UID) {
		super(x1, y1, x2, y2, size, caract, foreground, 0);
		type = SC.GRP_Line;
		jmiFigure.setText("Line#" + Integer.toHexString((int) UID));
		this.UID = UID;
	}


	public int contains(int x, int y) {
		if (x + size / 2 + ERROR_LIMIT < Math.min(x1, x2) ||
				x - size / 2 - ERROR_LIMIT > Math.max(x1, x2))
			return OUT_OF_BOUNDS;
		if (y + size / 2 + ERROR_LIMIT < Math.min(y1, y2) ||
				y - size / 2 - ERROR_LIMIT > Math.max(y1, y2))
			return OUT_OF_BOUNDS;

		int value2return = OUT_OF_BOUNDS;
		//verificam colturile:
		int corner = getCorner(x, y);
		if (corner != OUT_OF_BOUNDS && corner != LEFT_LOWER_CORNER &&
				corner != RIGHT_UPPER_CORNER)
			value2return |= corner;

		if (distance2(x1, y1, x2, y2, x, y) <
				(size + ERROR_LIMIT) * (size + ERROR_LIMIT) / 4)
			value2return |= FIGURE_HIT;
		return value2return;
	}

	/**
	 * conversia la formatul SVG.
	 * fiecare figura stie sa se converteasca la un sir ce reprezinta
	 * codificare SVG a sa
	 */
	public String toSVG(String prefix) {
		return prefix+"<!-- simple line-->\n" +
				prefix+"<line x1=\"" + x1 + "\" y1=\"" + y1 + "\" x2=\"" + x2 + "\" y2=\"" + y2 + "\"" +
				" style=\"stroke-width:" + size + "; fill:none; " +
				" stroke:#" + Integer.toHexString(foreground.getRGB()).substring(2, 8) +
				(getStyle(LINE_STYLE_MASK) == DOTTED_LINE? "; stroke-dasharray:" + size:
				(getStyle(LINE_STYLE_MASK) == DASHED_LINE? "; stroke-dasharray:" +
				(2 + size * 2):"")) +
				(getStyle(START_HEAD_ARROW) != 0? "; marker-start:url(#StartArrow)":"") +
				(getStyle(END_HEAD_ARROW) != 0?"; marker-end:url(#EndArrow)":"") +
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
		return v;
	}



	public void send(HermixApi sender, String group) {
		Rect r = new Rect();
		r.x1 = x1;
		r.y1 = y1;
		r.x2 = x2;
		r.y2 = y2;
		java.util.Vector v = new java.util.Vector(3, 1);
		v.add(new Long(getUID()));
		v.add(new Integer(caract));
		sender.g_send_whiteboard_message(group, type, r, foreground,
										 size, null, v);
	}

	/**
	 * Obtinerea marimii figurii (inaltimea) - cand este cazul
	 */
	public int getHeight() {
		return y2 - y1;
	}

	/**
	 * Obtinerea marimii figurii (latimea) - cand este cazul
	 */
	public int getWidth() {
		return x2 - x1;
	}

	/**
	 * metoda de desenare.
	 * presupun contextul grafic deja initializat cu valorile dorite
	 * imi setez singur pe cele private
	 * la iesirea din metoda nu se garanteaza ca atributele grfice
	 * sunt aceleasi ca cele de la apelare
	 */
	public void draw(Graphics2D g) {
		//culoarea si grosimea liniei
		g.setPaint(foreground);
		switch (getStyle(super.LINE_STYLE_MASK)) {
			case DASHED_LINE:
				float dash1[] = {10.0f};
				g.setStroke(new BasicStroke(this.size, BasicStroke.CAP_BUTT,
											BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f));
				break;
			case DOTTED_LINE:
				float dot1[] = {size};
				g.setStroke(new BasicStroke(this.size, BasicStroke.CAP_BUTT,
											BasicStroke.JOIN_MITER, 1.0f, dot1, 0.0f));
				break;
			default:
				g.setStroke(new BasicStroke(this.size));
		}
		g.drawLine(x1, y1, x2, y2);

		//eventual tratez sageti
		g.setStroke(new BasicStroke(this.size));
		if (getStyle(START_HEAD_ARROW) != 0)
			drawArrow(g, x2, y2, x1, y1, size);
		if (getStyle(END_HEAD_ARROW) != 0)
			drawArrow(g, x1, y1, x2, y2, size);
	}//draw

	/**
	 * Metoda care afiseaza chenarul in cazul unei figuri ce se editeaza... <br>
	 * De fapt deseaza cate un partatel cu dimensuinea thickness in
	 * extremitatile figurii.
	 */
	public void drawBorder(Graphics2D g, int thickness) {
		g.setPaintMode();
		g.setStroke(new BasicStroke(thickness / 10.0f));
		int Tsize = thickness / 2;
		g.setPaint(Color.white);
		g.fillRect(x1 - Tsize, y1 - Tsize, thickness, thickness);
		g.fillRect(x2 - Tsize, y2 - Tsize, thickness, thickness);
		g.fillOval((x1 + x2) / 2 - Tsize, (y1 + y2) / 2 - Tsize, thickness, thickness);

		g.setPaint(Color.black);
		g.drawRect(x1 - Tsize, y1 - Tsize, thickness, thickness);
		g.drawRect(x2 - Tsize, y2 - Tsize, thickness, thickness);
		g.drawOval((x1 + x2) / 2 - Tsize, (y1 + y2) / 2 - Tsize, thickness, thickness);
	}//drawBorder

	public void updateToolConfig(WBListener listener, Whiteboard wb) {
		listener.actionPerformed(new ActionEvent(wb.jtbLine,
												 ActionEvent.ACTION_PERFORMED, "JToggleButtonChanged"));
		wb.jtbLine.setSelected(true);

		if ((getStyle(LINE_STYLE_MASK) & DOTTED_LINE) != 0)
			wb.jcbLineStyleChooser.setSelectedItem("Dotted");
		else if ((getStyle(LINE_STYLE_MASK) & DASHED_LINE) != 0)
			wb.jcbLineStyleChooser.setSelectedItem("Dashed");
		else
			wb.jcbLineStyleChooser.setSelectedItem("Solid");

		if (getStyle(ARROW_MASK) == TWO_HEADED_ARROW)
			wb.jcbArrowStyleChooser.setSelectedItem("Both Arrows");
		else if (getStyle(ARROW_MASK) == START_HEAD_ARROW)
			wb.jcbArrowStyleChooser.setSelectedItem("Start Arrow");
		else if (getStyle(ARROW_MASK) == END_HEAD_ARROW)
			wb.jcbArrowStyleChooser.setSelectedItem("End Arrow");
		else
			wb.jcbArrowStyleChooser.setSelectedItem("Simple");


		wb.jbForegroundColor.setForeground(foreground);

		wb.jcbLineSizeChooser.setSelectedItem("" + size);

	}


}//Line