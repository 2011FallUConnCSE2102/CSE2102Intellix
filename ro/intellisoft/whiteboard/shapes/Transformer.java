
package ro.intellisoft.whiteboard.shapes;

import com.hermix.HermixApi;
import com.hermix.Rect;

import java.awt.*;
import java.util.Vector;

/**
 * Title:        Unified Hermix Applet
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Intellisoft SRL
 * @author Intellisoft
 * @version 1.0
 */

public class Transformer extends Figure {

	/**
	 * Textul care va fi scris la GTExt.
	 */
	private String text = null;

	/**
	 * Culoarea de fill pentru figurile corespunzatoare.
	 */
	private Color fill;

	/**
	 * Numele fontului (numao pentru GTExt)
	 */
	private String fontName = "";

	/**
	 * Punctul de curbura.
	 */
	private int xc, yc;

	private Rect r = new Rect();

	public Transformer(int type, long UID, Rect r, Color c, int size, String text, Color fill, int caract, String fontName, int xc, int yc) {
		super(r.x1, r.y1, r.x2, r.y2, size, caract, c, 0);
		this.r = r;
		this.fill = fill;
		this.type = type;
		this.text = text;
		this.UID = UID;
		this.fontName = fontName;
		this.xc = xc;
		this.yc = yc;
	}

	public int contains(int x, int y) {
		return OUT_OF_BOUNDS;
	}

	public void send(HermixApi sender, String group) {
		r.x1 = x1;
		r.y1 = y1;
		r.x2 = x2;
		r.y2 = y2;
		java.util.Vector v = new java.util.Vector(5, 0);
		v.add(new Long(UID));
		v.add(new Integer(caract));
		if (fill != null && (fontName == null || fontName.equals("")))
			v.add(fill);
		else
			v.add(fontName);
		v.add(new java.awt.Point(xc, yc));
		sender.g_send_whiteboard_message(group, type, this.r, foreground, size, text, v);
	}

	public String toSVG(String prefix) {
		return "";
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
		v.addElement(fill);
		v.addElement(fontName);
		v.addElement(text);
		v.addElement(new Point(xc, yc));
		return v;
	}


	public void updateToolConfig(ro.intellisoft.whiteboard.WBListener listener, ro.intellisoft.whiteboard.Whiteboard wb) {
	}

	public void draw(java.awt.Graphics2D g) {
	}

	/**
	 * Metoda care permite sa setam fontul cu care textul este afisat. <br>
	 * Aplicabuil numai daca tramsformam o figura de ti Gtext
	 */
	public void setFont(String fontFace) {
		this.fontName = fontFace;
	}

	public void transform(int type, int w, int h) {
		//interpretez (x, y) ca fiind noile dimensiuni
		x2 = w;
		y2 = h;
	}

	/**
	 * Metoda care permite sa setam culoare de fill. <br>
	 * Aplicabuil numai daca tramsformam o figura de tip rectangle sau Ellipse
	 */
	public void setFillColor(Color fill) {
		this.fill = fill;
	}

	/**
	 * Metoda care ia cele 3 puncte dintr-un arc si le salveaza.
	 */
	public void saveArc(Arc f) {
		r.x1 = x1 = f.x1;
		r.x2 = x2 = f.x2;
		r.y1 = y1 = f.y1;
		r.y2 = y2 = f.y2;
		xc = f.xc;
		yc = f.yc;
	}

	public void updateFigure(Figure fig) {
		if (fig instanceof Arc) {
			fig.x1 = x1;
			fig.x2 = x2;
			((Arc) fig).xc = xc;
			fig.y1 = y1;
			fig.y2 = y2;
			((Arc) fig).yc = yc;
		} else {
			//interpretez (x2, y2) ca dimensiunile noi ale figurii:
			fig.transform(-1, x2 / 2, y2 / 2);
			fig.translateTo(x1, y1);
		}
		if (foreground != null)
			fig.setForeground(foreground);
		if (fig instanceof Ellipse)
			((Ellipse) fig).setFillColor(fill);
		else if (fig instanceof ro.intellisoft.whiteboard.shapes.Rectangle)
			((ro.intellisoft.whiteboard.shapes.Rectangle) fig).setFillColor(fill);
		fig.setSize(size);
		fig.setStyle(-1, caract);
		if (text != null && fig instanceof GText) {
			((GText) fig).setText(text);
			((GText) fig).setFont(fontName);
		}
	}
}