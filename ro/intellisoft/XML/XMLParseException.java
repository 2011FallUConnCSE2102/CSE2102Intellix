
package ro.intellisoft.XML;

/**
 * class XMLParseException
 * @author: Maxiniuc Ovidiu
 * @company: Intellisoft SRL
 * @date: 17.08.2001(start)
 * @version 1.0
 *
 * clasa careeste aruncata la aparitia unei erori in citirea
 * si parsarea unui fisier
 *
 * Versiunea *light*
 */

public class XMLParseException extends Exception {
	private int line = 0,
	row = 0;

	/**
	 * Construnctorii nu pot fi apelati decat din acest paket
	 * de asemenea si metoda setPosition()
	 */
	private XMLParseException(String s) {
		super(s);
	}

	/**
	 * Construnctorii nu pot fi apelati decat din acest paket
	 * de asemenea si metoda setPosition()
	 */
	XMLParseException(String s, int line, int row) {
		super(s);
		setPosition(line, row);
	}

	/**
	 * Construnctorii nu pot fi apelati decat din acest paket
	 * de asemenea si metoda setPosition()
	 */
	void setPosition(int line, int row) {
		this.line = line;
		this.row = row;
	}

	/**
	 * In skimb oricine poate afla linia, coloana si mesajul de eroare produs.
	 */
	public int getLine() {
		return line;
	}

	/**
	 * In skimb oricine poate afla linia, coloana si mesajul de eroare produs.
	 */
	public int getRow() {
		return row;
	}

	/**
	 * In skimb oricine poate afla linia, coloana si mesajul de eroare produs.
	 */
	public String toString() {
		return ("XMLParseException: " + super.getMessage() + " at (" + line + ", " + row + ").");
	}
}