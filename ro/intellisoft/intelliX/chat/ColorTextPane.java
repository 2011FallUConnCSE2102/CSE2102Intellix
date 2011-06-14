
package ro.intellisoft.intelliX.chat;

/**
 class ColorTextPane
 - new class which demo-es Hermix functions
 o componenta text care afiseaza un text colorat
 @author: Ovidiu MAXINIUC
 @author: Doru PETRUC
 @date:  01.06.2001 (start)
 @date:	27.06.2001 (last update)
 */

import javax.swing.*;
import java.util.ArrayList;

public class ColorTextPane extends JEditorPane  {

	private static int MAX_LINES = 50;                  //memoram maxim MAX_LINES linii colorate
	private ArrayList lines = new ArrayList(MAX_LINES);
	private ArrayList colors = new ArrayList(MAX_LINES);
	private int first = 0;                                    //prima linie ce se va afisa
	private int last = 0;                                     //ultima linie (lista circulara), se calculeaza modulo MAX_LINES
	String fontFace = new String("Times");					//tipul fontului folosit
	String colorBackground = new String("white");			//culoarea de background
	String fontSize = new String("normal");					//marimea fontului
	String fontSizeHtml = new String("");					//string de contruire tag pt marimea fontului
	String bkImage = null;

	public ColorTextPane() {
		super.setContentType("text/html");
		super.setText("<html> </html>");
		super.setEditable(false);
	}//end ColorTextPane

	private String getBackgroundAttribute() {
		if (bkImage == null)
			return " bgcolor='" + colorBackground + "' ";
		else
			return " background='" + bkImage + "' ";
	}

	public void setText(String text, String color) {
		lines.add(0, text);//[0]=text;
		colors.add(0, color);//[0]=color;
		first = 0;
		last = 1;

		super.setText("<html><body " + getBackgroundAttribute() + "><font face='" + fontFace + "' color='" + (String) colors.get(0) + "' " + fontSizeHtml + " >" + (String) lines.get(0) + "</font></body></html>");
		setCaretPosition(super.getDocument().getLength());
	}//end setText


	//seteaza marimea fontului
	public void setFontSize(String newFontSize) {
		this.fontSize = newFontSize;
		if (this.fontSize.equals("normal"))
			fontSizeHtml = "";
		else
			fontSizeHtml = "size = " + this.fontSize;
	}//end  setFontSize


	public void setText(String text) {
		this.setText(text, "black");
	}//end  setText


	//clear the text
	public void clearScreen() {
		first = 0;
		last = 0;
		super.setText("<html><body " + getBackgroundAttribute() + "></body></html>");
	}//end clearScreen

	//seteaza tipul fontului
	public void setFontFace(String newFontFace) {
		this.fontFace = newFontFace;
		setCaretPosition(getDocument().getLength());
	}//end  setFontFace

	//seteaza culoarea de background
	public void setBackgroundColor(String newBackgroundColor) {
		this.colorBackground = newBackgroundColor;
		super.setCaretPosition(getDocument().getLength());
	}//end setBackgroundColor

	public void setBackgroundImage(String bkImage) {
		this.bkImage = bkImage;
	}

	public int getMaxLines() {
		return MAX_LINES;
	}

	public void setMaxLines(int maxLines) {
		MAX_LINES = maxLines;
	}

	public void append(String text) {
		append(text, "black");
	}

	public void append(String text, String color) {
		lines.add(0, text);//[last%MAX_LINES]=text;
		colors.add(0, color);//[last%MAX_LINES]=color;
		if (last < MAX_LINES)
			last++;
		else
			last = MAX_LINES;
		StringBuffer temp = new StringBuffer("");
		for (int i = last - 1; i >= 0; i--){
			temp.append("<font color='" + (String) colors.get(i) + "'>");
			temp.append((String) lines.get(i) + "</font><br>");
		}
		try {
			super.setText("<html><body " + getBackgroundAttribute() + "><font face='" + fontFace + "' " + fontSizeHtml + " >" + temp + "</font></body></html>");
			setCaretPosition(getDocument().getLength());
		} catch (java.lang.NullPointerException e) {
			System.out.println("+"+temp);
		} catch (java.lang.RuntimeException e) {
			System.out.println("*"+temp);
		}

	}// end append
}//class  ColorTextPane
