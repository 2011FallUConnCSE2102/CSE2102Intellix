
package ro.intellisoft.XML;

/**
 * class XMLMiniParser
 * @author: Maxiniuc Ovidiu
 * @company: Intellisoft SRL
 * @date: 17.08.2001(start)
 * @version 1.1
 *
 * Parseaza un fisier XML in elemente primare.<BR>
 * Nu face verificarea daca este conform DTD.
 */

import java.io.*;

public class XMLMiniParser {

	/**
	 * Camp local, nu ar trebui sa fie vizibile
	 */
	XMLHandler handler;

	/**
	 * Camp local, nu ar trebui sa fie vizibile
	 */
	XMLStreamTokenizer st;

	/**
	 * Default constructor. <Br>
	 * Does nothing.
	 */
	public XMLMiniParser() {
		//do nothing yet;
	}

	/**
	 * Metoda principala. Ia ca argunente un @see File filename si un
	 * @see  XMLHandler handler si parseaza fisierul, trimitand
	 * elementele handler'ului.
	 */
	public void parse(File filename, XMLHandler handler) {
		this.handler = handler;
		//construiesc un tokenizer pe baza fisierului
		try {
			st = new XMLStreamTokenizer(new BufferedReader(new InputStreamReader(new FileInputStream(filename))));
		} catch (Exception e) {
			e.printStackTrace();
			this.handler.error(new XMLParseException("File not found!", 0, 0));
		}
		try {
			parseDocument();
		} catch (Exception e) {
			e.printStackTrace();
			this.handler.error(new XMLParseException("Bad XMLFormat!", st.getLine(), st.getRow()));
		}
	}

	/**
	 * Metoda care parsaza un sir de caractere generat/citit.
	 * parse(File, XMLHandler) ~ File->read()->String parse(String, XMLHandler)
	 */
	public void parse(String stringToParse, XMLHandler handler) {
		this.handler = handler;
		//construiesc un tokenizer pe baza sirului
		try {
			st = new XMLStreamTokenizer(new java.io.StringReader(stringToParse));
		} catch (IOException e1) {
			System.out.println("e1 = " + e1);
		} catch (XMLParseException e1) {
			System.out.println("e1 = " + e1);
		}

		try {
			parseDocument();
		} catch (Exception e) {
			e.printStackTrace();
			this.handler.error(new XMLParseException("Bad XMLFormat!", st.getLine(), st.getRow()));
		}
	}

	/**
	 * Un document XML este format din mai multe elemente:<br>
	 * <b>XMLDocument</b> == <b>XMLElement*</b>
	 * <br>
	 * Aici se face testarea daca fisierul este intr-adevar
	 * conform sintaxei XML conform:<br>
	 * <b>XMLIdentTag</b> || <b>XMLTypeTag</b>
	 */
	private void parseDocument() throws Exception {
		this.handler.startDocument();
		st.nextToken();
		if (!verifyIdentTag())
			throw new XMLParseException("Bad XML header", st.getLine(), st.getRow());
		st.nextToken();
		if (!verifyTypeTag())
			throw new XMLParseException("Bad XML header", st.getLine(), st.getRow());
		while (st.nextToken() != st.TT_EOF) {
			parseElement();
		}
		this.handler.endDocument();
	}

	/**
	 * Un element XML poate fi de mai multe tipuri:<br>
	 * <b>XMLElement</b> == <b>XML1TagElemet</b> ||
	 * <b>XML2TagElement</b> <br>
	 * Pentru fiecare tip se apeleaza metodele respective. <br>
	 * Pentru a ne da seama de tipul elementului construim lista de atribute
	 * si apelam start(element) si vedem cu ce se termina tag-ul xml.
	 */
	private void parseElement() throws Exception {
		boolean isEnd = false;
		if (st.ttype != '<') {
			//text din interiorul unui tag... probabil
			this.handler.characters(parseCharacters('<'));
			st.nextToken();
		}
		if (st.nextToken() == '!') {
			//poate incepe un comentariu...
			parseCommentary();
			return;
		} else if (st.ttype == '/') {
			st.nextToken();
			isEnd = true;
		}
		if (st.ttype != st.TT_WORD) {
			this.handler.error(new XMLParseException("Bad XML format... (wait for id, found " + (char) st.ttype + ")", st.getLine(), st.getRow() - 1));
			do {
				st.nextToken();
			} while (st.ttype != st.TT_WORD);
		}
		//daca am ajuns aici atunci sigur am numele unui tag
		String tagName = st.sval;
		//daca nu este un tag de sfarsit...
		//parsam atributele:
		//sa dam jos si atributele:
		if (!isEnd) {
			XMLAttributes attr = parseAttributes();
			//apelam handler-ul pentru inceputul de element curent:
			this.handler.startElement(tagName, attr);
			//acum ar trebui sa fim pe caracterul / daca tagul este simplu
			//sau pe > daca tagul are si parte de final </tag>
			if (st.ttype == '/')
				st.nextToken();
		} else { //este tag de sfarsit:
			this.handler.endElement(tagName);
			st.nextToken();
		}
		//in orice caz se termina cu >
		if (st.ttype != '>')
			if (st.ttype == st.TT_WORD)
				this.handler.error(new XMLParseException("Bad XML format...(wait for >" + (isEnd?"":" or />") + "), found " + st.sval + ")", st.getLine(), st.getRow() - st.sval.length() - 1));
			else
				this.handler.error(new XMLParseException("Bad XML format...(wait for >" + (isEnd?"":" or />") + "), found " + (char) st.ttype + ")", st.getLine(), st.getRow() - 2));
	}


	/**
	 * Un element XML poate fi de mai multe tipuri:<br>
	 * <b>XMLAttr</b> == AttrName = "<<b>XMLChars</b>>" <br>
	 * Pentru fiecare tip se apeleaza metodele respective.<br>
	 * Pentru a ne da seama de tipul elementului construim lista de atribute
	 * si apelam start(element) si vedem cu ce se termina tag-ul xml.
	 */
	private XMLAttributes parseAttributes() throws Exception {
		XMLAttributes attr = new XMLAttributes();
		while (st.nextToken() == st.TT_WORD) {
			String oneAttribute = st.sval;
			if (st.nextToken() != '=')
				if (st.ttype == st.TT_WORD)
					handler.error(new XMLParseException("Bad XML format...(wait for =, found " + st.sval + ")", st.getLine(), st.getRow() - st.sval.length() - 1));
				else
					handler.error(new XMLParseException("Bad XML format...(wait for =, found " + (char) st.ttype + ")", st.getLine(), st.getRow() - 2));
			if (st.nextToken() != '"')
				if (st.ttype == st.TT_WORD)
					handler.error(new XMLParseException("Bad XML format...(wait for \", found " + st.sval + ")", st.getLine(), st.getRow() - st.sval.length() - 1));
				else
					handler.error(new XMLParseException("Bad XML format...(wait for \", found " + (char) st.ttype + ")", st.getLine(), st.getRow() - 2));
			String oneValue = st.searchFor("\"");
			if (st.nextToken() != '"')
				if (st.ttype == st.TT_WORD)
					handler.error(new XMLParseException("Bad XML format...(wait for \", found " + st.sval + ")", st.getLine(), st.getRow() - st.sval.length() - 1));
				else
					handler.error(new XMLParseException("Bad XML format...(wait for \", found " + (char) st.ttype + ")", st.getLine(), st.getRow() - 2));
			//aici suntem pe caracterul "
			//salvam atruibutul
			attr.addAttribute(oneAttribute, oneValue.trim());
			//si trecem la elementul urmator:(incheiel while=ul)
		}
		//daca nu suntem pozitonat pe un cuvant inseamna ca s-a terminat lista
		//de atribute si returnam lista in formatul curent
		return attr;
	}

	/**
	 * Acestea se gasesc intre 2 tag-uri <start> si <end>
	 * conform gramnaticii caracterele sunt insirui de ascii: <br>
	 * <b>XMLChars</b>       == <b>CDATA*</b> <br>
	 * Cand se termina?  la urmatorul endsWith.
	 */
	private String parseCharacters(char endsWith) throws Exception {
		String oneValue = st.sval + st.searchFor((char) endsWith + "");
		return oneValue.trim();
	}

	/**
	 * Conform gramnaticii comentariile sunt insirui de ascii:
	 * <b>XMLChars</b>       == <b>CDATA*</b><br>
	 * Cand incep?      la <!-- (presupunem < deja validat) </br>
	 * Cand se termina?  la urmatorul \-->.
	 */
	private void parseCommentary() throws Exception {
		if (st.nextToken() != '-')
			if (st.ttype == st.TT_WORD)
				handler.error(new XMLParseException("Bad XML format...(wait for <!--, found " + st.sval + ")", st.getLine(), st.getRow() - st.sval.length() - 1));
			else
				handler.error(new XMLParseException("Bad XML format...(wait for <!--, found " + (char) st.ttype + ")", st.getLine(), st.getRow() - 2));
		if (st.nextToken() != '-')
			if (st.ttype == st.TT_WORD)
				handler.error(new XMLParseException("Bad XML format...(wait for <!--, found " + st.sval + ")", st.getLine(), st.getRow() - st.sval.length() - 1));
			else
				handler.error(new XMLParseException("Bad XML format...(wait for <!--, found " + (char) st.ttype + ")", st.getLine(), st.getRow() - 2));
		String comment = "";
		//cautam sfarsitul de commentariu:
		do {
			if (st.nextToken() == '-')
				comment += '-';
			else if (st.ttype == '>')
				comment += '>';
			else
				comment = "";
		} while (!comment.endsWith("-->"));
	}


	/**
	 * Verifica daca declaratia este de tipul: <BR>
	 *  <pre> <?xml version="1.0" standalone="no"?> </pre>
	 */
	private boolean verifyIdentTag() throws Exception {
		if (st.ttype != '<')
			return false;
		if (st.nextToken() != '?')
			return false;
		st.nextToken();
		if (!st.sval.equals("xml"))
			return false;
		st.nextToken();
		if (!st.sval.equals("version"))
			return false;
		if (st.nextToken() != '=')
			return false;
		if (st.nextToken() != '"')
			return false;
		if (!st.searchFor("\"").equals("1.0"))
			return false;
		if (st.nextToken() != '"')
			return false;
		st.searchFor("?");
		st.nextToken();
		return st.nextToken() == '>';
	}

	/**
	 * Verifica daca declaratia este de tipul:
	 * <pre><!DOCTYPE ...> </pre>
	 * etc
	 */
	boolean verifyTypeTag() throws Exception {
		if (st.ttype != '<')
			return false;
		st.nextToken();
		if (st.ttype != '!')
			return false;
		while (st.ttype != '>')
			st.nextToken();
		return true;
	}


}