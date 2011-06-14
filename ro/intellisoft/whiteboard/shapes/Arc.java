
package ro.intellisoft.whiteboard.shapes;


import com.hermix.Rect;
import com.hermix.SC;
import ro.intellisoft.whiteboard.WBListener;
import ro.intellisoft.whiteboard.Whiteboard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.QuadCurve2D;
import java.util.Vector;

/**
 * <B>Title:        </B>Arc Figure <br>
 * <B>Description:  </B>Abstractizarea unui arc de hiperbola<br>
 * <B>Copyright:    </B>Copyright (c) 2001 <br>
 * <B>Company:      </B>Intellisoft <br>
 * @author Ovidiu Maxiniuc
 * @version 2.0
 */


public class Arc extends Figure {

	/**
	 * Punctele de curbura.
	 */
	protected int xc, yc;

	/**
	 *  Construnctorul initializeaza elementele comune
	 */
	public Arc(int x1, int y1, int xc, int yc, int x2, int y2, int size, int caract,
			   Color foreground, int hc) {
		super(x1, y1, x2, y2, size, caract, foreground, hc);
		type = SC.GRP_Arc;
		this.xc = xc;
		this.yc = yc;
		jmiFigure.setText("Arc#" + Integer.toHexString((int) UID));
	}

	/**
	 *  Construnctorul initializeaza elementele comune. <br>
	 *  Obiectul este creat in alta parte. Deci putem sa setam UID'ul.
	 */
	public Arc(int x1, int y1, int xc, int yc, int x2, int y2, int size, int caract,
			   Color foreground, long UID) {
		super(x1, y1, x2, y2, size, caract, foreground, 0);
		type = SC.GRP_Arc;
		this.xc = xc;
		this.yc = yc;
		jmiFigure.setText("Arc#" + Integer.toHexString((int) UID));
		this.UID = UID;
	}


	public int contains(int x, int y) {
		int val2return = OUT_OF_BOUNDS;
		//verificam colturile first
		if (Math.abs(x - x1) < 2 * ERROR_LIMIT && Math.abs(y - y1) < 2 * ERROR_LIMIT)
			val2return |= LEFT_UPPER_CORNER;
		if (Math.abs(x - x2) < 2 * ERROR_LIMIT && Math.abs(y - y2) < 2 * ERROR_LIMIT)
			val2return |= RIGHT_LOWER_CORNER;
		int xcc = ((x1 + x2) / 2 + xc) / 2;
		int ycc = ((y1 + y2) / 2 + yc) / 2;
		if (Math.abs(x - xcc) < 2 * ERROR_LIMIT && Math.abs(y - ycc) < 2 * ERROR_LIMIT)
			val2return |= CURVATURE_CORNER;

		/**
		 * to do here: aproximat mai bine curba...
		 */
		if (new QuadCurve2D.Float(x1, y1, xc, yc, x2, y2).intersects
				(x - ERROR_LIMIT, y - ERROR_LIMIT, 2 * ERROR_LIMIT, 2 * ERROR_LIMIT))
			val2return |= FIGURE_HIT;

		return val2return;
	}

	/**
	 * conversia la formatul SVG.
	 * fiecare figura stie sa se converteasca la un sir ce reprezinta
	 * codificare SVG a sa
	 */
	public String toSVG(String prefix) {
		return prefix+"<!-- curve -->\n" +
				prefix+"<path d=\"M" + x1 + "," + y1 + " Q" + xc + "," + yc + " " + x2 + "," + y2 +
				"\" style=\"stroke-width:" + size + "; fill:none; " +
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
		v.addElement(new Point(xc, yc));
		v.addElement(new Integer(caract));
		v.addElement(new Integer(size));
		v.addElement(foreground);
		return v;
	}

	public void send(com.hermix.HermixApi sender, String group) {
		Rect r = new Rect();
		r.x1 = x1;
		r.y1 = y1;
		r.x2 = x2;
		r.y2 = y2;
		java.util.Vector v = new java.util.Vector(4, 1);
		v.add(new Long(getUID()));
		v.add(new Integer(caract));
		v.add(new Point(xc, yc));
		sender.g_send_whiteboard_message(group, type, r, foreground,
										 size, null, v);
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
		g.draw(new QuadCurve2D.Float(x1, y1, xc, yc, x2, y2));

		//eventual tratez sageti
		g.setStroke(new BasicStroke(this.size));
		if (getStyle(START_HEAD_ARROW) != 0)
			drawArrow(g, xc, yc, x1, y1, size);
		if (getStyle(END_HEAD_ARROW) != 0)
			drawArrow(g, xc, yc, x2, y2, size);
	}//draw


	/**
	 * Metoda care face transformarea unei figuri. In functie de type si de
	 * tipul figurii, x si y sunt interpretate diferit.
	 */
	public void transform(int point, int x, int y) {
		//interpretez x si y ca fiind noua pozitie a punctului point
		if ((point & LEFT_UPPER_CORNER) != 0) {// ~(x1, y1)
			xc += (x1 - x) / 2;
			yc += (y1 - y) / 2;
			x1 = x;
			y1 = y;
		} else if ((point & RIGHT_LOWER_CORNER) != 0) {// ~(x2, y2)
			xc += (x2 - x) / 2;
			yc += (y2 - y) / 2;
			x2 = x;
			y2 = y;
		} else if ((point & CURVATURE_CORNER) != 0) {// ~(xc, yc)
			xc = 2 * x - (x1 + x2) / 2;
			yc = 2 * y - (y1 + y2) / 2;
		}
	}

	/**
	 * Metoda de deplasare (translatare) a unei figuri pana la un anumit punct.
	 */
	public void translateTo(int x, int y) {
		translateWith(x - getPosition().x, y - getPosition().y);
	}

	/**
	 * Aceasta figura trebuie sa isi faca singura translatarea.
	 * Nu putem folosi translatarea de la figura generala.
	 * @see Figure#translate(int x, int y)
	 * @param dx nr de pixeli cu care facem translatia pe coordonata Ox
	 * @param dy nr de pixeli cu care facem translatia pe coordonata Oy
	 */
	public void translateWith(int dx, int dy) {
		super.translateWith(dx, dy);
		xc += dx;
		yc += dy;
	}//translate

	/**
	 * Metoda care afiseaza chenarul in cazul unei figuri ce se editeaza... <br>
	 * De fapt deseaza cate un partatel cu dimensuinea thickness in
	 * extremitatile figurii.
	 */
	public void drawBorder(Graphics2D g, int thickness) {
		int nxc = ((x1 + x2) / 2 + xc) / 2;
		int nyc = ((y1 + y2) / 2 + yc) / 2;
		int size = thickness / 2;
		g.setPaintMode();
		g.setStroke(new BasicStroke(thickness / 10.0f));

		g.setPaint(Color.white);
		g.fillRect(x1 - size, y1 - size, thickness, thickness);
		g.fillRect(x2 - size, y2 - size, thickness, thickness);
		g.fillOval(nxc - size, nyc - size, thickness, thickness);

		g.setPaint(Color.black);
		g.drawRect(x1 - size, y1 - size, thickness, thickness);
		g.drawRect(x2 - size, y2 - size, thickness, thickness);
		g.drawOval(nxc - size, nyc - size, thickness, thickness);


	}//drawBorder

	/**
	 * Obtinerea coltul initial (de obicei cel din dreapta-sus)
	 */
	public Point getPosition() {
		int nxc = ((x1 + x2) / 2 + xc) / 2;
		int nyc = ((y1 + y2) / 2 + yc) / 2;
		return new Point(Math.min(x1, Math.min(x2, nxc)),
						 Math.min(y1, Math.min(y2, nyc)));
	}

	/**
	 * Obtinerea marimii figurii (inaltimea) - cand este cazul
	 */
	public int getWidth() {
		int nxc = ((x1 + x2) / 2 + xc) / 2;
		int xm = getPosition().x;
		int xM = Math.max(x1, Math.max(x2, nxc));
		return xM - xm;
	}

	/**
	 * Obtinerea marimii figurii (latimea) - cand este cazul
	 */
	public int getHeight() {
		int nyc = ((y1 + y2) / 2 + yc) / 2;
		int ym = getPosition().y;
		int yM = Math.max(y1, Math.max(y2, nyc));
		return yM - ym;
	}

	public void updateToolConfig(WBListener listener, Whiteboard wb) {
		listener.actionPerformed(new ActionEvent(wb.jtbArc,
												 ActionEvent.ACTION_PERFORMED, "JToggleButtonChanged"));
		wb.jtbArc.setSelected(true);

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


}//Arc