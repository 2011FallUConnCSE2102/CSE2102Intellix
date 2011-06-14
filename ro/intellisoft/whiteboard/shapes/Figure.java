
package ro.intellisoft.whiteboard.shapes;

import com.hermix.Rect;
import com.hermix.SC;
import ro.intellisoft.whiteboard.WBListener;
import ro.intellisoft.whiteboard.Whiteboard;
import ro.intellisoft.intelliX.IntelliX;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Vector;

/**
 * <B>Title:        </B>New Hermix Applet <br>
 * <B>Description:  </B>The second version of the Hermix Applet <br>
 * <B>Copyright:    </B>Copyright (c) 2001 <br>
 * <B>Company:      </B>Intellisoft <br>
 * @author Ovidiu Maxiniuc
 * @version 2.0
 */


public abstract class Figure implements Serializable, ActionListener {

	/**
	 * Identificator unic.
	 * Fiecare utilizator isi creeaza propriile uid-uri dupa algoritmul:
	 * <ol> <li>in momentul constructiei WB:
	 *   <pre>long t0 = System.currentTimeMillis();</pre></li>
	 * <li> in momentul obtinerii nick-ului:
	 *   <pre>int hc = username.hashCode();</pre></li>
	 * <li> in momentul crearii unei figuri:
	 *   <pre>long t1 = System.currentTimeMillis();</pre></li>
	 * <li><pre>figura.setUID( (t1-t0) | (hc*0x100000000L) );</pre></li>
	 * </ol><hr>
	 * Note si explicatii:<pre>
	 *         long UID:
	 *         +-------------------------------++------------------------------+
	 *         |         dt = t1 - t0          ||              hc              |
	 *         +-------------------------------++------------------------------+
	 *  biti:  0                                32                            64
	 * </pre>
	 * <b>Observatii:</b>
	 * <ul> <li> daca dt < 25 de zile, timpul in ms se poate scrie pe 32 octeti. </li>
	 * <li>HashCode este un intreg pe 32 octeti care vor ocupa partea superioara a (long)UID </li>
	 * </ol>
	 */
	protected long UID = 0;

	/* Deprecated:
	 * Time to live
	 * Din momentul in care utilizatorul a desenat figura, aceasta are un
	 * anumit timp in care traieste independenta.
	 * Daca serverul nu returneaza un UID, dupa expirarea TTL, aceasta
	 * va disparea.
	 * DE FAPT, TTL = milisecunda dupa care figura nu mai este 'valabila'
	 */
	//private long TTL=0;

	/**
	 *  Tipul figurii
	 *  Probabil nu va mai fi neaparat necesar, fiecare figura
	 *  stiindu-si deja constanta simbolica
	 *  Poate fi folosita pentru eventuale sub-tipuri
	 */
	protected int type = -1;

	/**
	 * Caracteristicile figurii.<br>
	 * De exemplu: nr de capete cu sageata (when applicable)
	 * caracteristicile textului formatat: bold, italic, etc... <br>
	 * Se utilizeza masti de biti definite in obiectele respective.
	 */
	protected int caract = 0;

	/**
	 * Dimensiunea figurii.
	 * De fapt grosimea liniei pentru mjoritatea figurilor
	 * pentru text, dimensiunea in puncte a fontului
	 * Deocamdata se va folosi numai 'Arial', posibil in versiunile
	 * urmatoare sa se transmita si tipul de caracter intr-un
	 * parametru separat.
	 */
	protected int size = 0;

	/**
	 * Limitele in care se inscrie figura,
	 * de fapt vor fi folosite ca extreme pentru figuri.
	 */
	protected int x1, y1, x2, y2;

	/**
	 * Culoarea cu care este desenata figura
	 * la figurile 'pline' este culoarea kenarului,
	 * culoarea de umplere va fi definita in clasa respectiva.
	 */
	protected Color foreground = null;

	/*
	 * constante simbolice pentru atributele de desenare:
	 */

	/**
	 * Constanta simbolica pentru un segment sau un arc
	 * cu un capatul initial in forma de sageata.
	 */
	public static final int START_HEAD_ARROW = 0x0001;

	/**
	 * Constanta simbolica pentru un segment sau un arc
	 * cu un capatul final in forma de sageata.
	 */
	public static final int END_HEAD_ARROW = 0x0002;

	/**
	 * Constanta simbolica pentru un segment sau un arc
	 * cu ambele capete in forma de sageata.
	 */
	public static final int TWO_HEADED_ARROW = 0x0003;

	/**
	 * Constanta simbolica pentru un segment sau un arc
	 * simplu, ie fara capete in forma de sageata.
	 */
	public static final int NO_HEADED_ARROW = 0x0000;

	/*
	pentru a desena cu linie punctata se foloseste cam asa ceva:

	final static float dash1[] = {10.0f};
	final static BasicStroke dashed = new BasicStroke(this.size,
		BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
		dash1, 0.0f);
	*/

	/**
	 * Constanta simbolica pentru un segment sau un arc
	 * trasat cu linie intrerupta.
	 */
	public static final int DASHED_LINE = 0x0004;

	/**
	 * Constanta simbolica pentru un segment sau un arc
	 * trasat cu linie punctata.
	 */
	public static final int DOTTED_LINE = 0x0008;

	/**
	 * Constanta simbolica pentru un segment sau un arc
	 * trasat cu linie normala.
	 */
	public static final int SOLID_LINE = 0x0000;

	/**
	 * Constanta simbolica pentru un colt de dreptunghi
	 * ascutit (normal).
	 */
	public static final int NORMAL_CORNER = 0x0000;

	/**
	 * Constanta simbolica pentru un colt de dreptunghi
	 * rotunjit.
	 */
	public static final int ROUNDED_CORNER = 0x0010;

	/**
	 * Constanta simbolica pentru a obtine atributele pentru
	 * masca de biti cu atributele sagetilor.
	 */
	public static final int ARROW_MASK = 0x0003;

	/**
	 * Constanta simbolica pentru a obtine atributele pentru
	 * masca de biti cu atributele stilurilor de linie.
	 */
	public static final int LINE_STYLE_MASK = 0x000C;

	/**
	 * Constanta simbolica pentru a obtine atributele pentru
	 * masca de biti cu atributele coltului.
	 */
	public static final int CORNER_MASK = 0x0010;

	/**
	 * Constanta simbolica pentru a obtine atributele pentru
	 * masca de biti cu atributele fontului.
	 */
	public static final int FONT_MASK = 0x0F00;

	/**
	 * Constanta simbolica pentru a obtine stilul de font ingrosat (bold).
	 */
	public static final int BOLD = 0x0100;

	/**
	 * Constanta simbolica pentru a obtine stilul de font inclinat (italic).
	 */
	public static final int ITALIC = 0x0200;

	/**
	 * Constanta simbolica pentru a obtine stilul de font subliniat.
	 */
	public static final int UNDERLINE = 0x0400;

	/**
	 * Constanta simbolica pentru a obtine stilul de font taiat (STRIKETROUGHT).
	 */
	public static final int STRIKE = 0x0800;


	/**
	 * Constanta simbolica care ne da aproximarea la apartenenta
	 * unui punct la o dreapta (figura).
	 * @see Figure#contains(int x, int y)
	 */
	protected static final int ERROR_LIMIT = 3;

	/**
	 * Constanta ce reprezinta faptul ca punctul este in afara figurii si
	 * in afara dreprubghiului de incadrare.
	 */
	public static final int OUT_OF_BOUNDS = 0x00;

	/**
	 * Constanta ce reprezinta faptul ca punctul este pe figura
	 */
	public static final int FIGURE_HIT = 0x100;

	/**
	 * Constanta simbolica care spune ca a fost atins coltul stanga-sus.
	 */
	public static final int LEFT_UPPER_CORNER = 0x01;

	/**
	 * Constanta simbolica care spune ca a fost atins coltul dreapta-sus.
	 */
	public static final int RIGHT_UPPER_CORNER = 0x02;

	/**
	 * Constanta simbolica care spune ca a fost atins coltul stanga-jos.
	 */
	public static final int LEFT_LOWER_CORNER = 0x04;

	/**
	 * Constanta simbolica care spune ca a fost atins coltul dreapta-jos.
	 */
	public static final int RIGHT_LOWER_CORNER = 0x08;

	/**
	 * Constanta simbolica care spune ca a fost atins punctul de curbura.
	 */
	public static final int CURVATURE_CORNER = 0x10;

	/**
	 * Cnstanta simbolica care spune daca a fost atins oricare duintre
	 * cele 5 puncte importante.
	 */
	public static final int ANY_CORNER_HIT = LEFT_UPPER_CORNER | RIGHT_UPPER_CORNER | LEFT_LOWER_CORNER | RIGHT_LOWER_CORNER | CURVATURE_CORNER;

	/**
	 * Constanta ce reprezinta faptul ca punctul este in interiorul
	 * dreptunghiului ce incadreaza figura, dar nu pe figura
	 */
	public static final int INSIDE_BOUNDS = 0x20;

	/**
	 * Whiteboard-ul parinte. De pe el se sterge o figura/ se skimba
	 * adancimea (toBack/toFront) si se afiseaza meniul.
	 */
	protected Whiteboard wb = null;

	/**
	 * Timpul initial de la care se calculeaza UID.
	 * @see Figure#UID
	 */
	private static final long t0 = System.currentTimeMillis();

	/**
	 * Timpul la care a fost facuta ultima figura. <br>
	 * Se foloseste pentru a nu avea 2 figuri pentru acelasi utilizator cu
	 * acelasi id de timp.
	 */
	private static long t2 = t0;

	/* Deprecated:
	 * constanta simbolica reprezentand timpul de viata implicit in msec
	 * implicit este setata la 1min=60sec=60000msec
	 */
	//public static final long defaultTTL=60000;

	/**
	 * Meniul ce va aparea la click dreapta pe figura this.
	 */
	protected JPopupMenu jpmFigure = new JPopupMenu();

	/**
	 * Item disabled (titlu) care afiseaza pe ce s-a dat click dreapta.
	 */
	protected JMenuItem jmiFigure = new JMenuItem("Figure");

	/**
	 * Item care permite editarea figurii selectate:
	 */
	protected JMenuItem jmiEdit = new JMenuItem("Edit");

	/**
	 * Item care sterge figura de pe whiteboard.
	 */
	protected JMenuItem jmiDelete = new JMenuItem("Delete");

	/**
	 * Item care aduca figura in primplan.
	 */
	protected JMenuItem jmiToFront = new JMenuItem("To front");

	/**
	 * Item care aduca figura in planul ultim.
	 */
	protected JMenuItem jmiToBack = new JMenuItem("To back");

	/**
	 * Constructor care primeste ca argumente toate proprietatile comune.<br>
	 * seteaza automat UID'ul.
	 * @see Figure#UID
	 */
	public Figure(int x1, int y1, int x2, int y2, int size, int caract, Color c, int hc) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.size = size;
		this.foreground = c;
		this.caract = caract;
		long t1 = System.currentTimeMillis();
		if (t1 <= t2) {
			//daca este o eroare de ceas, returnam urlmatoare us ...
			t1 = t2;
		}
		//pregatim pentru urmatoare figura...
		t2 = t1 + 1;
		UID = (t1 - t0) | (hc * 0x100000000L);
		//System.out.println(UID);

		//facem meniul contextual:
		jpmFigure.add(jmiFigure);
		jpmFigure.addSeparator();
		jpmFigure.add(jmiEdit);
		jpmFigure.add(jmiDelete);
		jpmFigure.addSeparator();
		jpmFigure.add(jmiToFront);
		jpmFigure.add(jmiToBack);

		jmiFigure.setEnabled(false);
		jmiFigure.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 14));
		jmiEdit.addActionListener(this);
		jmiDelete.addActionListener(this);
		jmiToFront.addActionListener(this);
		jmiToBack.addActionListener(this);
	}


	//public Figure(){}

	/**
	 * Metoda de desenare.<br>
	 * Fiecare figura stie sa se deseneze utilizand contextul grafic. <br>
	 * Figurile isi seteaza singure culorile.<br>
	 * La iesirea din metoda nu se garanteaza ca atributele grfice
	 * sunt aceleasi ca cele de la apelare.<br>
	 */
	public abstract void draw(Graphics2D g);

	/**
	 * Metoda care afiseaza chenarul in cazul unei figuri ce se editeaza... <br>
	 * De fapt deseaza cate un partatel cu dimensuinea thickness in
	 * extremitatile figurii.
	 */
	public void drawBorder(Graphics2D g, int thickness) {
		int x = getPosition().x;
		int y = getPosition().y;
		int Tsize = thickness / 2;
		int w = getWidth();
		int h = getHeight();
		g.setPaintMode();

		g.setStroke(new BasicStroke(thickness / 10.0f));
		g.setPaint(Color.white);
		g.fillRect(x - Tsize, y - Tsize, thickness, thickness);
		g.fillRect(x + w - Tsize, y - Tsize, thickness, thickness);
		g.fillRect(x + w - Tsize, y + h - Tsize, thickness, thickness);
		g.fillRect(x - Tsize, y + h - Tsize, thickness, thickness);
		g.fillOval(x + getWidth() / 2 - Tsize, y + getHeight() / 2 - Tsize, thickness, thickness);

		g.setPaint(Color.black);
		g.drawRect(x - Tsize, y - Tsize, thickness, thickness);
		g.drawRect(x + w - Tsize, y - Tsize, thickness, thickness);
		g.drawRect(x + w - Tsize, y + h - Tsize, thickness, thickness);
		g.drawRect(x - Tsize, y + h - Tsize, thickness, thickness);
		g.drawOval(x + getWidth() / 2 - Tsize, y + getHeight() / 2 - Tsize, thickness, thickness);

		g.setStroke(new BasicStroke(0));
		g.drawRect(x, y, w, h);
	}

	/**
	 * Conversia la formatul SVG.<br>
	 * Fiecare figura stie sa se converteasca la un sir ce reprezinta codificare SVG a sa.
	 */
	public final String toSVG(){
		return toSVG("");
	}

	public abstract Vector toVector();

	public static Figure createFigureFromVector(Vector v){
		int type = ((Integer)v.elementAt(0)).intValue();
		long UID = ((Long)v.elementAt(1)).longValue();
		switch (type){
			case SC.GRP_Arc:
				int x1 = ((Point)v.elementAt(2)).x;
				int y1 = ((Point)v.elementAt(2)).y;
				int x2 = ((Point)v.elementAt(3)).x;
				int y2 = ((Point)v.elementAt(3)).y;
				int xc = ((Point)v.elementAt(4)).x;
				int yc = ((Point)v.elementAt(4)).y;
				int car = ((Integer)v.elementAt(5)).intValue();
				int size = ((Integer)v.elementAt(6)).intValue();
				Color col = (Color)v.elementAt(7);
				return new Arc(x1, y1, xc, yc, x2, y2, size, car, col, UID);
			case SC.GRP_Background:
				return new Background((Color)v.elementAt(2));
			case SC.GRP_Clear:
				return new Clear();
			case SC.GRP_Circle:
				x1 = ((Point)v.elementAt(2)).x;
				y1 = ((Point)v.elementAt(2)).y;
				x2 = ((Point)v.elementAt(3)).x;
				y2 = ((Point)v.elementAt(3)).y;
				car = ((Integer)v.elementAt(4)).intValue();
				size = ((Integer)v.elementAt(5)).intValue();
				col = (Color)v.elementAt(6);
				Color fill = (Color)v.elementAt(7);
				return new Ellipse(x1, y1, x2, y2, size, car, col, fill, UID);
			case SC.GRP_Delete_Figure:
				return new Eraser(UID);
			case SC.GRP_Free:
				car = ((Integer)v.elementAt(2)).intValue();
				size = ((Integer)v.elementAt(3)).intValue();
				col = (Color)v.elementAt(4);
                return new FreeHand((Vector)v.elementAt(5),size, car, col, UID);
			case SC.GRP_GText:
				x1 = ((Point)v.elementAt(2)).x;
				y1 = ((Point)v.elementAt(2)).y;
				car = ((Integer)v.elementAt(3)).intValue();
				size = ((Integer)v.elementAt(4)).intValue();
				String fontname = (String)v.elementAt(5);
				String text = (String)v.elementAt(6);
				col = (Color)v.elementAt(7);
				return new GText(x1, y1, size, car, col, text, fontname, UID);
			case SC.GRP_Image:
				x1 = ((Point)v.elementAt(2)).x;
				y1 = ((Point)v.elementAt(2)).y;
				x2 = ((Point)v.elementAt(3)).x;
				y2 = ((Point)v.elementAt(3)).y;
				return new Image(x1, y1, x2, y2, UID);
			case SC.GRP_Line:
				x1 = ((Point)v.elementAt(2)).x;
				y1 = ((Point)v.elementAt(2)).y;
				x2 = ((Point)v.elementAt(3)).x;
				y2 = ((Point)v.elementAt(3)).y;
				car = ((Integer)v.elementAt(4)).intValue();
				size = ((Integer)v.elementAt(5)).intValue();
				col = (Color)v.elementAt(6);
				return new Line(x1, y1, x2, y2, size, car, col, UID);
			case SC.GRP_Rectangle:
				x1 = ((Point)v.elementAt(2)).x;
				y1 = ((Point)v.elementAt(2)).y;
				x2 = ((Point)v.elementAt(3)).x;
				y2 = ((Point)v.elementAt(3)).y;
				car = ((Integer)v.elementAt(4)).intValue();
				size = ((Integer)v.elementAt(5)).intValue();
				col = (Color)v.elementAt(6);
				fill = (Color)v.elementAt(7);
				return new Rectangle(x1, y1, x2, y2, size, car, col, fill, UID);
			case SC.GRP_Transform_All:
				x1 = ((Point)v.elementAt(2)).x;
				y1 = ((Point)v.elementAt(2)).y;
				x2 = ((Point)v.elementAt(3)).x;
				y2 = ((Point)v.elementAt(3)).y;
				car = ((Integer)v.elementAt(4)).intValue();
				size = ((Integer)v.elementAt(5)).intValue();
				col = (Color)v.elementAt(6);
				fill = (Color)v.elementAt(7);
				fontname = (String)v.elementAt(8);
				text = (String)v.elementAt(9);
				xc = ((Point)v.elementAt(10)).x;
				yc = ((Point)v.elementAt(10)).y;
				return new Transformer(type, UID, new Rect(x1, y1, x2, y2), col, size, text, fill, car, fontname, xc, yc);
		}
		return null;
	}

	/**
	 * Conversia la formatul SVG.<br>
	 * Fiecare figura stie sa se converteasca la un sir ce reprezinta codificare SVG a sa.
	 * @param prefix un prefix care trebuie pus inaintea fecarei linii
	 */
	public abstract String toSVG(String prefix);

	/**
	 * Metoda folosita pentru 'apucarea' unei figuri cu mouse-ul
	 * de pe ecran. Ea intorce o constanta intreaga ce reprezinta
	 * ce parte a figurii contine un anumit punct de coordionate (x, y).
	 */
	public abstract int contains(int x, int y);

	/**
	 * Metoda folosita pentru 'apucarea' unei figuri cu mouse-ul
	 * de pe ecran. Ea intorce o constanta intreaga ce reprezinta
	 * ce parte a figurii contine un anumit punct de coordionate (x, y).
	 */
	public final int contains(Point p) {
		return contains(p.x, p.y);
	}

	/**
	 * Metoda de verificare a apartenentei unui punct la colturi...
	 */
	public int getCorner(int x, int y) {
		if (Math.abs(x - getPosition().x) < 2 * ERROR_LIMIT && Math.abs(y - getPosition().y) < 2 * ERROR_LIMIT)
			return LEFT_UPPER_CORNER;
		if (Math.abs(x - getWidth() - getPosition().x) < 2 * ERROR_LIMIT && Math.abs(y - getPosition().y) < 2 * ERROR_LIMIT)
			return RIGHT_UPPER_CORNER;
		if (Math.abs(x - getPosition().x) < 2 * ERROR_LIMIT && Math.abs(y - getHeight() - getPosition().y) < 2 * ERROR_LIMIT)
			return LEFT_LOWER_CORNER;
		if (Math.abs(x - getWidth() - getPosition().x) < 2 * ERROR_LIMIT && Math.abs(y - getHeight() - getPosition().y) < 2 * ERROR_LIMIT)
			return RIGHT_LOWER_CORNER;
		return OUT_OF_BOUNDS;
	}

	/**
	 * Metoda de deplasare (translatare) a unei figuri pana la un anumit punct.
	 */
	public void translateTo(int x, int y) {
		x2 += x - x1;
		y2 += y - y1;
		x1 = x;
		y1 = y;
	}

	/**
	 * Metoda de deplasare (translatare) a unei figuri cu un anumit vector.
	 */
	public void translateWith(int dx, int dy) {
		x2 += dx;
		y2 += dy;
		x1 += dx;
		y1 += dy;
	}

	/**
	 * Metoda care face transformarea unei figuri. In functie de type si de
	 * tipul figurii, x si y sunt interpretate diferit.
	 */
	public void transform(int type, int x, int y) {
		//interpretez x si y ca fiind noua distanta de la centru...
		int xc = getPosition().x + getWidth() / 2;
		int yc = getPosition().y + getHeight() / 2;
		//si fac o scalare...
		x1 = xc - x;
		y1 = yc - y;
		x2 = xc + x;
		y2 = yc + y;
	}

	/**
	 * Metoda de deplasare (translatare) a unei figuri cu un anumit vector.
	 */
	public final void translateWith(Point p) {
		translateWith(p.x, p.y);
	}

	/**
	 * Metoda care testeaza daca doua figuri sunt egale logic. <br>
	 * Suprascriu metoda Object.equals(Object).
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Figure))
			return false;
		Figure f = (Figure) o;
		return x1 == f.x1 && x2 == f.x2 && y1 == f.y1 && y2 == f.y2 && size == f.size && caract == f.caract && type == f.type;
	}

	/**
	 *  Daca intru in modul de editare trebuie sa setez paleta de instrumente
	 *  conform cu configuratia actuala a figurii pe care o editez.
	 */
	public abstract void updateToolConfig(WBListener listener, Whiteboard wb);

	/**
	 * Metoda care trimite pe teava figura curenta.
	 */
	public abstract void send(com.hermix.HermixApi sender, String group);

	/*
	* Setters & getters
	*/

	/**
	 * Metoda care imi da dimensiunile preferate ale unei figuri.
	 * In cazul general va fi maximul dintre parametri (dimesiunile actuale)
	 * pe X s Y.
	 */
	public java.awt.Dimension getPreferedSize(int x, int y) {
		return new java.awt.Dimension(Math.max(x, y), Math.max(x, y));
	}

	/**
	 * Setarea culoarii de desenare:
	 */
	public void setForeground(Color c) {
		this.foreground = c;
	}

	/**
	 * Setarea dimensiunii
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Obtinerea culorii de desenare:
	 */
	public Color getForeground() {
		return this.foreground;
	}

	/**
	 * Obtinerea coltul initial (de obicei cel din dreapta-sus)
	 */
	public Point getPosition() {
		return new Point(x1, y1);
	}

	/**
	 * Obtinerea marimii figurii (inaltimea) - cand este cazul
	 */
	public int getHeight() {
		return Math.abs(y2 - y1);
	}

	/**
	 * Obtinerea marimii figurii (latimea) - cand este cazul
	 */
	public int getWidth() {
		return Math.abs(x2 - x1);
	}

	/**
	 * Obtinerea UID-ului.
	 * @see Figure#UID
	 */
	public final long getUID() {
		return UID;
	}

	/**
	 * Metoada care returneaza tipul fugurii. <br>
	 * Fiecare figura trebuie sa isi initializeze tipul in constructor.
	 */
	public final int getType() {
		return type;
	}

	/**
	 * Metoda care returnraza stilul unei figuri in functie de masca aplicata.
	 * @param style_mask masca pentru care se doreste stilul.
	 */
	public int getStyle(int style_mask) {
		return caract & style_mask;
	}

	/**
	 * Metoda care seteaza stilul unei figuri in functie de masca aplicata.<br>
	 * Metodele de desenare nu vor lua in considerare decat stilurile proprii
	 * figurilor respective. Deci, kiar daca se admite setarea de stiliri ce nu
	 * apartin unei anumite figuri, ele vor fi ignorate.
	 * @param style_mask masca pentru care se doreste setaera stilului.
	 * @param style stilul ce se doreste a fi aplicat.
	 */
	public void setStyle(int style_mask, int style) {
		caract = (caract & (style_mask ^ 0xFFFFFFFF)) | style;
	}

	/* Deprecated:
	 * Metoda care testeaza daca figura mai este viabila
	 * ie. daca nu a expirat TTL
	 *
	public boolean hasExpired(){
		if (TTL==-1)
			return false;
		else
			return System.currentTimeMillis() >= TTL;
	}
	*/

	/**
	 * Metoda care afiseaza meniul contextual.
	 */
	public void showMenu(int x, int y, Whiteboard wb, Figure selectedFigure) {
		this.wb = wb;
		if (selectedFigure == this)
			jmiEdit.setEnabled(false);
		else
			jmiEdit.setEnabled(true);
		jpmFigure.show(wb, x, y);
		try {
			jmiDelete.setIcon(IntelliX.loadImageResource(wb, "images/whiteboarddelete.gif"));
			jmiToFront.setIcon(IntelliX.loadImageResource(wb, "images/whiteboardtofront.gif"));
			jmiToBack.setIcon(IntelliX.loadImageResource(wb, "images/whiteboardtoback.gif"));
			jmiEdit.setIcon(IntelliX.loadImageResource(wb, "images/whiteboardedit.gif"));
		} catch (Exception ex) {
		}
	}

	/**
	 * Invoked when an action occurs. <br>
	 * Deocamdata numai evenimente de meniu.
	 */
	public void actionPerformed(ActionEvent e) {
		Object theActioner = e.getSource();
		if (theActioner == jmiDelete) {
			wb.send(new Eraser(this.UID));
			wb.remove(this);
		} else if (theActioner == jmiToBack) {
			wb.send(new Transformer(SC.GRP_2Back_Figure, UID, new Rect(), null, -1, null, null, -1, null, 0, 0));
			wb.changeDepth(this, 0);
		} else if (theActioner == jmiToFront) {
			wb.send(new Transformer(SC.GRP_2Front_Figure, UID, new Rect(), null, -1, null, null, -1, null, 0, 0));
			wb.changeDepth(this, -1);
		} else if (theActioner == jmiEdit) {
			try {
				wb.editingFigure = this;
				//setam instrumentele de editare:
				wb.setStatus(Whiteboard.EDIT);
			} catch (Exception ex) {}
		}
	}

	/**
	 * Metoda care returneaza un obiect ce rprezinta transformarea necesara
	 * pentru a ajunge de la un obiect din clasa crenta la obiectul curent.
	 */
	public Transformer getTransformer() {
		Rect r = new Rect();
		r.x1 = getPosition().x;
		r.y1 = getPosition().y;
		r.x2 = getWidth();
		r.y2 = getHeight();
		return new Transformer(SC.GRP_Transform_All, UID, r, foreground, size, (this instanceof GText?((GText) this).getText():null),
			(this instanceof Ellipse? ((Ellipse) this).fillColor :  (this instanceof Rectangle? ((Rectangle) this).fillColor :null)), this.getStyle(-1), (this instanceof GText?((GText) this).getFontName():""), 0, 0);
	}

	/**
	 * Pentru ca sa desenez un capat de sageata la capatul (x_2, y_2). <Br>
	 * Presupunem ca contextul grafic este pregatit (culoare, brush).
	 */
	static void drawArrow(Graphics2D g2, int x_1, int y_1, int x_2, int y_2, int size) {
		int dx = x_1 - x_2,
				dy = y_1 - y_2;
		if (dy == 0 && dx == 0) //nu pot calcula panta dreptei support
		// fortez! 'deplasez' cu un pixel un punct pe coordonata x
			dx = 1;
		//calculam unghiul pe care il face segmentul si proiectia pe axe Bx si By
		double sin_m = dy / Math.sqrt(dx * dx + dy * dy),
				cos_m = dx / Math.sqrt(dx * dx + dy * dy),
				Bx = x_2 + 2 * size * cos_m,
				By = y_2 + 2 * size * sin_m;
		g2.drawLine(x_2, y_2, (int) (Bx - 2 * size * sin_m), (int) (By + 2 * size * cos_m));
		g2.drawLine(x_2, y_2, (int) (Bx + 2 * size * sin_m), (int) (By - 2 * size * cos_m));
	}

	/**
	 * Metoda care calculeaza patratul distantei dintre un punct si o dreapta.
	 * @param ((x1, y1) (x2, y2)) dreapta ce trece prin aste 2 puncte.
	 * @param (x, y) punctul de la care calculez diatanta
	 */
	protected static final double distance2(int x1, int y1, int x2, int y2, int x, int y) {
		/*metoda de rezolvare :
		* notez A=(x1,y1), B=(x2, y2), C=(x,y)
		* D = piciorul inaltimii din C in tr. ABC
		* Calculez AD (folosind 2* Pitagora)
		* si apoi scot CD=inaltimea=distanta care ne intereseaza*/
		double AB = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

		// daca punctele coincid, distanta este egala cu distanta la unul din puncte
		if (AB == 0)
			return (x1 - x) * (x1 - x) + (y1 - y) * (y1 - y);
		double AC2 = (x1 - x) * (x1 - x) + (y1 - y) * (y1 - y); //asta e la patrat
		double BC2 = (x - x2) * (x - x2) + (y - y2) * (y - y2); //asta e la patrat
		double AD = (AB + (AC2 - BC2) / AB) / 2;
		return AC2 - AD * AD;
	}
}