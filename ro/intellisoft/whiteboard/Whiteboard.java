
package ro.intellisoft.whiteboard;

import com.hermix.Rect;
import com.hermix.SC;
import com.hermix.HermixApi;
import ro.intellisoft.whiteboard.shapes.*;
import ro.intellisoft.whiteboard.shapes.Image;
import ro.intellisoft.whiteboard.event.WhiteboardListener;
import ro.intellisoft.whiteboard.event.WhiteboardEvent;
import ro.intellisoft.intelliX.IntelliX;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.util.Vector;

/**
 * Title:        Whiteboard
 * Description:  The second version of the Whiteboard
 * Copyright:    Copyright (c) 2001
 * Company:      Intellisoft
 * @author Ovidiu Maxiniuc
 * @version 3.01sta
 */

public class Whiteboard extends JComponent implements Scrollable, Printable {

	/**Tablou cu toate fonturile disponibile.*/
	private static final String[] fontArray = {"Arial", "Courier New", "Helvetica", "Fixedsys", "Gothic", "Lucida Console", "System", "Tahoma", "Terminal", "Times New Roman", "Utopia", "Verdana"};

	/**Tablou cu toate dimensiunile de font disponibile.*/
	private static final String[] fontSizeArray = {"5", "8", "10", "12", "14", "18", "24", "36", "72", "144"};

	/**Tablou cu dimensiunile de zoom-are a whiteboard-ului.*/
	private static final String[] zoomArray = {"1%", "10%", "25%", "50%", "100%", "2x", "5x", "10x", "100x"};

	/** Tablou cu toate tipuile de sageti disponibile.*/
	private static final String[] arrowStyleArray = {"Simple", "Start Arrow", "End Arrow", "Both Arrows"};

	/**Tablou cu toate stilurile de linie disponibile.*/
	private static final String[] lineStyleArray = {"Solid", "Dashed", "Dotted"};

	/**Tablou cu toate dimensiunile de linie disponibile.*/
	private static final String[] lineSizeArray = {"1", "2", "3", "4", "5", "10", "25", "50", "100"};

	/**
	 * Constanta simbolica pentru starea interna de asteptare.
	 * @see Whiteboard#status
	 */
	public static final int IDLE = 0;

	/**
	 * Constanta simbolica pentru starea interna de zoom'ing.
	 * @see Whiteboard#status
	 */
	public static final int ZOOM = 1;

	/**
	 * Constanta simbolica pentru starea interna de panning (shift).
	 * @see Whiteboard#status
	 */
	public static final int SHIFT = 2;

	/**
	 * Constanta simbolica pentru starea interna de mutare a unei figuri.
	 * @see Whiteboard#status
	 */
	public static final int MOVE = 3;

	/**
	 * Constanta simbolica pentru starea interna de stergere a unei figuri.
	 * @see Whiteboard#status
	 */
	public static final int KILL = 4;

	/**
	 * Constanta simbolica pentru starea interna de editare a unei figuri.
	 * @see Whiteboard#status
	 */
	public static final int EDIT = 5;

	/**
	 * Constanta simbolica care desemneaza ca tragem cu mousul curbura unui
	 * arc de cerc.
	 * @see Whiteboard#status
	 */
	public static final int CARC = 6;

	/**
	 * Constanta simbolica pentru starea interna de slideshow.
	 * @see Whiteboard#status
	 * @see SideShow#SideShow(Whiteboard)
	 */
	public static final int SLIDE_SHOW = 7;

	/**
	 * Constanta simbolica pentru a bloca iesirea dintr-o anumitra stare...
	 * @see Whiteboard#setStatus,
	 */
	public static final int BLOCK_STATUS = 8;

	/**Memoreaza stilul BOLD pentru text grafic formatat.*/
	protected boolean bold = false;

	/**Memoreaza stilul ITALIC pentru text grafic formatat.*/
	protected boolean italic = false;

	/**Memoreaza stilul UNDERLINE pentru text grafic formatat.*/
	protected boolean underline = false;

	/**Memoreaza stilul STOKE pentru text grafic formatat.*/
	protected boolean stroke = false;

	/**Memoreaza stilul de linie pentru figuri.*/
	protected int lineType = Figure.SOLID_LINE;

	/**Memoreaza tipul de sageata la figurile corespunzatoare.*/
	protected int arrowType = Figure.NO_HEADED_ARROW;

	/**Memoreaza tipul de colturi la rectangle/freehand.*/
	protected int cornerType = Figure.NORMAL_CORNER;

	/**Dimensiunea cu care sunt create implicit obiectele (grosimea liniei)*/
	protected int size = 1;

	/**Dimensiunea cu care sunt create implicit textele formatate.*/
	protected int fontSize = 12;

	/**
	 * Aici sunt stocate toate figurile separate pe grupuri.
	 * Exista o corespondenta bijectva intre elementele vectorului shapes
	 * si cel al grupurilor.
	 */
	private Vector shapesVector = new Vector(20, 10);

	/**
	 * ToolBar care va contine sculele de desenat. <br>
	 * Pentru a avea acces la el se apeleaza getToolPane()
	 * @see Whiteboard#getToolbar()
	 */
	protected JToolBar toolsToolbar = new JToolBar();

	/**JLabel care afiseaza zoom-ul curent ca utilizatorul sa stie pe ce lume se afla...*/
	protected JLabel zoomViewer = new JLabel("100%");

	/**
	 * Culoarea curenta a background-ului.
	 */
	protected Color backgroundColor = Color.white;

	/**
	 * Culoarea curenta a foreground-ului (ie. cu care desenez).
	 */
	protected Color foregroundColor = Color.black;

	/**
	 * Culoarea curenta de umplere a figurilor.
	 */
	public Color fillColor = Color.cyan;

	/**
	 *  Coeficientul cu care maresc suprafata de desenare pentru afisare
	 *  pe coordonata X.
	 */
	protected float zoomX = 1.0f;

	/**
	 *  Coeficientul cu care maresc suprafata de desenare pentru afisare
	 *  pe coordonata Y.
	 */
	protected float zoomY = 1.0f;

	/**Starea interna initiala a whiteboard-ului.*/
	private int status = Whiteboard.MOVE;

	/**
	 * Setare vizualizare de inalta calitate. <br>
	 * @see Whiteboard#setHighQuality(boolean)
	 */
	protected boolean highQuality = !false;

	/**Fontul curent pentru text formatat.*/
	protected String fontFace = fontArray[0];

	/**
	 * Buton de activare a stilului BOLD pentru text formatat.
	 * @see Figure#BOLD
	 */
	public JToggleButton jtbBold = new JToggleButton("B");

	/**
	 * Buton de activare a stilului ITALIC pentru text formatat.
	 * @see Figure#ITALIC
	 */
	public JToggleButton jtbItalic = new JToggleButton("i");

	/**
	 * Buton de activare a stilului UNDERLINE pentru text formatat.
	 * @see Figure#UNDERLINE
	 */
	public JToggleButton jtbUnderline = new JToggleButton("U");

	/**
	 * Buton de setare a figurlor pline pentru rectangle si ellipse.
	 * @see Whiteboard#fillColor
	 */
	public JToggleButton jtbFillFigures = new JToggleButton("Filled");

	/**
	 * Buton de activare a stilului STRIKE-OUT pentru text formatat.
	 * @see Figure#STRIKE
	 */
	public JToggleButton jtbStroke = new JToggleButton("S");

	/**
	 * Variabila de tip BlinkCursor care imi va apela metoda blinkingCursor
	 * pentru a face cursorul sa palpaie in cazul in care introduc text grafic.
	 */
	public BlinkCursor blinkCursor = new BlinkCursor(this);

	/**Panel in care bag elemente de configurare.*/
	protected JToolBar configToolbar = new JToolBar ();

	/**Panel in care bag instrumentele de zoom.*/
	protected JToolBar zoomToolbar = new JToolBar ();

	/**Buton care va permite ca utilizatorul sa aleaga culoarea de desenare.*/
	public JButton jbForegroundColor = new JButton("fg");

	/**Buton care va permite ca utilizatorul sa aleaga culoarea de umplere a figurilor pline.*/
	public JButton jbFillColor = new JButton("fill");

	/**Buton care va permite ca utilizatorul sa aleaga culoarea de fundal.*/
	public JButton jbBackgroundColor = new JButton("bg");

	/**
	 *  Buton care insereaza o noua imagine pe wb la coordonatele (0,0).
	 *  La apasarea pe acest buton se deschide un dialog de deschidere
	 *  de fisier care permite alegerea fis. dorit. (jpeg)
	 */
	public JButton jbResetImageSize = new JButton("Reset image size");

	/**
	 * Buton care va permite ca utilizatorul sa stearga suprafata de
	 * desenare si sa reseteze starea wb la valorile implicite.
	 */
	protected JButton jbNew = new JButton(" New");

	/**
	 * Buton care va permite ca utilizatorul sa incarce un nou fisier pe
	 * whiteboard. Sunt permise numai fisiere .SVG si .JPG.
	 */
	protected JButton jbLoad = new JButton("Load");

	/**
	 * Buton care va permite ca utilizatorul sa salveze continutul wb intr-un
	 * fisier .SVG sau .JPG.
	 */
	protected JButton jbSave = new JButton("Save");

	/**
	 * Buton care va permite ca utilizatorul sa tipareasca continutul wb la
	 * imprimanta.
	 */
	protected JButton jbPrint = new JButton("Print");

	/**Combobox cu dimensiunile prestabilite pentru factorul de zoom.*/
	public JComboBox jcbZoomChooser = new JComboBox(zoomArray);

	/**
	 * Combobox care contine fonturile disponibile. Userul isi poate alege
	 * fontul pentru formatarea textului grafic.
	 */
	public JComboBox jcbFontChooser = new JComboBox(fontArray);

	/**
	 * Combobox care contine dimensiunle de  font disponibile. Userul isi
	 * poate alegedimensiunea fontului pentru formatarea textului grafic.
	 */
	public JComboBox jcbFontSizeChooser = new JComboBox(fontSizeArray);

	/**
	 * Combobox care contine tipurile de linie disponibile. Userul isi poate
	 * alege tipul obiectelor (exceptie face gtext).
	 */
	public JComboBox jcbLineStyleChooser = new JComboBox(lineStyleArray);

	/**
	 * Combobox care contine tipurile de sageti disponibile. Userul isi poate
	 * alege tipul obiectelor (exceptie face gtext, rectangle si ellipse).
	 */
	public JComboBox jcbArrowStyleChooser = new JComboBox(arrowStyleArray);

	/**
	 * Combobox care contine tipurile de colturi disponibile. Userul isi poate
	 * alege tipul obiectelor (exceptie face gtext, line si freehand).
	 */
	public JToggleButton jtbRoundedCorner = new JToggleButton("Rounded corner", false);

	/**
	 * Combobox care contine grosimile de linii disponibile. Userul isi poate
	 * alege grosimea liniei obiectelor (exceptie face gtext).
	 */
	public JComboBox jcbLineSizeChooser = new JComboBox(lineSizeArray);

	/**Slider-ul din care se poate seta scalarea suprafetei de afisare.*/
	protected JSlider jsZoom = new JSlider(-98, 91, 0);

	/**Buton de reset la 100% a whiteboard-ului.*/
	protected JButton jbResetZoom = new JButton("100%");

	/**RadioButton care activeaza starea de asteptare pentru desenat linii.*/
	public JToggleButton jtbLine = new JToggleButton("Line");

	/**RadioButton care activeaza starea de asteptare pentru desenat dreptunghiuri.*/
	public JToggleButton jtbRectangle = new JToggleButton("Rectangle");

	/**RadioButton care activeaza starea de asteptare pentru desenat elipse.*/
	public JToggleButton jtbEllipse = new JToggleButton("Ellipse");

	/**RadioButton care activeaza starea de asteptare pentru text formatat.*/
	public JToggleButton jtbGText = new JToggleButton("GText");

	/**RadioButton care activeaza starea de asteptare pentru desenat trasare de desene cu mana libera.*/
	public JToggleButton jtbFreehand = new JToggleButton("Free Hand");

	/**RadioButton care activeaza starea de asteptare pentru desenat trasare de arce de cerc.*/
	public JToggleButton jtbArc = new JToggleButton("Arc");

	/**RadioButton care activeaza starea de asteptare pentru inserat imagine.*/
	public JToggleButton jtbImage = new JToggleButton("Image");

	/**RadioButton care activeaza starea de asteptare pentru mutat o figura deja desenata.*/
	protected JToggleButton jtbEdit = new JToggleButton("Move item");

	/**RadioButton care activeaza starea de asteptare pentru sters ofigura deja desenata. */
	protected JToggleButton jtbKill = new JToggleButton("Erase Item");

	/**if selected the toolTollbar should ve visible*/
    protected JToggleButton jtbShowToolToolbar = new JToggleButton("Show Tool Toolbar", true);

	/**if selected the cofigurationTollbar should ve visible*/
    protected JToggleButton jtbShowConfigToolbar = new JToggleButton("Show Config Toolbar", true);

	/** retimen cursorul mouse-ului.*/
	protected Cursor mouseCursor = new Cursor(Cursor.DEFAULT_CURSOR);

	/**Salvez aici coordonatele punctelor prin care trec cu soarecele pentru a putea memora figura trasata cu mana libera.*/
	protected Vector freeHandCore = null;

	/**Salvez temporar figura care contine textul formatat.*/
	protected GText tempText = null;

	/**Figura pe care o editez...*/
	public Figure editingFigure = null;

	/**Salvez aici diferentele dintre figura pe care o salvez inainte si in timpul transformarilor....*/
	public Transformer difference_old = null;

	/**HashCode-ul pentru utilizatorul curent.*/
	protected int hc = 0;

	/**
	 * Folosit pentru comunicatii. Numai pentru trimitere de date...<br>
	 * pentru receptionare se apeleaza add(Figure)
	 */
	protected HermixApi sender = null;

	/**Calea pana la celelalte fisiere ce trebuie incarcate (imagini).*/
	String path = null;

	/**Meniul ce va aparea la click dreapta pe whiteboard-ul gol. (ie. nu pe o figura..)*/
	JPopupMenu jpmWhiteboard = new JPopupMenu();

	/**Item disabled (titlu) care afiseaza pe ce s-a dat click dreapta.*/
	protected JMenuItem jmiWhiteboard = new JMenuItem("Whiteboard");

	/**Item-ul din meniu care seteaza calitatea afisarii.*/
	protected JCheckBoxMenuItem jcbmiHighQuality = new JCheckBoxMenuItem("High Quality", highQuality);

	/**Item care permite ca sa trimit continutul wb. unui utilizator...*/
	protected JMenu jmiWBSendTo = new JMenu("Spread content to");

	/**Item care permite ca sa resetez wb-ul*/
	protected JMenuItem jmiWBNew = new JMenuItem("New");

	/**Item care permite ca sa incarc un fisier pe whiteboard..*/
	protected JMenuItem jmiWBLoad = new JMenuItem("Load");

	/**Item care permite ca sa salvez continutul Wb intr-un fisier.*/
	protected JMenuItem jmiWBSave = new JMenuItem("Save");

	/**Item care permite ca sa imprim continutul wb. la imprimanta*/
	protected JMenuItem jmiWBPrint = new JMenuItem("Print");

	/**Asculatatorul care preia toate mesajele de butoane si mouse pe wh curent..*/
	private WBListener listener = new WBListener(this);

	/**grupul pe care se transmit figurile**/
	private String group = "guest";

	/**tells if the whiteboard is editable from the currnt user!*/
	boolean editable = false;

	private Dimension wbSize = new Dimension(1023, 1023);

	protected String savedAs = null;

	private Vector whiteboardListeners = new Vector();

	/**Constructor*/
	public Whiteboard(int hc, String path) {
		//partea de initializare:
		this.hc = hc;
		this.path = path;
		this.init();
		this.setButtonsFace();
	}

	/**Metoda de initializare a tuturor componentelor si campurilor.*/
	private void init() {
		//aice trebuie construit si panelul
		toolsToolbar.setFloatable(true);
		toolsToolbar.setRollover(true);
		configToolbar.setFloatable(true);
		configToolbar.setRollover(true);
		Font aFont = new Font("Courier New", Font.BOLD, 12);
		jtbBold.setFont(aFont);
		jtbItalic.setFont(aFont);
		jtbUnderline.setFont(aFont);
		jtbStroke.setFont(aFont);

		//setam ascultatorii de soarece si tastatura
		this.addMouseMotionListener(listener);
		this.addMouseListener(listener);
		this.addKeyListener(listener);
		jtbBold.addActionListener(listener);
		jtbItalic.addActionListener(listener);
		jtbUnderline.addActionListener(listener);
		jtbStroke.addActionListener(listener);
		jbBackgroundColor.addActionListener(listener);
		jbForegroundColor.addActionListener(listener);
		jbFillColor.addActionListener(listener);
		jbLoad.addActionListener(listener);
		jbNew.addActionListener(listener);
		jbSave.addActionListener(listener);
		jbPrint.addActionListener(listener);
		jcbZoomChooser.addActionListener(listener);
		jcbFontChooser.addActionListener(listener);
		jcbFontSizeChooser.addActionListener(listener);
		jcbLineStyleChooser.addActionListener(listener);
		jcbArrowStyleChooser.addActionListener(listener);
		jtbRoundedCorner.addActionListener(listener);
		jcbLineSizeChooser.addActionListener(listener);
		jtbEllipse.addActionListener(listener);
		jtbFreehand.addActionListener(listener);
		jtbGText.addActionListener(listener);
		jtbFillFigures.addActionListener(listener);
		jtbKill.addActionListener(listener);
		jtbLine.addActionListener(listener);
		jtbArc.addActionListener(listener);
		jtbImage.addActionListener(listener);
		jtbEdit.addActionListener(listener);
		jtbRectangle.addActionListener(listener);
		jbResetImageSize.addActionListener(listener);
		jsZoom.addChangeListener(listener);
		jbResetZoom.addActionListener(listener);
		jtbShowToolToolbar.addActionListener(listener);
		jtbShowConfigToolbar.addActionListener(listener);
		//facem meniul contextual:
		jpmWhiteboard.add(jmiWhiteboard);
		jpmWhiteboard.addSeparator();
		jpmWhiteboard.add(jcbmiHighQuality);
		jpmWhiteboard.addSeparator();
		jpmWhiteboard.add(jmiWBNew);
		jpmWhiteboard.add(jmiWBLoad);
		jpmWhiteboard.add(jmiWBSave);
		jpmWhiteboard.add(jmiWBPrint);
		jpmWhiteboard.addSeparator();
		jpmWhiteboard.add(jmiWBSendTo);
		//punem si icoane la meniuri...
		try {
			jmiWBNew.setIcon(IntelliX.loadImageResource(this, "images/whiteboardnewfile.gif"));
			jmiWBSave.setIcon(IntelliX.loadImageResource(this, "images/whiteboardsavefile.gif"));
			jmiWBLoad.setIcon(IntelliX.loadImageResource(this, "images/whiteboardloadfile.gif"));
			jmiWBPrint.setIcon(IntelliX.loadImageResource(this, "images/whiteboardprintfile.gif"));
		} catch (Exception ex) {}

		//ascultatorii de meniuri.
		jmiWhiteboard.setEnabled(false);
		jmiWhiteboard.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 13));
		jcbmiHighQuality.addActionListener(listener);
		jmiWBNew.addActionListener(listener);
		jmiWBLoad.addActionListener(listener);
		jmiWBSave.addActionListener(listener);
		jmiWBPrint.addActionListener(listener);
		//jmiWBSendTo.addActionListener(listener);

		toolsToolbar.add(jtbEdit);
		toolsToolbar.add(jtbLine);
		toolsToolbar.add(jtbArc);
		toolsToolbar.add(jtbFreehand);
		toolsToolbar.add(jtbRectangle);
		toolsToolbar.add(jtbEllipse);
		toolsToolbar.add(jtbGText);
		toolsToolbar.add(jtbImage);
		toolsToolbar.add(jtbKill);

		ButtonGroup bg = new ButtonGroup();
		bg.add(jtbEdit);
		bg.add(jtbLine);
		bg.add(jtbArc);
		bg.add(jtbFreehand);
		bg.add(jtbRectangle);
		bg.add(jtbEllipse);
		bg.add(jtbGText);
		bg.add(jtbImage);
		bg.add(jtbKill);

		setExactDimension(jbResetImageSize, 28, 28);
		setExactDimension(jtbEdit, 28, 28);
		setExactDimension(jtbLine, 28, 28);
		setExactDimension(jtbArc, 28, 28);
		setExactDimension(jtbFreehand, 28, 28);
		setExactDimension(jtbRectangle, 28, 28);
		setExactDimension(jtbEllipse, 28, 28);
		setExactDimension(jtbGText, 28, 28);
		setExactDimension(jtbImage, 28, 28);
		setExactDimension(jtbKill, 28, 28);

		configToolbar.add(jbForegroundColor);
		jbForegroundColor.setForeground(foregroundColor);
		configToolbar.add(jbFillColor);
		jbFillColor.setForeground(fillColor);
		configToolbar.add(jbBackgroundColor);
		jbBackgroundColor.setForeground(backgroundColor);

		setExactDimension(jbForegroundColor, 28, 28);
		setExactDimension(jbFillColor, 28, 28);
		setExactDimension(jbBackgroundColor, 28, 28);

        zoomToolbar.add(zoomViewer);
		zoomViewer.setBorder(BorderFactory.createLoweredBevelBorder());
		zoomViewer.setHorizontalAlignment(JLabel.RIGHT);
		zoomToolbar.add(jsZoom);
		zoomToolbar.add(jbResetZoom);
		zoomToolbar.addSeparator();
		zoomToolbar.add(jtbShowToolToolbar);
		zoomToolbar.add(jtbShowConfigToolbar);
		setExactDimension(jsZoom, 180, 24);
		setExactDimension(zoomViewer, 45, 24);
		setExactDimension(jbResetZoom, 45, 24);
		setExactDimension(jtbShowToolToolbar, 24, 24);
		setExactDimension(jtbShowConfigToolbar, 24, 24);

		//raman deocamdata:
		//aPanel.add(jbNew);
		//aPanel.add(jbLoad);
		//aPanel.add(jbSave);
		//aPanel.add(jbPrint);

		configToolbar.add(jtbRoundedCorner);
		configToolbar.add(jtbFillFigures);
		configToolbar.add(jbResetImageSize);
		configToolbar.add(jtbBold);
		configToolbar.add(jtbItalic);
		configToolbar.add(jtbUnderline);
		configToolbar.add(jtbStroke);
		configToolbar.add(jcbFontChooser);
		configToolbar.add(jcbFontSizeChooser);
		configToolbar.add(jcbLineStyleChooser);
		configToolbar.add(jcbArrowStyleChooser);
		configToolbar.add(jcbLineSizeChooser);

		setExactDimension(jtbBold, 28, 28);
		setExactDimension(jtbItalic, 28, 28);
		setExactDimension(jtbUnderline, 28, 28);
		setExactDimension(jtbStroke, 28, 28);
		setExactDimension(jcbFontChooser, 100, 28);
		setExactDimension(jcbFontSizeChooser, 40, 28);
		setExactDimension(jcbLineStyleChooser, 60, 28);
		setExactDimension(jcbArrowStyleChooser, 60, 28);
		setExactDimension(jcbLineSizeChooser, 60, 28);
		setExactDimension(jtbRoundedCorner, 28, 28);
		setExactDimension(jtbFillFigures, 28, 28);
	}

	public void addWhiteboardListener(WhiteboardListener wl){
		if (whiteboardListeners.contains(wl)){
			whiteboardListeners.remove(wl);
		}
		whiteboardListeners.addElement(wl);
	}

	public void removeWhiteboardListener(WhiteboardListener wl){
		whiteboardListeners.removeElement(wl);
	}

	public Dimension getWbSize() {
		return wbSize;
	}

	public void setWbSize(Dimension wbSize) {
		this.wbSize = wbSize;
	}

	/**seteaza grupul curent ie. ala pe care se trimit figurile*/
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * Metoda care schimba adancimea unei figuri:
	 * @param f figura care va fi deplasata pe axa Z
	 * @param depth adancimea la care va fi figura:
	 * <br>-1 = to front
	 * <br> 0 = to back
	 */
	public void changeDepth(Figure f, int depth) {
		//lucra numai daca figura este in stiva de figuri...
		if (!shapesVector.contains(f))
			return;
		//scoatem dintai figura
		shapesVector.remove(f);
		//apoi o bagam inapoi, in functie de adancimea ceruta...
		if (depth == -1)//este in fata
			shapesVector.add(f);
		else if (depth == 0)//este in spate
			shapesVector.add(0, f);
		else //verificam daca se poate baga la adancuimea ceruta
			if (depth <= shapesVector.size())
				shapesVector.add(depth, f);
		repaint();
	}

	/**
	 * Metoda care afiseaza meniul contextual pe ecran. <br>
	 * Daca la coordonatele respective se gaseste o figura se va
	 * afisa meniul corespunzator, daca nu, se afiseaza meniul principal.
	 */
	public void showMenu(int x, int y) {
		if (!editable){
			return;
		}
		Figure f = getFigureAt((int) (x / zoomX), (int) (y / zoomY));
		if (f == null) {
			jpmWhiteboard.show(this, x, y);
		} else {
			if (status == EDIT && editingFigure != null && editingFigure != f) {
				Transformer t = editingFigure.getTransformer();
				if (editingFigure instanceof Arc)
					t.saveArc((Arc) (editingFigure));
				send(t);
				editingFigure = null;
				blinkCursor.stopBlink();
				setStatus(Whiteboard.IDLE);
			}
			f.showMenu(x, y, this, editingFigure);
		}
	}

	/**
	 * Seteaza imaginea de pe un anumita componenta de tip buton. <br>
	 * Daca nu reusestre sa incarce imaginea, pune pe buton textul implicit.
	 */
	private void putImageOn(AbstractButton c, ImageIcon img, String defaultText) {
		try {
			c.setMargin(new Insets(1, 1, 1, 1));
			c.setIcon(img);
			c.setToolTipText(defaultText);
			c.setText("");
		} catch (Exception ex) {
			jbNew.setText(defaultText);
		}
	}

	/**Seteaza icoanele pentru toate butoanele din panelul de scule.*/
	private void setButtonsFace() {
		putImageOn(jbNew, IntelliX.loadImageResource(this, "images/whiteboardnewfile.gif"), "New");
		putImageOn(jbLoad, IntelliX.loadImageResource(this, "images/whiteboardloadfile.gif"), "Open");
		putImageOn(jbSave, IntelliX.loadImageResource(this, "images/whiteboardsavefile.gif"), "Save");
		putImageOn(jbPrint, IntelliX.loadImageResource(this, "images/whiteboardprintfile.gif"), "Print");
		putImageOn(jtbLine, IntelliX.loadImageResource(this, "images/whiteboardline.gif"), "Line");
		putImageOn(jtbEllipse, IntelliX.loadImageResource(this, "images/whiteboardellipse.gif"), "Ellipse");
		putImageOn(jtbRectangle, IntelliX.loadImageResource(this, "images/whiteboardrectangle.gif"), "Rectangle");
		putImageOn(jtbFreehand, IntelliX.loadImageResource(this, "images/whiteboardfreehand.gif"), "Free hand");
		putImageOn(jtbArc, IntelliX.loadImageResource(this, "images/whiteboardcurve.gif"), "Arc");
		putImageOn(jtbImage, IntelliX.loadImageResource(this, "images/whiteboardimage.gif"), "Insert image");
		putImageOn(jtbGText, IntelliX.loadImageResource(this, "images/whiteboardgtext.gif"), "Abc");
		putImageOn(jtbBold, IntelliX.loadImageResource(this, "images/whiteboardbold.gif"), "B");
		putImageOn(jtbItalic, IntelliX.loadImageResource(this, "images/whiteboarditalic.gif"), "i");
		putImageOn(jtbUnderline, IntelliX.loadImageResource(this, "images/whiteboardunderline.gif"), "U");
		putImageOn(jtbStroke, IntelliX.loadImageResource(this, "images/whiteboardstroke.gif"), "s");
		putImageOn(jtbFillFigures, IntelliX.loadImageResource(this, "images/whiteboardfilled.gif"), "Filled");
		putImageOn(jtbKill, IntelliX.loadImageResource(this, "images/whiteboarddelete.gif"), "Delete item");
		putImageOn(jtbEdit, IntelliX.loadImageResource(this, "images/whiteboardmove.gif"), "Move item");
		putImageOn(jtbRoundedCorner, IntelliX.loadImageResource(this, "images/whiteboardroundedcorner.gif"), "Rounded corner");
		putImageOn(jbResetImageSize, IntelliX.loadImageResource(this, "images/whiteboardrestoreimagesize.gif"), "Restore image size");
		putImageOn(jtbShowConfigToolbar, IntelliX.loadImageResource(this, "images/whiteboardconfig.gif"), "Shows up the configuration panel");
		putImageOn(jtbShowToolToolbar, IntelliX.loadImageResource(this, "images/whiteboardtool.gif"), "Shows up the tool panel");
		jcbArrowStyleChooser.setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				setText(value.toString());
				this.setBackground(isSelected ? new Color(153, 153, 204) : Color.lightGray);
				try {
					if (value.toString().equals("Simple"))
						setIcon(IntelliX.loadImageResource(this, "images/whiteboardarrow0.gif"));
					else if (value.toString().equals("Start Arrow"))
						setIcon(IntelliX.loadImageResource(this, "images/whiteboardarrow1.gif"));
					else if (value.toString().equals("Both Arrows"))
						setIcon(IntelliX.loadImageResource(this, "images/whiteboardarrow2.gif"));
					else if (value.toString().equals("End Arrow"))
						setIcon(IntelliX.loadImageResource(this, "images/whiteboardarrow3.gif"));
					setText("");
				} catch (Exception ex) {
				}
				return this;
			}//getListCellRendererComponent
		});//setRenderer*/
		jcbLineStyleChooser.setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				setText(value.toString());
				this.setBackground(isSelected ? new Color(153, 153, 204) : Color.lightGray);
				try {
					if (value.toString().equals("Solid"))
						setIcon(IntelliX.loadImageResource(this, "images/whiteboardsolid.gif"));
					else if (value.toString().equals("Dashed"))
						setIcon(IntelliX.loadImageResource(this, "images/whiteboarddashed.gif"));
					else if (value.toString().equals("Dotted"))
						setIcon(IntelliX.loadImageResource(this, "images/whiteboarddotted.gif"));
					setText("");
				} catch (Exception ex) {
				}
				return this;
			}//getListCellRendererComponent
		});//setRenderer*/

	}

	public int getFigureCount(){
		return shapesVector.size();
	}

	public Figure getFigureAt(int idx){
		return (Figure)shapesVector.elementAt(idx);
	}

	public void setEditable(boolean editOn){
		this.editable = editOn;
		configToolbar.setVisible(editOn);
		toolsToolbar.setVisible(editOn);
		jtbShowConfigToolbar.setEnabled(editOn);
		jtbShowToolToolbar.setEnabled(editOn);
		if (!editOn){
			setStatus(Whiteboard.IDLE);
		}
	}

	/**Metoda de stergere a Whiteboard-ului.*/
	public void clear() {
		shapesVector.removeAllElements();
		setBackground(Color.white);
		repaint();
	}

	/**Metoda de resetare a Whiteboard-ului.*/
	public void reset() {
		foregroundColor = Color.black;
		backgroundColor = Color.white;
		fillColor = Color.cyan;
		jbForegroundColor.setForeground(Color.black);
		jbBackgroundColor.setForeground(Color.white);
		jbFillColor.setForeground(Color.cyan);
		bold = italic = underline = stroke = false;
		jtbBold.setSelected(bold);
		jtbItalic.setSelected(italic);
		jtbUnderline.setSelected(underline);
		jtbStroke.setSelected(stroke);
		jsZoom.setValue(1);
		((JViewport) getParent()).setViewPosition(new Point(0, 0));
		jtbLine.setSelected(true);
		jcbFontChooser.setSelectedIndex(0);
		jtbFillFigures.setSelected(false);
		jtbFillFigures.setEnabled(false);
		jcbLineSizeChooser.setSelectedIndex(0);
		jcbFontSizeChooser.setSelectedItem("12");
		jtbRoundedCorner.setSelected(false);
		jtbRoundedCorner.setEnabled(false);
		jcbArrowStyleChooser.setSelectedItem("Simple");
		jcbLineStyleChooser.setSelectedItem("Solid");
		jbFillColor.setEnabled(false);
		setStatus(Whiteboard.MOVE);
		//TO DO: call enableButtons
		repaint();
	}

	/**Metoda de desenare. <br> Se actualizeaza toate figurile pe ecran.*/
	public void paint(java.awt.Graphics g) {

		//convertim g la Graphics2D:
		Graphics2D g2 = (Graphics2D) g;

		//setam modul de desenare de inalta calitate (daca este cazul):
		if (highQuality) {
			g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
		}

		//desenam fundalul:
		g2.setPaint(backgroundColor);
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());

		//setam zoom-ul:
		g2.scale(zoomX, zoomY);

		for (int i = 0; i < shapesVector.size(); i++) {
			((Figure) shapesVector.elementAt(i)).draw(g2);
		}

		//daca suntem in curs de desenare a unei freeHand...
		if (status == SC.GRP_Free && freeHandCore != null) {
			//este trasat la calitate proasta deoarece este ceva temporar:
			g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_OFF);
			g2.setPaint(foregroundColor);
			g2.setStroke(new BasicStroke(1));
			for (int i = 0; i < freeHandCore.size() - 1; i++)
				try {//verificam daca este un vector de puncte
					int x1 = ((Point) (freeHandCore.elementAt(i))).x,
							y1 = ((Point) (freeHandCore.elementAt(i))).y,
							x2 = ((Point) (freeHandCore.elementAt(i + 1))).x,
							y2 = ((Point) (freeHandCore.elementAt(i + 1))).y;
					g.drawLine(x1, y1, x2, y2);
				} catch (Exception e) {
				}//daca nu este nu facem nika
		}

		//daca editam o figura o marcam pe ecran:
		if (status == EDIT && editingFigure != null)
			editingFigure.drawBorder(g2, (int) (8.0 / zoomX));
	}

	/** Metoda care face comunicarea spre exterior... <br> Lansam pe alt thread! */
	public void send(Figure f) {
		if (!editable){
			return;
		}
		if (f != null && sender != null){
			new Launcher(f, sender, group).start();
		}
		WhiteboardEvent we = new WhiteboardEvent(this, 0, f);
		//call listeners:
		for (int i = 0; i < whiteboardListeners.size(); i++) {
			((WhiteboardListener) whiteboardListeners.elementAt(i)).actionPerformed(we);
		}
	}

	/**Metoda care trimite **TOATE** figurile la ceilalti din grupul specificat.*/
	public void sendAllFigures() {
		new Launcher(shapesVector, sender, group, backgroundColor).start();
	}

	/**
	 * Metoda de adaugare a unei figuri la Whiteboard pe grupul curent.
	 * Aceasta metoda nu trebie sa fie vizibila decat din WBListener.
	 */
	public void add(Figure f) {
		if (f instanceof Clear) {
			this.clear();
			return;
		}
		if (f instanceof Background)
			this.setBackground(((Background) f).getForeground());
		else if (f instanceof Transformer) {
			Figure fig = getFigureId(f.getUID());
			if (fig == null)//nu exista figura asta asa ca nu fac nika.
				return;
			//else: o transform dupa kum spune f
			switch (f.getType()) {
				case SC.GRP_2Back_Figure:
					changeDepth(fig, 0);
					break;
				case SC.GRP_2Front_Figure:
					changeDepth(fig, -1);
					break;
				case SC.GRP_TMove_Figure:
					fig.translateTo(f.getPosition().x, f.getPosition().y);
					break;
				case SC.GRP_Transform_All:
					((Transformer) f).updateFigure(fig);
			}
		} else {
			Figure fig = getFigure(f.getUID());
			int i = -1;//in foreground
			//daca mai exista o data figura inseamna ca asta este un update...
			if (fig != null) {
				i = shapesVector.indexOf(fig);
				remove(fig);//scoatem aia veche
				//bagam asta noua la indicele aluia vechi
				shapesVector.add(i, f);
			} else {
				//bagam asta noua in foreground
				shapesVector.add(f);
			}
		}
		repaint();
	}

	/**Metoda de stergere a unei figuri de pe Whiteboard dintr-un anumit plan de figuri.*/
	public void remove(Figure f) {
		if (f == null)
			return;
		if (shapesVector.contains(f)) {
			if (f == editingFigure) {
				editingFigure = null;
				blinkCursor.stopBlink();
				if (getStatus() == EDIT)
					setStatus(IDLE);
			}
			//daca nu il sterge macar sa nu se vada...
			f.setForeground(backgroundColor);
			shapesVector.remove(f);
			repaint();
		}
	}

	/**Metoda de stergere a unei figuri de pe Whiteboard dupa UID din planul defiguri specificat.*/
	public void remove(long UID) {
		this.remove(this.getFigure(UID));
	}

	/**
	 * Metoda care intoarce prima Figura din vectorul shapes care contine
	 * coordonatele (x, y). <br>
	 * In caz ca nu exista nici o figura pe coordonatele (x, y) intoarce null.
	 */
	public Figure getFigureAt(int x, int y) {
		for (int i = shapesVector.size() - 1; i >= 0; i--)
			if ((((Figure) shapesVector.elementAt(i)).contains(x, y) & Figure.FIGURE_HIT) != 0)
				return (Figure) shapesVector.elementAt(i);
		//daca nu am gasit nici o figura returnam null
		return null;
	}

	/**
	 * Metoda care intoarce prima Figura din vectorul shapes care are UID-ul
	 * specificat. <br>
	 * In caz ca nu exista o astfel de figura intoarce null.
	 */
	public Figure getFigure(long UID) {
		for (int i = shapesVector.size() - 1; i >= 0; i--)
			if (((Figure) shapesVector.elementAt(i)).getUID() == UID)
				return (Figure) shapesVector.elementAt(i);
		//daca nu am gasit nici o figura returnam null
		return null;
	}

	/**
	 * Metoda care face convertirea la un sir de caractere in format SVG. <br>
	 * Este utilizata pentru salvare in fisier. Pentru afisare pe ecran se
	 * foloseste cu parametru writer = System.out.
	 */
	public void saveFigures(PrintWriter writer, String filename) {
		//punem intai fundalul:
		writer.println("<!-- background --> \n<rect x=\"0\" y=\"0\" " + "width=\"101%\" height=\"101%\" style=\" fill:#" + Integer.toHexString(backgroundColor.getRGB()).substring(2, 8) + "; stroke:#" + Integer.toHexString(backgroundColor.getRGB()).substring(2, 8) + "\" />\n");
		for (int i = 0; i < shapesVector.size(); i++)
			if (shapesVector.elementAt(i) instanceof ro.intellisoft.whiteboard.shapes.Image)
				writer.println(((ro.intellisoft.whiteboard.shapes.Image) (shapesVector.elementAt(i))).toSVG("", filename));
			else
				writer.println(((Figure) (shapesVector.elementAt(i))).toSVG());
	}

	/**
	 * This method builds a String that contains the actual configuration of
	 * this whiteboard. The format of the String is SVG, but excluding the
	 * xml tags. ie onlu the root within <svg..>..</svg>
	 * @param prefix is a prefix for each line. Usually shoul be 0 or more tabs
	 */
	public String getXMLRepresentation(String prefix) {
		StringBuffer sb = new StringBuffer("\n"+prefix+"<svg width=\"1023\" height=\"1023\">\n");
		String oldprefix = prefix;
		prefix += "\t";
		sb.append(prefix+"<desc>\n" +
				prefix+"   Dump of Whiteboard 3.1b \n" +
				prefix+"   Date: " + (new java.util.Date()).toString() + " \n" +
				prefix+"</desc>\n" +
				prefix+"<defs>\n" +
				prefix+"<marker id=\"EndArrow\" viewBox=\"0 0 10 10\" refX=\"0\" refY=\"5\" markerUnits=\"strokeWidth\" markerWidth=\"4\" markerHeight=\"3\"  orient=\"auto\">" +
					" <path d=\"M 0 0 L 10 5 L 0 10 z\" />  </marker>\n" +
				prefix+" <marker id=\"StartArrow\" viewBox=\"-10 -10 10 10\" refX=\"0\" refY=\"-5\" markerUnits=\"strokeWidth\" markerWidth=\"4\" markerHeight=\"3\"  orient=\"auto\">" +
					" <path d=\"M 0 0 L -10 -5 L 0 -10 z\" />  </marker>\n" +
				prefix+"</defs>\n\n");
		sb.append(prefix+"<!-- background -->\n"+prefix+"<rect x=\"0\" y=\"0\" " + "width=\"101%\" height=\"101%\" style=\" fill:#" + Integer.toHexString(backgroundColor.getRGB()).substring(2, 8) + "; stroke:#" + Integer.toHexString(backgroundColor.getRGB()).substring(2, 8) + "\" />\n");
		for (int i = 0; i < shapesVector.size(); i++){
			if (!(shapesVector.elementAt(i) instanceof ro.intellisoft.whiteboard.shapes.Image)){
				//ro.intellisoft.whiteboard.shapes.Image will not be saved this way!
				sb.append(((Figure) (shapesVector.elementAt(i))).toSVG(prefix));
			}
		}
		sb.append("\n"+oldprefix+"</svg>\n");
		return sb.toString();
	}

	/**
	 * Metoda de setare a hc-ului utilizatorului.
	 */
	public void setUserHC(int hc) {
		this.hc = hc;
	}

	/**
	 * Metoda care intoarce Figura din vectorul shapes care are
	 * UID-ul specificat. <br>
	 * In caz ca nu exista nici o figura pe coordonatele (x, y) intoarce null.
	 * @see Figure#UID
	 */
	public Figure getFigureId(long UID) {
		for (int i = shapesVector.size() - 1; i >= 0; i--)
			if (((Figure) shapesVector.elementAt(i)).getUID() == UID)
				return (Figure) shapesVector.elementAt(i);
		//daca nu am gasit nici o figura returnam null
		return null;
	}

	/**
	 * Metoda apelata din cand in cand pentru a palpai cun cursor.
	 * Pentru ca nu cumva sa imi dispara textulm (tempText) metoda este sincronizata!
	 */
	public synchronized void alternateCursor() {
		if (tempText == null)
			return;
		Graphics2D g = (Graphics2D) getGraphics();
		g.setXORMode(backgroundColor);
		g.setPaint(Color.black);
		//g.setStroke(new BasicStroke(1));
		g.scale(zoomX, zoomY);
		int x = (int) (tempText.getPosition().getX() + tempText.getTextLength()),
				y = (int) tempText.getPosition().getY(),
				h = tempText.getTextHeight();
		if (h < 2)
			h = 2;
		g.drawLine(x, y, x, y + h);
	}

	/**Returneaza toolbarul cu instrumente de desenat.*/
	public JToolBar getToolbarPane() {
		return toolsToolbar;
	}

	/**Returneaza toolbarul cu configurarile instrumentelor de desenat.*/
	public JToolBar getToolbarConfigPane() {
		return configToolbar;
	}

	/**Returneaza toolbarul cu configurarile instrumentelor de desenat.*/
	public JToolBar getToolbarZoomPane() {
		return zoomToolbar;
	}

	/**Seteaza culoarea de fundal */
	public void setBackground(Color c) {
		if (c == null)
			c = Color.white;
		this.backgroundColor = c;
		this.repaint();
	}

	/**Setarea canalului de transmisie...*/
	public void setSender(HermixApi hermixApi) {
		this.sender = hermixApi;
	}

	/**Setarea calitatii imaginii.*/
	public void setHighQuality(boolean highQuality) {
		this.highQuality = highQuality;
		repaint();
	}

	/**Obtinerea calitatii randarii pe whiteboard.*/
	public boolean isHighQuality() {
		return highQuality;
	}

	/**
	 * Seteaza starea interna a wb-ului.
	 * @see Whiteboard#status
	 */
	public void setStatus(int status) {
		if (this.status == BLOCK_STATUS)
			return;//nu dau voie sa se modifice din afara!
		if (status >= 0 && status < 150) {//numai starile acceptabile
			if (status == EDIT) {
				editingFigure.updateToolConfig(listener, this);
			} else
				jbResetImageSize.setEnabled(false);
			this.status = status;

			if (status == SHIFT || status == MOVE || status == EDIT)
				mouseCursor = new Cursor(Cursor.HAND_CURSOR);
			else if (status == KILL)
				mouseCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
			else if (status == ZOOM)
				mouseCursor = new Cursor(Cursor.NW_RESIZE_CURSOR);
			else if (status == SC.GRP_GText)
				mouseCursor = new Cursor(Cursor.TEXT_CURSOR);
			else
				mouseCursor = new Cursor(Cursor.DEFAULT_CURSOR);

			setCursor(mouseCursor);
		}

		//TODO:now update button's state so as it will reflect the current state
		jbForegroundColor.setEnabled(!jtbEdit.isSelected() && !jtbImage.isSelected() && !jtbKill.isSelected());

		jtbUnderline.setVisible(jtbGText.isSelected());
		jtbBold.setVisible(jtbGText.isSelected());
		jtbItalic.setVisible(jtbGText.isSelected());
		jtbStroke.setVisible(jtbGText.isSelected());
        jcbFontChooser.setVisible(jtbGText.isSelected());
		jcbFontSizeChooser.setVisible(jtbGText.isSelected());

		jcbLineSizeChooser.setVisible(jtbArc.isSelected() || jtbLine.isSelected() || jtbFreehand.isSelected() ||
				jtbEllipse.isSelected() || jtbFreehand.isSelected() || jtbRectangle.isSelected());
		jcbLineStyleChooser.setVisible(jtbArc.isSelected() || jtbLine.isSelected() || jtbFreehand.isSelected() ||
				jtbEllipse.isSelected() || jtbFreehand.isSelected() || jtbRectangle.isSelected());
		jcbArrowStyleChooser.setVisible(jtbArc.isSelected() || jtbLine.isSelected() || jtbFreehand.isSelected());
		jbFillColor.setVisible(jtbEllipse.isSelected() || jtbRectangle.isSelected());
		jtbRoundedCorner.setVisible(jtbFreehand.isSelected() || jtbRectangle.isSelected());
		jtbFillFigures.setVisible(jtbEllipse.isSelected() || jtbRectangle.isSelected());
		jbFillColor.setEnabled(jtbFillFigures.isSelected());

		jbResetImageSize.setVisible(jtbImage.isSelected());
		jbResetImageSize.setEnabled(status == Whiteboard.EDIT);

	}

	/**
	 * Interogheaza starea interna a wb-ului.
	 * @see Whiteboard#status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Seteaza imaginea pentru figura cu UIDul respectiv:
	 */
	public void setImage(long UID, java.awt.Image img) {
		Figure f = getFigure(UID);
		if (f == null) {
			//probabil figura inca nu a ajuns
			(new ro.intellisoft.whiteboard.shapes.Image(0, 0, img.getWidth(this), img.getHeight(this), UID)).setImage(img);
			repaint();
			return;
		}
		if (!(f instanceof ro.intellisoft.whiteboard.shapes.Image))
			return;
		System.out.println("//");
		//daca am ajuns aici atunci f este o instanta a unei fuguri
		//careia ii vom updatat imaginea..
		((ro.intellisoft.whiteboard.shapes.Image) f).setImage(img);
		repaint();
	}

	////////////////////////////////////////////////////////////////////////
	// Urmeaza metodele de lucru cu grupuri:


	/**adauga o figura care a fost codifucata SVG*/
	public void add(String SVGFigure){
		System.out.println(SVGFigure);
	}

	/**Mesaj primt pe teava!*/
	public void add(String nick, int type, Rect rect, Color color, int thickness, String text, Object data) {
		Color fill = null;
		try {
			fill = Color.decode(text);
		} catch (Exception ex) {
			try {
				fill = (Color) ((Vector) data).elementAt(2);
			} catch (Exception exc) {
				fill = null;
			}
		}
		int xc = rect.x1;
		int yc = rect.y1;
		try {
			xc = ((Point) ((Vector) data).elementAt(2)).x;
			yc = ((Point) ((Vector) data).elementAt(2)).y;
		} catch (Exception ex) {
			try {
				xc = ((Point) ((Vector) data).elementAt(3)).x;
				yc = ((Point) ((Vector) data).elementAt(3)).y;
			} catch (Exception exc) {}
		}
		String fontName = "Arial";
		try {
			fontName = (String) ((Vector) data).elementAt(2);
		} catch (Exception ex) {
		}
		int caract = 0x100000;
		long UID = (long) (2 * Math.random() * Long.MAX_VALUE - Long.MAX_VALUE);
		/*acuma are: daca trimit de pe un grup pe altul!!
        if (nick.equals(hermix.getNick()))//este mesajul meu, nu are rost sa 'dublez' figura
            return;
        */
		if (type != SC.GRP_Free) {
			//incercam sa 'prindem' elementele carcteristice si UID pt. figura.
			try {
				caract = ((Integer) ((Vector) data).elementAt(1)).intValue();
			} catch (Exception ex) {
			}

			try {
				UID = ((Long) ((Vector) data).elementAt(0)).longValue();
			} catch (Exception ex) {
			}


			switch (type) {
				case SC.GRP_Line://line
					this.add(new Line(rect.x1, rect.y1, rect.x2, rect.y2, thickness, caract, color, UID));
					break;
				case SC.GRP_Rectangle://rectangle
					this.add(new ro.intellisoft.whiteboard.shapes.Rectangle(rect.x1, rect.y1, rect.x2, rect.y2, thickness, caract, color, fill, UID));
					break;
				case SC.GRP_Image://image
					this.add(new ro.intellisoft.whiteboard.shapes.Image(rect.x1, rect.y1, rect.x2, rect.y2, UID));
					break;
				case SC.GRP_Circle://ellipse
					this.add(new Ellipse(rect.x1, rect.y1, rect.x2, rect.y2, thickness, caract, color, fill, UID));
					break;
				case SC.GRP_Arc://arc de cerc
					this.add(new Arc(rect.x1, rect.y1, xc, yc, rect.x2, rect.y2, thickness, caract, color, UID));
					break;
				case SC.GRP_GText://string
					if (caract == 0x100000) {//pentru compatibilitate
						caract = 0;
						if ((thickness & 0x1) != 0)
							caract |= Figure.BOLD;
						if ((thickness & 0x2) != 0)
							caract |= Figure.ITALIC;
						if ((thickness & 0x4) != 0)
							caract |= Figure.UNDERLINE;
						if ((thickness & 0x8) != 0)
							caract |= Figure.STRIKE;
						thickness = (thickness / 16 + 2) * 3;
					}
					this.add(new GText(rect.x1, rect.y1, thickness, caract, color, text, fontName, UID));
					break;
				case SC.GRP_Clear://sterge ecranul
					this.clear();
					break;
				case 152:
					this.add(new FreeHand((Vector) data, thickness, Figure.ROUNDED_CORNER, color, UID));
					break;
				case SC.GRP_Background:
					this.setBackground(color);
					break;
				case SC.GRP_Delete_Figure:
					this.remove(UID);
					break;
				case SC.GRP_Transform_All:
				case SC.GRP_TMove_Figure:
				case SC.GRP_2Front_Figure:
				case SC.GRP_2Back_Figure:
					this.add(new Transformer(type, UID, rect, color, thickness, text, fill, caract, fontName, xc, yc));
					break;
			}///end Switch
		}//if type!=freehand
		else {
			Vector v = (Vector) data;
			try {
				if (v.elementAt(v.size() - 1) instanceof Integer)
					caract = ((Integer) (v.elementAt(v.size() - 1))).intValue();
				v.removeElementAt(v.size() - 1);
			} catch (Exception ex) {
			}
			try {
				if (v.elementAt(v.size() - 1) instanceof Long)
					UID = ((Long) (v.elementAt(v.size() - 1))).longValue();
				v.removeElementAt(v.size() - 1);
			} catch (Exception ex) {
			}
			//daca erau inca doua campuri in finalul vectorului le-am redus..
			this.add(new FreeHand(v, thickness, caract, color, UID));
		}//type== SC.GRP_Free
	}

	/**Pentru ca o figura sa stie de unde sa ia o imagine:*/
	public String getPath() {
		return this.path;
	}

	/**returns a string that represents the file wheew the constent has been saved or null*/
	public String getSavedAs() {
		return savedAs;
	}

	public String getGroup() {
		return group;
	}

	/** seteaza pe ntru jcomponetata specificata dimendiunile fixe specificate*/
	public static void setExactDimension(JComponent jComponent, int width, int height){
		Dimension d = new Dimension(width, height);
		jComponent.setMinimumSize(d);
		jComponent.setPreferredSize(d);
		jComponent.setMaximumSize(d);
	}

	////////////////////////////////////////////////////////////////////////
	// Urmeaza metodele de implementare a interfetelor:

	/**Metoda interfetei Printable.*/
	public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {
		if (pi >= 1) {
			return Printable.NO_SUCH_PAGE;
		}
		float saveZoomX = this.zoomX;
		float saveZoomY = this.zoomY;
		this.zoomX = this.zoomY = (float) (Math.min(pf.getImageableHeight(), pf.getImageableWidth()) / (double)wbSize.getHeight());
		g.translate((int) pf.getImageableX(), (int) pf.getImageableY());
		paint(g);
		this.zoomX = saveZoomX;
		this.zoomY = saveZoomY;
		return Printable.PAGE_EXISTS;
	}//end print

	/**
	 * Returns the preferred size of the viewport for a view component.
	 * @see Scrollable#getPreferredScrollableViewportSize()
	 */
	public Dimension getPreferredScrollableViewportSize() {
		return wbSize;
	}

	/**
	 * Components that display logical rows or columns should compute the scroll
	 * increment that will completely expose one block of rows or columns,
	 * depending on the value of orientation.
	 * @see Scrollable#getScrollableBlockIncrement(java.awt.Rectangle, int, int)
	 */
	public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
		if (orientation == SwingConstants.HORIZONTAL)
			return 50;
		else
			return 50;
	}

	/**
	 * Return true if a viewport should always force the height of this Scrollable
	 * to match the height of the viewport.
	 * @see Scrollable#getScrollableTracksViewportHeight()
	 */
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	/**
	 * Return true if a viewport should always force the width of this Scrollable
	 * to match the width of the viewport.
	 * @see Scrollable#getScrollableTracksViewportWidth()
	 */
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	/**
	 * Components that display logical rows or columns should compute the scroll
	 * increment that will completely expose one new row or column, depending on
	 * the value of orientation.
	 * @see Scrollable#getScrollableUnitIncrement(java.awt.Rectangle, int, int)
	 */
	public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
		return 5;
	}

	/**for testing purpose only:*/
	public static void main(String args[]){
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Whiteboard wb = new Whiteboard(100, ".");
		JScrollPane pane = new JScrollPane(wb);
		wb.setPreferredSize(new Dimension(1024, 1024));
		wb.setSize(new Dimension(1024, 1024));
		wb.setEditable(!false);
		wb.setStatus(Whiteboard.IDLE);
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(pane, BorderLayout.CENTER);
		wb.getToolbarPane().setOrientation(JToolBar.VERTICAL);
		f.getContentPane().add(wb.getToolbarPane(), BorderLayout.WEST);
		f.getContentPane().add(wb.getToolbarConfigPane(), BorderLayout.NORTH);
		f.getContentPane().add(wb.getToolbarZoomPane(), BorderLayout.SOUTH);
		//f.getContentPane().add(new JTextArea("rwle rliye r9wqey orqiw"), BorderLayout.EAST);
		JToolBar jt = new JToolBar(){
			public void paint(Graphics g){
				g.setColor(Color.red);
				g.drawLine(0, 0, getWidth(), getHeight());
			}
		};
		f.getContentPane().add(jt, BorderLayout.EAST);
		jt.setVisible(true);
		f.setSize(400, 300);
		f.show();
	}

}//Whiteboard