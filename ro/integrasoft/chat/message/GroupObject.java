///////////////////////////////////////////////////////////////////////////////////////////////////
// GroupObject.java
// Author		: Farkas Lajos-Mihaly
// Date		: 3.I.1998
// Copyright	: 1998 (c) Integrasoft SRL,Romania
// Last Modified: 17.VIII.1998 by Farkas Lajos
///////////////////////////////////////////////////////////////////////////////////////////////////

package ro.integrasoft.chat.message;

import java.awt.*;
import java.io.Serializable;

/**
 * GroupObject este clasa care se trimite ca parametru in mod obisnuit la mesajele de tip
 * grup.
 */
public class GroupObject implements Serializable {
	/**
	 * textul mesajului.Acesta este textul care se afiseaza daca mesajul este de tip text
	 */
	private String text;

	/**
	 * grupul dela care provine
	 */
	private String group;

	/**
	 * coordonata x stinga sus
	 */
	private int x;

	/**
	 * coordonata x dreapta jos
	 */
	private int x1;

	/**
	 * coordonata y stinga sus
	 */
	private int y;

	/**
	 * coordonata y dreapta jos
	 */
	private int y1;

	/**
	 * culoarea de desenare
	 */
	private Color c;

	/**
	 * latimea liniei de desenare
	 */
	private int w;

	/**
	 * paramtru suplimentar
	 */
	private Object o;

	/* constructor
	 *	@param group grupul de la care provine
	 */
	public GroupObject(String group) {
		this.group = group;
	}

	/**
	 * constructor
	 * @param group	grupul de la care provine
	 * @param text textul mesajului
	 */
	public GroupObject(String group, String text) {
		this.text = text;
		this.group = group;
	}

	/**
	 * constructor
	 *	@param	group	grupul de la care provine
	 *	@param	x			coordonata x stinga sus
	 *	@param	y			coordonata y stinga sus
	 *	@param	x1		coordonata x dreapta jos
	 *	@param	y1		coordonata y dreapta jos
	 *	@param	c			culoarea de desenare
	 *	@param 	w			grosimea liniei cu care se deseneaza
	 */
	public GroupObject(String group, int x, int y, int x1, int y1, Color c, int w) {
		this.group = group;
		this.x = x;
		this.x1 = x1;
		this.y = y;
		this.y1 = y1;
		this.c = c;
		this.w = w;
	}

	/**
	 * returneaza textul mesajului
	 */
	public String getText() {
		return text;
	}

	/**
	 * returneza grupul de la care provine
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * returneaza coordonata x stinga sus
	 */
	public int getX1() {
		return x;
	}

	/**
	 * returneaza coordonata x dreapta jos
	 */
	public int getX2() {
		return x1;
	}

	/**
	 * returneaza coordonata y stinga sus
	 */
	public int getY1() {
		return y;
	}

	/**
	 * returneaza coordonata y dreapta jos
	 */
	public int getY2() {
		return y1;
	}

	/**
	 * returneaza culoarea de desenare
	 */
	public Color getColor() {
		return c;
	}

	/**
	 * returneaza grosimea liniei
	 */
	public int getWidth() {
		return w;
	}

	/**
	 * returneaza parametrul suplimentar
	 */
	public Object getObject() {
		return o;
	}

	/**
	 * seteaza coordonata x stinga sus
	 * @param val valoarea coordonatei x stinga sus
	 */
	public void setX1(int val) {
		x = val;
	}

	/**
	 * seteaza coordonata x dreapta jos
	 * @param val valoarea coordonatei x dreapta jos
	 */
	public void setX2(int val) {
		x1 = val;
	}


	/**
	 * seteaza coordonata y stinga sus
	 * @param val valoarea coordonatei y stinga sus
	 */
	public void setY1(int val) {
		y = val;
	}

	/**
	 * seteaza coordonata y dreapta jos
	 * @param val valoarea coordonatei y dreapta jos
	 */
	public void setY2(int val) {
		y1 = val;
	}

	/**
	 * seteaza culoarea de desenare
	 * @param c culoarea de desenare
	 */
	public void setColor(Color c) {
		this.c = c;
	}

	/**
	 * seteaza grosimea liniei
	 *	@param w grosimea liniei
	 */
	public void setWidth(int w) {
		this.w = w;
	}

	/**
	 * seteaza parametrul suplimentar
	 * @param o parametrul suplimentar
	 */
	public void setObject(Object o) {
		this.o = o;
	}

	/**
	 * seteaza textul mesajului
	 * @param text textul mesajului
	 */
	public void setText(String text) {
		this.text = text;
	}
}