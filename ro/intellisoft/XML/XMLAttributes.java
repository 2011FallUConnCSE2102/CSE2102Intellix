
package ro.intellisoft.XML;

/**
 * class XMLAttributes
 * @author: Maxiniuc Ovidiu
 * @company: Intellisoft SRL
 * @date: 17.08.2001(start)
 * @version 1.0
 *
 * Clasa care stokeaza atributele unui element XML.
 *
 * Versiunea *light*
 */

import java.util.Vector;

public class XMLAttributes {
	/**
	 * Campuri private care stokeaza informatiile propriuzise
	 */
	Vector attr = new Vector(10, 10),
	values = new Vector(10, 10);

	/**
	 * Constructorul este apelabil numai le nivelul paketului <br>
	 * De fapt numai MiniParsr-ul are drept sa construiasca o asemenea clasa. <br>
	 * implementarile Handler-ului vor putea numai sa extraga valorile
	 * in vederea unei ulterioare prelucrari.
	 */
	XMLAttributes() {
	}

	/**
	 * metoda aceasta este apelabila numai din acest paket<br>
	 * De fapt numai XMLMiniParser are voie sa instantieze si sa
	 * adauge campuri in vestorul de atribute.
	 * @param name numele atributtului ce se adauga
	 * @param value valoarea atributtului cu numele specificat ce se adauga
	 */
	void addAttribute(String name, String value) {
		attr.add(name);
		values.add(value);
	}

	/**
	 * Look up the index of an attribute by XML 1.0 qualified name.
	 */
	public int getIndex(String name) {
		return attr.lastIndexOf(name);
	}

	/**
	 * Return the number of attributes in the list.
	 */
	public int getLength() {
		return attr.size();
	}

	/**
	 * Look up an attribute's XML 1.0 qualified name by index.
	 */
	public String getName(int index) {
		return attr.elementAt(index).toString();
	}

	/**
	 * Look up an attribute's value by index.
	 */
	public String getValue(int index) {
		return values.elementAt(index).toString();
	}

	/**
	 * Look up an attribute's value by XML 1.0 qualified name.
	 */
	public String getValue(String name) {
		if (attr.lastIndexOf(name) == -1)
			return "";
		else
			return values.elementAt(attr.lastIndexOf(name)).toString();
	}

}