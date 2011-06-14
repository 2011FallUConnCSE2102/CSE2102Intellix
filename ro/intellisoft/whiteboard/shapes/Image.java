
package ro.intellisoft.whiteboard.shapes;


import com.hermix.*;
import com.sun.image.codec.jpeg.JPEGCodec;
import ro.intellisoft.whiteboard.WBListener;
import ro.intellisoft.whiteboard.Whiteboard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;

/**
 * <B>Title:        </B>Line Figure <br>
 * <B>Description:  </B>Abstractizarea unui segment de dreapta<br>
 * <B>Copyright:    </B>Copyright (c) 2001 <br>
 * <B>Company:      </B>Intellisoft <br>
 * @author Ovidiu Maxiniuc
 * @version 2.0
 */


public class Image extends Figure {

	/**Imaginea propriuzisa*/
	private java.awt.Image image = null;

	/**Vom avea nevoie si de un observer care sa imi randeze imaginea..*/
	private javax.swing.JLabel observer = new javax.swing.JLabel();

	/**
	 *  Construnctorul initializeaza elementele comune. <br>
	 *  Uid-ul se stabileste altfel: in functie de hashcode-ul utilizatorului
	 *  si hashcode-ul numelui fisierului.
	 */
	public Image(int x1, int y1, int x2, int y2, long UID) {
		super(x1, y1, x2, y2, -1, 0, null, 0);
		type = SC.GRP_Image;
		jmiFigure.setText("Image#" + Integer.toHexString((int) UID));
		this.UID = UID;
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
		return value2return | FIGURE_HIT;
	}

	/**
	 * conversia la formatul SVG.
	 * fiecare figura stie sa se converteasca la un sir ce reprezinta
	 * codificare SVG a sa
	 */
	public String toSVG(String prefix) {
		return "<!-- image not available -->";
	}

	public Vector toVector() {
		Vector v = new Vector();
		v.addElement(new Integer(type));
		v.addElement(new Long(getUID()));
		v.addElement(new Point(x1, y1));
		v.addElement(new Point(x2, y2));
		return v;
	}



	/**
	 * conversia la formatul SVG.
	 * fiecare figura stie sa se converteasca la un sir ce reprezinta
	 * codificare SVG a sa si face si salvarea imaginii
	 */
	public String toSVG(String prefix, String filename) {
		if (image == null)
			return "";
		BufferedImage buf = new BufferedImage(image.getWidth(observer), image.getHeight(observer), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = buf.createGraphics();
		g.drawImage(image, 0, 0, observer);
		String JPEGFileName = new File(filename).getPath() + UID + ".jpg";
		try {
			File theFile = new File(JPEGFileName);//fisierul in care salvam
			// codam JPG si salvam in fisier
			JPEGCodec.createJPEGEncoder(new java.io.FileOutputStream(theFile)).encode(buf);
		} catch (java.io.IOException ioe) {
			System.out.println("Exception on writing JPG file :" + ioe.getMessage());
		}
		return prefix+"<!-- image -->\n" + prefix+"<image x=\"" + x1 + "\" y=\"" + y1 + "\" width=\"" + (x2 - x1) + "\" height=\"" + (y2 - y1) + "\" xlink:href=\"" + new File(JPEGFileName).getName() + "\" />\n";
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
		sender.g_send_whiteboard_message(group, type, r, null, 0, null, v);
	}

	/**
	 * metoda de desenare.
	 * presupun contextul grafic deja initializat cu valorile dorite
	 * imi setez singur pe cele private
	 * la iesirea din metoda nu se garanteaza ca atributele grfice
	 * sunt aceleasi ca cele de la apelare
	 */
	public void draw(Graphics2D g) {
		//daca avem o imagine o afisam, daca nu, nu!
		if (image != null)
			g.drawImage(image, x1, y1, x2 - x1, y2 - y1, observer);
		else {
			if (wb != null)
				g.setXORMode(wb.getBackground());
			g.setStroke(new BasicStroke(0.0f));
			g.setPaint(Color.black);
			g.drawRect(x1, y1, x2 - x1, y2 - y1);
			g.drawLine(x1, y1, x2, y2);
			g.drawLine(x2, y1, x1, y2);
			g.setPaintMode();
		}
	}//draw

	public void setImage(java.awt.Image img) {
		this.image = img;
	}

	/**
	 * Returneaza dimensiunile preferate de imagine...
	 * Daca sunt pozitive dimensiunea imaginii este scalata ca sa pastrazae
	 * proportiile.
	 * Daca prarametrii sunt negativi returnam dimensiunile reale ale imaginii.
	 */
	public java.awt.Dimension getPreferedSize(int x, int y) {
		if (x < 0 || y < 0)
			return new java.awt.Dimension(this.image.getWidth(observer), this.image.getHeight(observer));
		if (this.image == null || this.image.getHeight(observer) == 0 || this.image.getWidth(observer) == 0)
			return new java.awt.Dimension(x, y);
		double ratio = (this.image.getWidth(observer) + 0.0) / this.image.getHeight(observer);
		if ((x + 0.0) / y <= ratio)
			return new java.awt.Dimension((int) (y * ratio), y);
		else
			return new java.awt.Dimension(x, (int) (x / ratio));
	}


	/**
	 * Metoda care face transformarea unei figuri. In functie de type si de
	 * tipul figurii, x si y sunt interpretate diferit.
	 */
	public void transform(int type, int x, int y) {
		//fac aceleasi transformari ca si la un dreptunghi
		super.transform(type, x, y);
	}

	public void updateToolConfig(WBListener listener, Whiteboard wb) {
		listener.actionPerformed(new ActionEvent(wb.jtbImage, ActionEvent.ACTION_PERFORMED, "JToggleButtonChanged"));
		wb.jtbImage.setSelected(true);
		wb.jbResetImageSize.setEnabled(true);
	}


}//Image