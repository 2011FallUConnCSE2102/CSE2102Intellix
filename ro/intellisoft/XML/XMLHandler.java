
package ro.intellisoft.XML;

/**
 * class XMLHandler
 * @author: Maxiniuc Ovidiu
 * @company: Intellisoft SRL
 * @date: 17.08.2001(start)
 * @version 1.0
 *
 * Clasa adapter care trebui eimplementata de utililizator pentru a
 * putea beneficia de parsarea unui fisier XML <BR>
 *
 * Versiunea *light*
 */


public class XMLHandler {

	/**
	 * Receive notification of character data inside an element.<br>
	 * <b>XMLChar*</b><br>
	 * Implicitly prints out the String.
	 */
	public void characters(String data) {
		System.out.println(data);
	}

	/**
	 * Receive notification of the end of the document.
	 */
	public void endDocument() {
		System.out.println("Document end...");
	}

	/**
	 * Receive notification of the end of an element.
	 */
	public void endElement(String name) {
		System.out.println("Element end:" + name);
	}

	/**
	 * Receive notification of a parser warning or error.<br>
	 * Implicitly prints out the error.
	 */
	public void error(XMLParseException e) {
		System.out.println(e);
	}

	/**
	 * Receive notification of the beginning of the document.
	 */
	public void startDocument() {
		System.out.println("Document start...");
	}

	/**
	 * Receive notification of the start of an element.<br>
	 * Implicitly prints out the name and attributes.
	 * @see XMLAttributes
	 */
	public void startElement(String name, XMLAttributes attributes) {
		System.out.println("Element start:" + name);
		System.out.println("Attributes:");
		for (int i = 0; i < attributes.getLength(); i++)
			System.out.println("\t" + attributes.getIndex(attributes.getName(i)) + "\t" + attributes.getName(i) + "\t" + attributes.getValue(i));
	}

}