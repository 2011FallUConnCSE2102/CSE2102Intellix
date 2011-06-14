
package ro.intellisoft.whiteboard.shapes;

import com.hermix.*;
import ro.intellisoft.whiteboard.WBListener;
import ro.intellisoft.whiteboard.Whiteboard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.QuadCurve2D;
import java.util.Vector;


/**
 * <B>Title:        </B>Free Hand Drawing <br>
 * <B>Description:  </B>Clasa care abstractizeaza o linie trasata
 *          cu mana de utilizator<br>
 * <B>Copyright:    </B>Copyright (c) 2001 <br>
 * <B>Company:      </B>Intellisoft <br>
 * @author Ovidiu Maxiniuc
 * @version 2.0
 */


public class FreeHand extends Figure {

	/**
	 * Punctele prin care trece aceasta figura sunt stocate in acest vector:
	 */
	private Vector points = null;

	/**
	 * Coeficientul pe Ox cu care este scalata figura.
	 */
	private double scaleX = 1.0;

	/**
	 * Coeficientul pe Oy cu care este scalata figura.
	 */
	private double scaleY = 1.0;

	/**
	 * Coeficientul pe Ox cu care este translata figura.
	 */
	private int shiftX = 0;

	/**
	 * Coeficientul pe Oy  cu care este translata figura.
	 */
	private int shiftY = 0;

	/**
	 * Inaltimea initiala.
	 */
	private int absoluteHeight = 0;

	/**
	 * Latimea initiala.
	 */
	private int absoluteWidth = 0;

	/**
	 *  Construnctorul initializeaza elementele comune
	 */
	public FreeHand(Vector points, int size, int caract, Color foreground, int hc) {
		super(-1, -1, -1, -1, size, caract, foreground, hc);
		type = SC.GRP_Free;
		this.points = preProcess(points);
		jmiFigure.setText("FreeHand#" + Integer.toHexString((int) UID));
	}

	/**
	 *  Construnctorul initializeaza elementele comune. <br>
	 *  Obiectul este creat in alta parte. Deci putem sa setam UID'ul.
	 */
	public FreeHand(Vector points, int size, int caract, Color foreground, long UID) {
		super(-1, -1, -1, -1, size, caract, foreground, 0);
		type = SC.GRP_Free;
		this.points = preProcess(points);
		this.UID = UID;
		jmiFigure.setText("FreeHand#" + Integer.toHexString((int) UID));
	}

	/**
	 * Metoda de preprocesare. <br>
	 * Se deplaseaza punctele a.i. centrul figurii sa fie in origine.
	 * Se initializeaza x1, y1, x2, y2
	 */
	private Vector preProcess(Vector points) {
		try {
			if (points.elementAt(points.size() - 4) instanceof Double &&
					points.elementAt(points.size() - 3) instanceof Double) {
				//in acest caz vectorul ar trebui sa fie deja preprocesat
				//in plus trebuie sa aibe valorile de preprocesare deja stokate...
				scaleX = ((Double) points.elementAt(points.size() - 4)).doubleValue();
				scaleY = ((Double) points.elementAt(points.size() - 3)).doubleValue();
				absoluteWidth = ((Point) points.elementAt(points.size() - 2)).x;
				absoluteHeight = ((Point) points.elementAt(points.size() - 2)).y;
				shiftX = ((Point) points.elementAt(points.size() - 1)).x;
				shiftY = ((Point) points.elementAt(points.size() - 1)).y;
				points.remove(points.size() - 1);
				points.remove(points.size() - 1);
				points.remove(points.size() - 1);
				points.remove(points.size() - 1);
				return points;
			}
		} catch (Exception ex) {
			//daca nu reusesc convertirea asa .. incerc in modul clasik:
		}
		;
		Vector toReturn = new Vector(points.size() * 3 / 2, 10);
		x1 = x2 = ((Point) points.elementAt(0)).x;
		y1 = y2 = ((Point) points.elementAt(0)).y;
		for (int i = 1; i < points.size(); i++) {
			int x = ((Point) points.elementAt(i)).x;
			if (x < x1)
				x1 = x;
			else if (x > x2)
				x2 = x;
			int y = ((Point) points.elementAt(i)).y;
			;
			if (y < y1)
				y1 = y;
			else if (y > y2)
				y2 = y;
		}
		absoluteWidth = x2 - x1;
		absoluteHeight = y2 - y1;
		if (absoluteWidth < 1)
			absoluteWidth = 1;
		if (absoluteHeight < 1)
			absoluteHeight = 1;
		shiftX = absoluteWidth / 2 + x1;
		shiftY = absoluteHeight / 2 + y1;
		scaleX = 1;
		scaleY = 1;

		//in acest moment avem limitele x1, x2, y1, y2. facem translatia:
		for (int i = 0; i < points.size(); i++) {
			int x = ((Point) points.elementAt(i)).x - shiftX;
			int y = ((Point) points.elementAt(i)).y - shiftY;
			toReturn.add(new Point(x, y));
		}
		return toReturn;
	}

	/**
	 * Metoda care returneaza coordonata X a punctului cu indexul idx
	 */
	private int getX(int idx) {
		try {
			return (int) (shiftX + ((Point) (points.elementAt(idx))).x * scaleX);
		} catch (Exception ex) {
			return -1;
		}
	}

	/**
	 * Metoda care returneaza coordonata Y a punctului cu indexul idx
	 */
	private int getY(int idx) {
		try {
			return (int) (shiftY + ((Point) (points.elementAt(idx))).y * scaleY);
		} catch (Exception ex) {
			return -1;
		}
	}

	public int contains(int x, int y) {
		//verificam prima data dreptunghiul D incadrare:
		if (!new java.awt.geom.Rectangle2D.Float(getPosition().x - ERROR_LIMIT / 2,
												 getPosition().y - ERROR_LIMIT / 2, getWidth() + ERROR_LIMIT,
												 getHeight() + ERROR_LIMIT).contains(x, y))
			return OUT_OF_BOUNDS;

		//verificam colturile:
		int value2return = OUT_OF_BOUNDS;
		int corner = getCorner(x, y);
		if (corner != OUT_OF_BOUNDS)
			value2return |= corner;

		//verificam stilul de desenare a 'path'ului
		if (getStyle(CORNER_MASK) != ROUNDED_CORNER) {
			for (int i = 0; i < points.size() - 1; i++) {
				try {//verificam daca este un vector de puncte
					int x1 = getX(i),
							y1 = getY(i),
							x2 = getX(i + 1),
							y2 = getY(i + 1);
					if (x + size / 2 + ERROR_LIMIT < Math.min(x1, x2) ||
							x - size / 2 - ERROR_LIMIT > Math.max(x1, x2))
						continue;
					if (y + size / 2 + ERROR_LIMIT < Math.min(y1, y2) ||
							y - size / 2 - ERROR_LIMIT > Math.max(y1, y2))
						continue;
					if (distance2(x1, y1, x2, y2, x, y) <
							(size + ERROR_LIMIT) * (size + ERROR_LIMIT) / 4)
						return value2return |= FIGURE_HIT;
				} catch (Exception e) {
				}//daca nu este nu facem nika
			}
		} else {//ROUNDED_CORNER
			//testam linia franta prin curbe
			//exceptzie fac prima si ultima linie
			try {//ca sa nu ma mai complic cu testarea nr. de segmente
				x1 = getX(0);
				y1 = getY(0);
				x2 = getX(1);
				y2 = getY(1);
				//ma intereseaza numai prima jumate de segment:
				x2 = (x1 + x2) / 2;
				y2 = (y1 + y2) / 2;
				//atentie la 3 if-urile imbricate:
				if (x + size / 2 + ERROR_LIMIT > Math.min(x1, x2) &&
						x - size / 2 - ERROR_LIMIT < Math.max(x1, x2))
					if (y + size / 2 + ERROR_LIMIT > Math.min(y1, y2) &&
							y - size / 2 - ERROR_LIMIT < Math.max(y1, y2))
						if (distance2(x1, y1, x2, y2, x, y) <
								(size + ERROR_LIMIT) * (size + ERROR_LIMIT) / 4)
							return value2return |= FIGURE_HIT;
				//^am testat prima jumate de segment
				//continuam sa testam celelalte segmente
				//prin curbe tangente
				for (int i = 1; i < points.size() - 1; i++) {
					x1 = getX(i - 1);
					y1 = getY(i - 1);
					x2 = getX(i + 1);
					y2 = getY(i + 1);
					int xc = getX(i),
							yc = getY(i);
					if (new QuadCurve2D.Float((x1 + xc) / 2, (y1 + yc) / 2, xc, yc,
											  (x2 + xc) / 2, (y2 + yc) / 2).intersects(x - ERROR_LIMIT,
																					   y - ERROR_LIMIT, 2 * ERROR_LIMIT, 2 * ERROR_LIMIT))
						return value2return | FIGURE_HIT;
				}
				x1 = getX(points.size() - 2);
				y1 = getY(points.size() - 2);
				x2 = getX(points.size() - 1);
				y2 = getY(points.size() - 1);
				//ma intereseaza numai ultima jumate de segment:
				x1 = (x1 + x2) / 2;
				y1 = (y1 + y2) / 2;
				//atentie la 3 if-urile imbricate:
				if (x + size / 2 + ERROR_LIMIT > Math.min(x1, x2) &&
						x - size / 2 - ERROR_LIMIT < Math.max(x1, x2))
					if (y + size / 2 + ERROR_LIMIT > Math.min(y1, y2) &&
							y - size / 2 - ERROR_LIMIT < Math.max(y1, y2))
						if (distance2(x1, y1, x2, y2, x, y) <
								(size + ERROR_LIMIT) * (size + ERROR_LIMIT) / 4)
							return value2return | FIGURE_HIT;
			} catch (Exception e) {
			}
		}
		return value2return | INSIDE_BOUNDS;
	}

	public String toSVG(String prefix) {
		StringBuffer toReturn = new StringBuffer(prefix+"<!--  free hand drawings -->"+prefix+"\n <path d=\"M");
		try {
			if (false){// disable rounded-corner-savings: getStyle(CORNER_MASK) == ROUNDED_CORNER) {
				//trasam prima jumatate de segment:
				x1 = getX(0);
				y1 = getY(0);
				x2 = getX(1);
				y2 = getY(1);
				toReturn.append(x1 + "," + y1 + " L" + (x1 + x2) / 2 + "," + (y1 + y2) / 2);

				for (int i = 1; i < points.size() - 1; i++) {
					x1 = getX(i - 1);
					y1 = getY(i - 1);
					x2 = getX(i + 1);
					y2 = getY(i + 1);
					int xc = getX(i),
						yc = getY(i);
					toReturn.append(" Q" + xc + "," + yc + " " + (x2 + xc) / 2 + "," + (y2 + yc) / 2);
				}

				//si la urma ultima jumatate:
				x2 = getX(points.size() - 1);
				y2 = getY(points.size() - 1);
				toReturn.append(" L" + x2 + "," + y2);
			} else { //NORMAL_CORNER
				x1 = getX(0);
				y1 = getY(0);
				toReturn.append(x1 + "," + y1 + " L");

				for (int i = 1; i < points.size(); i++)
					toReturn.append(" " + getX(i) + "," + getY(i));

			}
		} catch (Exception e) {
			//daca e ceva in neregula returnam sirul vid.
			return "";
		}
		toReturn.append("\" style=\"stroke-width:").append(size).append("; fill:none; stroke:#");
		toReturn.append(Integer.toHexString(foreground.getRGB()).substring(2, 8));
		toReturn.append((getStyle(LINE_STYLE_MASK) == DOTTED_LINE? "; stroke-dasharray:" + size:
				(getStyle(LINE_STYLE_MASK) == DASHED_LINE? "; stroke-dasharray:" +
				(2 + size * 2):"")));
		toReturn.append((getStyle(START_HEAD_ARROW) != 0? "; marker-start:url(#StartArrow)":"") +
				(getStyle(END_HEAD_ARROW) != 0?"; marker-end:url(#EndArrow)":""));
		toReturn.append("\" />\n");
		return toReturn.toString();
	}

	/**
	 * Obtinerea coltul initial (de obicei cel din dreapta-sus)
	 */
	public Point getPosition() {
		return new Point((int) (shiftX - absoluteWidth * scaleX / 2),
						 (int) (shiftY - absoluteHeight * scaleY / 2));
	}

	/**
	 * Obtinerea marimii figurii (inaltimea) - cand este cazul
	 */
	public int getHeight() {
		return (int) (absoluteHeight * scaleY);
	}

	/**
	 * Obtinerea marimii figurii (latimea) - cand este cazul
	 */
	public int getWidth() {
		return (int) (absoluteWidth * scaleX);
	}


	/**
	 * Metoda care face transformarea unei figuri. In functie de type si de
	 * tipul figurii, x si y sunt interpretate diferit.
	 */
	public void transform(int type, int x, int y) {
		//interpretez x si y ca fiind noua distanta de la centru...
		int xc = getPosition().x + getWidth() / 2;
		int yc = getPosition().y + getHeight() / 2;

		scaleX = 2.0 * x / absoluteWidth;
		scaleY = 2.0 * y / absoluteHeight;

		//si fac o scalare...
		x1 = getPosition().x;
		y1 = getPosition().y;
		x2 = x1 + getWidth();
		y2 = y1 + getHeight();
	}

	public void send(HermixApi sender, String group) {
		Rect r = new Rect();
		r.x1 = getX(0);
		r.y1 = getY(0);
		r.x2 = r.x1 + this.getWidth();
		r.y2 = r.y1 + this.getHeight();

		java.util.Vector v = new java.util.Vector(points.size(), 10);
		for (int i = 0; i < points.size(); i++)
			v.add(new Point(getX(i), getY(i)));

		v.add(new Long(getUID()));
		v.add(new Integer(caract));
		sender.g_send_whiteboard_message(group, type, r, foreground,
										 size, null, v);
	}

	public Vector toVector() {
		Vector v = new Vector();
		v.addElement(new Integer(type));
		v.addElement(new Long(getUID()));
		v.addElement(new Integer(caract));
		v.addElement(new Integer(size));
		v.addElement(foreground);
		Vector v2 = new Vector();
		for (int i = 0; i < points.size(); i++){
			v2.add(new Point(getX(i), getY(i)));
		}
		v.addElement(v2);
		return v;
	}



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

		//verificam stilul de desenare a 'path'ului
		if (getStyle(CORNER_MASK) != ROUNDED_CORNER) {
			for (int i = 0; i < points.size() - 1; i++)
				try {//verificam daca este un vector de puncte
					int x1 = getX(i),
							y1 = getY(i),
							x2 = getX(i + 1),
							y2 = getY(i + 1);
					g.drawLine(x1, y1, x2, y2);
					Stroke saveStroke = g.getStroke();
					g.setStroke(new BasicStroke(this.size));
					if (i == 0 && getStyle(START_HEAD_ARROW) != 0)
						drawArrow(g, x2, y2, x1, y1, size);
					if (i == points.size() - 2 && getStyle(END_HEAD_ARROW) != 0)
						drawArrow(g, x1, y1, x2, y2, size);
					g.setStroke(saveStroke);
				} catch (Exception e) {
				}//daca nu este nu facem nika
		} else { //ROUNDED_CORNER
			//rotunjim linia franta prin curbe
			//exceptzie fac prima si ultima linie
			try {//ca sa nu ma mai complic cu testarea nr. de segmente
				int x1 = getX(0),
						y1 = getY(0),
						x2 = getX(1),
						y2 = getY(1);
				g.drawLine((x1 + x2) / 2, (y1 + y2) / 2, x1, y1);
				if (getStyle(START_HEAD_ARROW) != 0) {
					Stroke saveStroke = g.getStroke();
					g.setStroke(new BasicStroke(this.size));
					drawArrow(g, x2, y2, x1, y1, size);
					g.setStroke(saveStroke);
				}
				//^am desenat prima jumate de segment
				//continuam sa unim jumatatile celorlalte segmente
				//prin curbe tangente
				for (int i = 1; i < points.size() - 1; i++) {
					x1 = getX(i - 1);
					y1 = getY(i - 1);
					x2 = getX(i + 1);
					y2 = getY(i + 1);
					int xc = getX(i),
							yc = getY(i);
					g.draw(new QuadCurve2D.Float((x1 + xc) / 2, (y1 + yc) / 2, xc, yc,
												 (x2 + xc) / 2, (y2 + yc) / 2));
				}
				//desenam ultima jumatate de segment
				x1 = getX(points.size() - 2);
				y1 = getY(points.size() - 2);
				x2 = getX(points.size() - 1);
				y2 = getY(points.size() - 1);
				g.drawLine(x2, y2, (x1 + x2) / 2, (y1 + y2) / 2);
				if (getStyle(END_HEAD_ARROW) != 0) {
					g.setStroke(new BasicStroke(this.size));
					drawArrow(g, x1, y1, x2, y2, size);
				}
			} catch (Exception e) {
			}
		}//ROUNDED_CORNER
	}//draw


	public void updateToolConfig(WBListener listener, Whiteboard wb) {
		listener.actionPerformed(new ActionEvent(wb.jtbFreehand,
												 ActionEvent.ACTION_PERFORMED, "JToggleButtonChanged"));
		wb.jtbFreehand.setSelected(true);

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

		wb.jtbRoundedCorner.setSelected(getStyle(CORNER_MASK) == ROUNDED_CORNER);
		wb.jbForegroundColor.setForeground(foreground);
		wb.jcbLineSizeChooser.setSelectedItem("" + size);
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
		shiftX += dx;
		shiftY += dy;
		x1 += dx;
		y1 += dy;
		x2 += dx;
		y2 += dy;
	}//translate
}//FreeHand