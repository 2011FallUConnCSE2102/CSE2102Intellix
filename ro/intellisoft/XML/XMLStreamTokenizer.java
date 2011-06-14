
package ro.intellisoft.XML;

/**
 * class XMLStreamTokenizer
 * @author: Maxiniuc Ovidiu
 * @company: Intellisoft SRL
 * @date: 17.08.2001(start)
 * @version 1.0
 *
 * Deoarece StreamTokenizer-ul nu imi faca ca lume aparsarea in token-uri
 *
 */


import java.io.IOException;
import java.io.Reader;

public class XMLStreamTokenizer {

	/**
	 * Fisierul din care citesc
	 */
	private Reader r;

	/**
	 * Bufferul in care citesc temporar - pentru marirea vitezei si
	 * posibilitatea intoarcerii inapoi. ;-)
	 */
	private char[] buffer = new char[65510];

	/**
	 * Un token temporar, folosit in interior.
	 * <br> Cred ca poate fi scos.
	 */
	private String tempToken = "";

	/**
	 * Indica pozitzia la care am ajuns cu parsarea in buffer
	 */
	private int pointer = 0;

	/**
	 * Nr de octeti cititi ultima data.
	 */
	private int bufferLen = 0;

	/**
	 * Linia curenta.
	 */
	private int line = 1;

	/**
	 * Coloana curenta.
	 */
	private int row = 0;


	/**
	 * Variabila publica cu acelasi rol ca si in java.io.StreamTokenizer.<br>
	 * Reprezinta ultimul sir citit.
	 */
	public String sval = null;

	/**
	 * Variabila publica cu acelasi rol ca si in java.io.StreamTokenizer.<br>
	 * Retine tipul ultimului token citit.<br>
	 * Initial are valoarea -4.
	 */
	public int ttype = -4;

	/**
	 * Constanta publica cu acelasi rol ca si in java.io.StreamTokenizer.
	 */
	public static final int TT_WORD = -3;

	/**
	 * Constanta publica cu acelasi rol ca si in java.io.StreamTokenizer.
	 */
	public static final int TT_NUMBER = -2;

	/**
	 * Constanta publica cu acelasi rol ca si in java.io.StreamTokenizer.
	 */
	public static final int TT_EOL = -1;

	/**
	 * Constanta publica cu acelasi rol ca si in java.io.StreamTokenizer.
	 */
	public static final int TT_EOF = -5;

	/**
	 * Constructor. <br>
	 * @param r fisierul care va fi 'spart' in bucati.
	 */
	public XMLStreamTokenizer(Reader r) throws IOException, XMLParseException {
		this.r = r;
		//r.reset();
		if (!r.ready())
			throw new XMLParseException("Error handling file...", 0, 0);
	}

	/**
	 * Parseaza urmatorul token.<br>
	 * Asemenator cu metoda din StreamTokenizer.<br>
	 * @return tipul token'ului care a fost parsat.
	 */
	public int nextToken() throws IOException {
		ttype = -4;
		sval = null;
		int c;
		this.tempToken = null;
		do {
			c = readChar();
			if (c == TT_EOF) {
				ttype = c;
				return ttype;
			} else if (c == TT_EOL) {
				ttype = c;
				line++;
				row = 1;
			}
		} while (c <= ' ' || c > 255);
		if (c == '"' || c == '=' || c == '<' || c == '>' || c == '-' || c == '?' || c == '!' || c == '/')
			ttype = c;
		else {
			tempToken = (char) c + readWord();
			ttype = TT_WORD;
			sval = tempToken;
		}
		return ttype;
	}

	/**
	 * Returneaza stringu' care incepe la pozitia curenta si se termina
	 * cu un caracter inainte de %what%.<br>
	 * De exemplu daca vreau continutul unui sir intre ghilimele:<br>
	 * <pre>      quotesString=searchFor("\""); </pre>
	 * sau sfarsitul de comentariu in XML:
	 * <pre>      quotesString=searchFor("-->"); </pre>
	 * ATENTIE: pointerul ramane pozitionat pe primul caracter duin %what%.
	 */
	public String searchFor(String what) throws IOException {
		String toReturn = "";
		int c;
		while ((c = readChar()) != TT_EOF) {
			if (c == TT_EOL) {
				line++;
				row = 1;
				toReturn += "\n";
			} else
				toReturn += (char) c;
			if (toReturn.endsWith(what)) {
				pointer -= what.length();
				break;
			}
		}
		if ((ttype = c) == TT_EOF)
			return toReturn;
		else
			return toReturn.substring(0, toReturn.indexOf(what));
	}

	/**
	 * Metoda care ia resteul dintr-un sir.
	 */
	private String readWord() throws IOException {
		String build = "";
		int c;
		do {
			c = readChar();
			if (c <= ' ' || c > 255) {
				//am dat de un spatiu, ne oprim aici
				break;
			} else if (c == '"' || c == '=' || c == '<' || c == '>' || c == '?' || c == '!' || c == '/') {
				//am dat de un caracter special, ne oprim aici
				break;
			} else if (c == '-') {
				//poate fi sfarsit de commentariu sau cratima
				if ((c = readChar()) == '-') {
					//clar, sfarsit de comentariu:
					pointer--;
					row--;
					break;
				} else {
					pointer--;
					row--;
					//citim in continuare, dar punem caratima
					build += "-";
				}
			} else {
				//caracter normal:
				build += (char) c;
			}
		} while (c <= 255 && c > ' ');
		pointer--;
		row--;
		return build;
	}

	/**
	 * Metoda care returneaza urmatorul caracter din stream. <br>
	 * Stie sa recunoasca sfarsitul de linie si de fisier.<br>
	 * Actualizeza nr de coloana, dar nu si linia curenta.
	 */
	private int readChar() throws IOException {
		if (pointer >= bufferLen) {// trebuie sa mai citim un calup
			bufferLen = this.r.read(buffer, 0, 65500);
			pointer = 0;
			if (bufferLen == -1)
				return TT_EOF;
		}
		if (buffer[pointer] == '\r') {
			if (buffer[pointer + 1] == '\n') {
				pointer += 2;
				return TT_EOL;
				//sfarsit de linie in DOS
			} else {
				pointer++;//ignoram acest caracter si ...
				return readChar();//returnam urmatorul caracter
			}
		} else if (buffer[pointer] == '\n') {
			pointer++;
			return TT_EOL;
			//sfarsit de linie in UNIX
		} else {//in sfarsit un caracter normal, inclusiv spatziu
			//ii 'salvam' pozitia:
			row++;
			//pregatim urmatorul caracter de citit
			pointer++;
			//returnam caracterul 'normal'
			return buffer[pointer - 1];
		}
	}


	/**
	 * Returneaza nr. liniei curente.<br>
	 * (de exemplu pemtru a afisa o eroare la parsare) <br>
	 */
	public int getLine() {
		return line;
	}

	/**
	 * Returneaza nr. coloanei curente.<br>
	 * (de exemplu pemtru a afisa o eroare la parsare) <br>
	 * *** consideram /t ca are lungimea 1. <br>
	 * *** la fel si /000 (caracterul nul). <br>
	 * deci toate cacarterele au aceeasi dimensiume. <br>
	 */
	public int getRow() {
		return row;
	}
}

