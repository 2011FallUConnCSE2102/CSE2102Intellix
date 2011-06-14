
package ro.intellisoft.whiteboard;

/**
 * Title:        Clasa care stie sa incarce continutul wb-ului pe un alt thread.
 *
 * Copyright:    Copyright (c) 2001
 * Company:      Intellisoft
 * @author Ovidiu Maxiniuc
 * @version 2.0
 */

import ro.intellisoft.XML.XMLAttributes;
import ro.intellisoft.XML.XMLHandler;
import ro.intellisoft.XML.XMLParseException;
import ro.intellisoft.whiteboard.shapes.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;

public class SVGHandler extends XMLHandler {

	/**Retin Whiteboard-ul ca sa desenez*/
	private Whiteboard wb = null;

	/**Iau niste valori implicite pentru dimensiunile whiteboardului.*/
	private int height = 1023,	width = 1023;

	/**Retin numarul de imbricari pentru definitii SVG.*/
	private int defs = 0;

	/**
	 * Flag care indica faptul ca astept sa citesc continutul unui string
	 * care ma intereseaza (GRP_GText)
	 */
	private boolean wait4text = false;

	/**Retin cam pe unde am ajuns ca sa pot afisa in wb.statusBar*/
	private int progressLevel = 10;

	/**GText temporar, pentru a putea manipula datele (din cauza cupluluide tag-uri).*/
	private GText tempText = null;

	/**Numele complet al directorului ca sa pot pune imaginea...*/
	String absolutePath = null;

	/**Grupul de Whiteboard pe care se face incarcarea:*/
	private String activeGroup;

	/**
	 * Constructor
	 * @param wb  = whiteboard-ul caruia ii trimit informatiile din fis
	 */
	public SVGHandler(Whiteboard wb, String filePath, String aGroup) {
		this.absolutePath = filePath;
		this.wb = wb;
		this.activeGroup = aGroup;
	}

	// metode de prelucrare a fisierului SCV:

	public void startDocument() {
		wb.clear();
		wb.send(new Clear());
	}

	public void error(XMLParseException e) {
		e.printStackTrace();
	}

	public void startElement(String name, XMLAttributes attr) {
		this.progressLevel++;
		//TODO: update the percentage un image on wb

		if (name.equals("defs"))
			defs++;
		if (defs > 0)
			return;

		if (name.equals("svg")) {/*
            if ((height=getIntAttr(attr,"height"))==0)
                height=1023;
            if ((width=getIntAttr(attr,"width"))==0)
                width=1023;*/
			///???? and what?
		} else if (name.equals("path")) {
			Vector unVector = new Vector(10, 100);
			StringTokenizer attrParser = new StringTokenizer(attr.getValue("d"), " ,aAcClLhHqQsStTvVzZmM");
			try {
				while (attrParser.hasMoreTokens()) {
					unVector.add(new Point((int) Float.parseFloat(attrParser.nextToken()), (int) Float.parseFloat(attrParser.nextToken())));
				}
				//daca apare Z(z) atunci e o curba inchisa:
				if ((attr.getValue("d").lastIndexOf('z') != -1) || (attr.getValue("d").lastIndexOf('Z') != -1))
					unVector.add(unVector.firstElement());
				int t = getStyleIntAttr(getStrAttr(attr, "style"), "stroke-width");
				t = t >= 0?t:1;
				int dash = getStyleIntAttr(getStrAttr(attr, "style"), "stroke-dasharray");
				dash = (dash <= 0 ? Figure.SOLID_LINE:
						(dash <= t ? Figure.DOTTED_LINE:
						Figure.DASHED_LINE));
				String arrow1 = getStyleStrAttr(getStrAttr(attr, "style"), "marker-start");
				String arrow2 = getStyleStrAttr(getStrAttr(attr, "style"), "marker-end");
				int arrow = Figure.NO_HEADED_ARROW;
				if (arrow1 != "")//obligatoriu este cu 2 capete
					arrow = Figure.TWO_HEADED_ARROW;
				else if (arrow2 != "")//obligatoriu este cu 1 capat
					arrow = Figure.END_HEAD_ARROW;
				if ((attr.getValue("d").lastIndexOf('c') != -1) || (attr.getValue("d").lastIndexOf('C') != -1) || (attr.getValue("d").lastIndexOf('S') != -1) || (attr.getValue("d").lastIndexOf('s') != -1) || (attr.getValue("d").lastIndexOf('A') != -1) || (attr.getValue("d").lastIndexOf('a') != -1) || (attr.getValue("d").lastIndexOf('q') != -1) || (attr.getValue("d").lastIndexOf('Q') != -1) || (attr.getValue("d").lastIndexOf('t') != -1) || (attr.getValue("d").lastIndexOf('T') != -1))
					dash |= Figure.ROUNDED_CORNER;
				Color c = decodeColor(getStyleStrAttr(getStrAttr(attr, "style"), "stroke"));
				FreeHand fh = new FreeHand(unVector, t, dash | arrow, c, wb.hc);
				wb.add(fh);
				wb.send(fh);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (name.equals("ellipse")) {
			int cx = getIntAttr(attr, "cx"),
					cy = getIntAttr(attr, "cy"),
					rx = getIntAttr(attr, "rx"),
					ry = getIntAttr(attr, "ry");
			int t = getStyleIntAttr(getStrAttr(attr, "style"), "stroke-width");
			Color c = decodeColor(getStyleStrAttr(getStrAttr(attr, "style"), "stroke"));
			String fillColor = getStyleStrAttr(getStrAttr(attr, "style"), "fill");
			t = t >= 0?t:1;
			int dash = getStyleIntAttr(getStrAttr(attr, "style"), "stroke-dasharray");
			dash = (dash <= 0 ? Figure.SOLID_LINE:
					(dash <= t ? Figure.DOTTED_LINE:
					Figure.DASHED_LINE));
			Ellipse el = new Ellipse(cx - rx, cy - ry, cx + rx, cy + ry, t, dash, c, (fillColor.equals("") || fillColor.equals("none"))
																					 ?null:decodeColor(fillColor), wb.hc);
			wb.add(el);
			wb.send(el);
		} else if (name.equals("polyline")) {
			Vector unVector = new Vector(10, 100);
			StringTokenizer attrParser = new StringTokenizer(attr.getValue("points"), " ,");
			try {
				while (attrParser.hasMoreTokens()) {
					unVector.add(new Point((int) Float.parseFloat(attrParser.nextToken()), (int) Float.parseFloat(attrParser.nextToken())));
				}
				int t = getStyleIntAttr(getStrAttr(attr, "style"), "stroke-width");
				t = t >= 0?t:1;
				int dash = getStyleIntAttr(getStrAttr(attr, "style"), "stroke-dasharray");
				dash = (dash <= 0 ? Figure.SOLID_LINE:
						(dash <= t ? Figure.DOTTED_LINE:
						Figure.DASHED_LINE));
				String arrow1 = getStyleStrAttr(getStrAttr(attr, "style"), "marker-start");
				String arrow2 = getStyleStrAttr(getStrAttr(attr, "style"), "marker-end");
				int arrow = Figure.NO_HEADED_ARROW;
				if (arrow1 != "")//obligatoriu este cu 2 capete
					arrow = Figure.TWO_HEADED_ARROW;
				else if (arrow2 != "")//obligatoriu este cu 1 capat
					arrow = Figure.END_HEAD_ARROW;
				Color c = decodeColor(getStyleStrAttr(getStrAttr(attr, "style"), "stroke"));
				FreeHand fh = new FreeHand(unVector, t, dash | arrow, c, wb.hc);
				wb.add(fh);
				wb.send(fh);
			} catch (Exception e) {
				System.err.println(e);
			}
		} else if (name.equals("line")) {
			int x1 = getIntAttr(attr, "x1"),
					y1 = getIntAttr(attr, "y1"),
					x2 = getIntAttr(attr, "x2"),
					y2 = getIntAttr(attr, "y2");
			int t = getStyleIntAttr(getStrAttr(attr, "style"), "stroke-width");
			t = t >= 0?t:1;
			int dash = getStyleIntAttr(getStrAttr(attr, "style"), "stroke-dasharray");
			dash = (dash <= 0 ? Figure.SOLID_LINE:
					(dash <= t ? Figure.DOTTED_LINE:
					Figure.DASHED_LINE));
			String arrow1 = getStyleStrAttr(getStrAttr(attr, "style"), "marker-start");
			String arrow2 = getStyleStrAttr(getStrAttr(attr, "style"), "marker-end");
			int arrow = Figure.NO_HEADED_ARROW;
			if (arrow1 != "")//obligatoriu este cu 2 capete
				arrow = Figure.TWO_HEADED_ARROW;
			else if (arrow2 != "")//obligatoriu este cu 1 capat
				arrow = Figure.END_HEAD_ARROW;
			Color c = decodeColor(getStyleStrAttr(getStrAttr(attr, "style"), "stroke"));
			Line l = new Line(x1, y1, x2, y2, t, dash | arrow, c, wb.hc);
			wb.add(l);
			wb.send(l);
		} else if (name.equals("rect")) {
			int x = getIntAttr(attr, "x"),
					y = getIntAttr(attr, "y"),
					width = getIntAttr(attr, "width"),
					height = getIntAttr(attr, "height");
			int t = getStyleIntAttr(getStrAttr(attr, "style"), "stroke-width");
			t = t >= 0?t:1;
			int dash = getStyleIntAttr(getStrAttr(attr, "style"), "stroke-dasharray");
			dash = (dash <= 0 ? Figure.SOLID_LINE:
					(dash <= t ? Figure.DOTTED_LINE:
					Figure.DASHED_LINE));
			if (getIntAttr(attr, "rx") >= 1)
				dash |= Figure.ROUNDED_CORNER;
			Color c = decodeColor(getStyleStrAttr(getStrAttr(attr, "style"), "stroke"));
			String fillColor = getStyleStrAttr(getStrAttr(attr, "style"), "fill");
			if (x == 0 && y == 0 && width >= 1023 && height >= 1023 && !((fillColor.equals("") || fillColor.equals("none")))) {
				//setam background-ul la noi :
				wb.setBackground(decodeColor(fillColor));
				wb.send(new Background(decodeColor(fillColor)));
			} else {
				ro.intellisoft.whiteboard.shapes.Rectangle r = new ro.intellisoft.whiteboard.shapes.Rectangle(x, y, x + width, y + height, t, dash, c, (fillColor.equals("") || fillColor.equals("none"))
																																					   ?null:decodeColor(fillColor), wb.hc);
				wb.add(r);
				wb.send(r);
			}
		} else if (name.equals("image")) {
			int x = getIntAttr(attr, "x"),
					y = getIntAttr(attr, "y"),
					width = getIntAttr(attr, "width"),
					height = getIntAttr(attr, "height");
			String xx = getStrAttr(attr, "xlink:href");
			if (!new File(xx).isAbsolute())
				xx = absolutePath + "//" + xx;
			ro.intellisoft.whiteboard.shapes.Image i = new ro.intellisoft.whiteboard.shapes.Image(x, y, x + width, y + height, wb.hc * 0x100000000L + new File(xx).getName().hashCode());
			wb.add(i);
			wb.send(i);
			wb.setImage(wb.hc * 0x100000000L + new File(xx).getName().hashCode(), new ImageIcon(xx).getImage());
			wb.sender.longFileNames.add(xx);
			wb.sender.s_send_file(new File(xx).length() + "", "0", activeGroup, new File(xx).getName());
		} else if (name.equals("text")) {
			int textSize = getStyleIntAttr(getStrAttr(attr, "style"), "font-size");
			String font = getStyleStrAttr(getStrAttr(attr, "style"), "font-family");
			if (font == "")
				font = "Arial";
			tempText = new GText(getIntAttr(attr, "x"), getIntAttr(attr, "y"), textSize > 0?textSize:12, 0, decodeColor(getStyleStrAttr(getStrAttr(attr, "style"), "fill")), "", font, wb.hc);
			wait4text = true;
			if (attr.getIndex("style") != -1) {
				String stil = attr.getValue("style");
				if (stil.indexOf("bold") != -1)
					tempText.setBold(true);
				if (stil.indexOf("italic") != -1)
					tempText.setItalic(true);
				if (stil.indexOf("underline") != -1)
					tempText.setUnderline(true);
				if (stil.indexOf("line-through") != -1)
					tempText.setStroke(true);
			}
		} else
			System.err.println("Ignoring SVG tag.." + name);
	}

	public void endElement(String name) {
		if (name.equals("defs"))
			defs--;//am urcat un nivel
	}

	public void endDocument() {
	}

	public void characters(String data) {
		//interpretez numai daca sunt in mijlocul uinui tag text
		if (!wait4text)
			return;
		//fac asta numai o data:
		wait4text = false;
		tempText.setText(data.trim());
		tempText.translateWith(0, -tempText.getTextHeight());
		wb.add(tempText);
		wb.send(tempText);
		tempText = null;
	}

	//metode statice care stiu sa se uite si sa scoata valoarea unui
	//atribut dintr-o lista si sa trateze absebta lui (prin valori default)
	private int getIntAttr(XMLAttributes attr, String a) {
		String value = attr.getValue(a);
		try {
			return (int) Float.parseFloat(value);
		} catch (Exception e) {
			int ret = convert2UserUnits(value);
			return ret == -1?0:ret;
		}
	}

	private static String getStrAttr(XMLAttributes attr, String a) {
		if (attr.getIndex(a) == -1)
			return "";
		else
			return attr.getValue(a);
	}

	//metode care stie sa returneze un sub-atribut dintr-o lista de stiluri
	//tratezeaza absenta atributului prin returnarea unei valori implicite
	private String getStyleStrAttr(String style, String attr) {
		StringTokenizer st = null;
		if (attr.equals("font-family"))
			st = new StringTokenizer(style, ":;");
		else
			st = new StringTokenizer(style, " :;");
		//cautam atributul:
		while (st.hasMoreElements())
			if (st.nextToken().equals(attr))
				break;
		if (st.hasMoreElements())
			return st.nextToken();
		else
			return "";
	}

	private int getStyleIntAttr(String style, String attr) {
		StringTokenizer st = new StringTokenizer(style, " :;");
		//cautam atributul:
		while (st.hasMoreElements())
			if (st.nextToken().equals(attr))
				break;
		if (st.hasMoreElements()) {
			String token = st.nextToken();
			try {
				return (int) Float.parseFloat(token);
			} catch (Exception e) {
				return convert2UserUnits(token);
			}
		} else
			return -1;
	}

	/**
	 * Metoda care stie sa converteasca de la unitatile SVG la cele utilizator.
	 * <br>Returneaza -1 daca value este rau formata
	 */
	int convert2UserUnits(String value) {
		int i = value.indexOf("px");
		try {
			if (i != -1) {//este in PiXeli
				return (int) Float.parseFloat(value.substring(0, i).trim());
			} else if ((i = value.indexOf("pt")) != -1) {//este in PoinTs
				return (int) (1.25 * Float.parseFloat(value.substring(0, i).trim()));
			} else if ((i = value.indexOf("pc")) != -1) {//este in PC (ce-or mai fi si alea???)
				return (int) (15 * Float.parseFloat(value.substring(0, i).trim()));
			} else if ((i = value.indexOf("mm")) != -1) {//este in MMetri
				return (int) (3.543307 * Float.parseFloat(value.substring(0, i).trim()));
			} else if ((i = value.indexOf("cm")) != -1) {//este in CMetri
				return (int) (35.43307 * Float.parseFloat(value.substring(0, i).trim()));
			} else if ((i = value.indexOf("in")) != -1) {//este in INchi
				return (int) (90 * Float.parseFloat(value.substring(0, i).trim()));
			} else if ((i = value.indexOf("%")) != -1) {//este precentual fata de viuport :)
				return (int) (width / 100.0 * Float.parseFloat(value.substring(0, i).trim()));
			} else //nu am gasit nici o unitate de masura cunoscuta:
				return -1;
		} catch (Exception e) {
			//convertirea a esuat
			return -1;
		}
	}

	/**
	 * Decodeaza culorile: <br>
	 * Din cele recunoascute de html/xml in cele cunoscute de Java.<br>
	 * Nu toate culorile, numai cele mai importante, sau cele
	 * in format explicit RGB. <br>
	 * Daca culoarea nu este recunoscuta.. intoarce Color.black
	 */
	static public Color decodeColor(String color2decode) {
		if (color2decode.equals("black"))
			return Color.black;
		else if (color2decode.equals("blue"))
			return Color.blue;
		else if (color2decode.equals("green"))
			return Color.green;
		else if (color2decode.equals("red"))
			return Color.red;
		else if (color2decode.equals("yellow"))
			return Color.yellow;
		else if (color2decode.equals("cyan"))
			return Color.cyan;
		else if (color2decode.equals("white"))
			return Color.white;
		else if (color2decode.equals("silver"))
			return Color.lightGray;
		else if (color2decode.equals("gray"))
			return Color.darkGray;
		else if (color2decode.equals("navy"))
			return new Color(0, 0, 128);
		else if (color2decode.equals("teal"))
			return new Color(0, 128, 128);
		else if (color2decode.equals("lime"))
			return new Color(0, 255, 0);
		else if (color2decode.equals("aqua"))
			return new Color(0, 255, 255);
		else if (color2decode.equals("maroon"))
			return new Color(128, 0, 0);
		else if (color2decode.equals("purple"))
			return new Color(128, 0, 128);
		else if (color2decode.equals("olive"))
			return new Color(128, 128, 0);
		else if (color2decode.equals("fuchista"))
			return new Color(255, 0, 255);
		else if (color2decode.startsWith("#")) {
			return new Color(Integer.parseInt(color2decode.substring(1), 16));
		} else
			return Color.black;
	}//end decodeColor
}

