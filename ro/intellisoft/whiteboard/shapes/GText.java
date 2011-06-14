
package ro.intellisoft.whiteboard.shapes;


import com.hermix.HermixApi;
import com.hermix.Rect;
import com.hermix.SC;
import ro.intellisoft.whiteboard.WBListener;
import ro.intellisoft.whiteboard.Whiteboard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

/**
 * <B>Title:        </B>Graphical Text class <br>
 * <B>Description:  </B>Abstractizarea unui text formatat.<br>
 * <B>Copyright:    </B>Copyright (c) 2001 <br>
 * <B>Company:      </B>Intellisoft <br>
 * @author Ovidiu Maxiniuc
 * @version 2.0
 */


public class GText extends Figure {

	/**
	 * Textul propriuzis ce va fi afisat.
	 */
	private String text = "";

	/**
	 * Fontul cu care desenez.
	 */
	private Font fontUsed = null;

	/**
	 * Numele fontului cu care desenez.
	 */
	private String fontName = "";

	/**
	 * Layout-ul pe care lucrez si care imi da toate infrmatiile legate de
	 * afisarea stringului pe ecran.
	 */
	private TextLayout layout = null;

	/**
	 *  Construnctorul initializeaza elementele comune.
	 */
	public GText(int x, int y, int size, int style, Color c, String text, String fontFace, int hc) {
		super(x, y, -1, -1, size, style, c, hc);
		this.text = text;
		if (text == null)
			this.text = "";
		fontName = fontFace;
		type = SC.GRP_GText;
		jmiFigure.setText("GText#" + Integer.toHexString((int) UID));
		updateTextLayout();
	}

	/**
	 *  Construnctorul initializeaza elementele comune. <br>
	 *  Obiectul este creat in alta parte. Deci putem sa setam UID'ul.
	 */
	public GText(int x, int y, int size, int style, Color c, String text, String fontFace, long UID) {
		super(x, y, -1, -1, size, style, c, 0);
		this.UID = UID;
		this.text = text;
		if (text == null)
			this.text = "";
		fontName = fontFace;
		type = SC.GRP_GText;
		jmiFigure.setText("GText#" + Integer.toHexString((int) UID));
		updateTextLayout();
	}

	public int contains(int x, int y) {
		int a = this.getTextLength();
		int b = this.getTextHeight();
		if (new Rectangle2D.Float(x1 - ERROR_LIMIT / 2, y1 - ERROR_LIMIT / 2, a + ERROR_LIMIT, b + ERROR_LIMIT).contains(x, y))
			return FIGURE_HIT | INSIDE_BOUNDS;
		else
			return OUT_OF_BOUNDS;
	}

	public String toSVG(String prefix) {
		int fontAttr = getStyle(FONT_MASK);
		return prefix + "<!-- GText -->\n" + prefix + "<text x=\"" + x1 + "\" y=\"" + (y1 + getTextHeight()) + "\" style=\"font-family:" + fontName + "; font-size:" + size + "; " + //is Bold?
				((fontAttr & BOLD) != 0 ? " font-weight:bold; ":"") + //is Italic?
				((fontAttr & ITALIC) != 0 ? " font-style:italic; ":"") + //is line-through or Underline?
				((fontAttr & (STRIKE | UNDERLINE)) != 0 ? " text-decoration:" + ((fontAttr & UNDERLINE) != 0 ? "underline ":"") + ((fontAttr & STRIKE) != 0 ? "line-through ":"") + ";":"") + " fill:#" + Integer.toHexString(foreground.getRGB()).substring(2, 8) + "\" >\n\t" + prefix + text + "\n" + prefix + "</text>\n";
	}

	public void send(HermixApi sender, String group) {
		Rect r = new Rect();
		r.x1 = x1;
		r.y1 = y1;
		r.x2 = x1 + this.getWidth();
		r.y2 = y1 + this.getHeight();
		java.util.Vector v = new java.util.Vector(4, 1);
		v.add(new Long(getUID()));
		v.add(new Integer(caract));
		v.add(fontName);
		sender.g_send_whiteboard_message(group, type, r, foreground, size, text, v);

	}

	public Vector toVector() {
		Vector v = new Vector();
		v.addElement(new Integer(type));
		v.addElement(new Long(getUID()));
		v.addElement(new Point(x1, y1));
		v.addElement(new Integer(caract));
		v.addElement(new Integer(size));
		v.addElement(fontName);
		v.addElement(text);
		v.addElement(foreground);
		return v;
	}

	public void draw(Graphics2D g) {
		g.setFont(fontUsed);
		int x = this.getTextLength();
		int y = this.getTextHeight();

		/*g.setPaint(Color.darkGray);
        g.drawRect(x1,y1,x,y);*/

		g.setPaint(foreground);
		int fontAttr = getStyle(FONT_MASK);//caracteristicile fontului memorate

		//desenez numai daca sirul nu este vid => layout!=null !
		if (layout != null) {
			g.drawString(text, (int) (x1 - layout.getBounds().getX()), (int) (y1 - layout.getBounds().getY()));

			if ((fontAttr & UNDERLINE) != 0) {//underline
				g.setStroke(new BasicStroke(size / 20 + 1));
				g.drawLine(x1, y1 + 2 - (int) (layout.getBaseline() + layout.getBounds().getY()), x1 + x, y1 + 2 - (int) (layout.getBaseline() + layout.getBounds().getY()));
			}
			if ((fontAttr & STRIKE) != 0) {//StrikeOut
				g.setStroke(new BasicStroke(size / 40 + 1));
				g.drawLine(x1, y1 - (int) (layout.getBaseline() + layout.getBounds().getY()) / 2, x1 + x, y1 - (int) (layout.getBaseline() + layout.getBounds().getY()) / 2);
			}//if strikeout
		}//if layout!=null
	}//draw


	public void updateToolConfig(WBListener listener, Whiteboard wb) {
		listener.actionPerformed(new ActionEvent(wb.jtbGText, ActionEvent.ACTION_PERFORMED, "JToggleButtonChanged"));
		wb.jtbGText.setSelected(true);

		wb.jtbItalic.setSelected((getStyle(FONT_MASK) & ITALIC) != 0);
		wb.jtbBold.setSelected((getStyle(FONT_MASK) & BOLD) != 0);
		wb.jtbUnderline.setSelected((getStyle(FONT_MASK) & UNDERLINE) != 0);
		wb.jtbStroke.setSelected((getStyle(FONT_MASK) & STRIKE) != 0);

		wb.jcbFontChooser.setSelectedItem(fontName);

		wb.jbForegroundColor.setForeground(foreground);

		int apprSize = 0;
		for (int i = 0; i < wb.jcbFontSizeChooser.getItemCount(); i++) {
			try {
				apprSize = Integer.parseInt(wb.jcbFontSizeChooser.getItemAt(i).toString());
			} catch (Exception ex) {
			}
			if (apprSize >= size)
				break;
		}
		wb.jcbFontSizeChooser.setSelectedItem("" + apprSize);
	}


	/**
	 * Metoda care permite sa setam textul afisat.
	 */
	public void setText(String text) {
		this.text = text;
		updateTextLayout();
	}

	/**
	 * Metoda care permite sa obtinem textul afisat.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Obtinerea marimii figurii (inaltimea) - cand este cazul
	 */
	public int getWidth() {
		return getTextLength();
	}

	/**
	 * Obtinerea marimii figurii (latimea) - cand este cazul
	 */
	public int getHeight() {
		return getTextHeight();
	}

	/**
	 * Metoda care permite sa obtinem fontul textului afisat.
	 */
	public String getFontName() {
		return fontName;
	}

	/**
	 * Metoda care permite sa setam fontul cu care textul este afisat.
	 */
	public void setFont(String fontFace) {
		this.fontName = fontFace;
		updateTextLayout();
	}

	/**
	 * Metoda care permite sa setam fontul cu care textul este afisat.
	 */
	public void setFontSize(int fontSize) {
		this.size = fontSize;
		updateTextLayout();
	}

	/**
	 * Metoda care permite sa setam stilul Bold pentru textul afisat.
	 */
	public void setBold(boolean boldOn) {
		if (boldOn)
			caract |= BOLD;
		else
			caract &= 0xFFFFFFFF ^ BOLD;
		updateTextLayout();
	}

	/**
	 * Metoda care permite sa setam stilul Italic pentru textul afisat.
	 */
	public void setItalic(boolean italicOn) {
		if (italicOn)
			caract |= ITALIC;
		else
			caract &= 0xFFFFFFFF ^ ITALIC;
		updateTextLayout();
	}

	/**
	 * Metoda care permite sa setam stilul Underline pentru textul afisat.
	 */
	public void setUnderline(boolean underlineOn) {
		if (underlineOn)
			caract |= UNDERLINE;
		else
			caract &= 0xFFFFFFFF ^ UNDERLINE;
		updateTextLayout();
	}

	/**
	 * Metoda care permite sa setam stilul Stroke pentru textul afisat.
	 */
	public void setStroke(boolean strokeOn) {
		if (strokeOn)
			caract |= STRIKE;
		else
			caract &= 0xFFFFFFFF ^ STRIKE;
		updateTextLayout();
	}


	/**
	 * Metoda care returneaza lungimea unui text formatat.
	 */
	public int getTextLength() {
		if (layout != null)
			return (int) (layout.getBounds().getWidth());
		else
			return 0;
	}

	/**
	 * Metoda care returneaza inaltimea unui text formatat.
	 */
	public int getTextHeight() {
		if (layout != null)
			return (int) layout.getBounds().getHeight();
		else
			return 0;
	}//getTextHeight

	/**
	 * Metoda care face updatarea asezarii textului in pagina.
	 */
	private void updateTextLayout() {
		int textAttr = Font.PLAIN; //caracteristicile fontului cu care vom desena
		int fontAttr = getStyle(FONT_MASK);//caracteristicile fontului memorate

		if ((fontAttr & BOLD) != 0)
			textAttr |= Font.BOLD;
		if ((fontAttr & ITALIC) != 0)
			textAttr |= Font.ITALIC;
		this.fontUsed = new Font(fontName, textAttr, size);
		if (text.length() > 0)
			layout = new TextLayout(text, fontUsed, new FontRenderContext(fontUsed.getTransform(), true, false));
		else
			layout = null;
	}

}//GText