///////////////////////////////////////////////////////////////////////////////////////////////////
// Message.java
// Author				: Farkas Lajos-Mihaly
// Date					: 12.I.1998
// Copyright		: 1998 (c) Integrasoft SRL,Romania
// Last Modified: 3.I.1998 by Farkas Lajos
///////////////////////////////////////////////////////////////////////////////////////////////////
/**
 *	Message este clasa parinte pentru toate tipurile de mesage care se vehiculeaza intre server si
 *	client.
 */

package ro.integrasoft.chat.message;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
	/**
	 * Timpul receptionarii mesajului de catre server.Este manul,luna,ziua,ora,minutul,secunda in care
	 * a ajuns pachetul la server
	 */
	private Date received = null;

	/**
	 * Numele (nick-ul) utilizatorului care a trimis mesajul.
	 */
	private String user;

	/**
	 * Datele care se trimit cu mesajul ( daca se trimit date, null daca nu ).Fiecare descendent al
	 * clasei poate folosi sau nu suportul acesta, pentru a trimite datele.
	 */
	private Object data;

	/**
	 * Tipul mesajului ( folosit pentru refacerea mesajului origilal ).
	 */
	private String type;

	/**
	 * Construcorul vid al clasei.
	 */
	public Message() {
		this.user = "";
		this.data = null;
		this.received = null;
	}

	/**
	 * Construieste un mesaj, punind in el datele care se specifica.
	 * @param data datele mesajului
	 */
	public Message(Object data) {
		this.user = "";
		this.data = data;
		this.received = null;
	}

	/**
	 * Construieste mesajul ca apartinind unui anumit utilizator si avind si date
	 * @param uid identificatorul utilizatorului
	 * @param data datele mesajului
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Seteaza datele mesajului.
	 * @param data datele mesajului
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * Seteaza timpul in care a fost inregistrat mesajul.Aceasta metoda este apelata doar o singura
	 * data pentru un mesaj dat, de server, cind receptioneaza mesajul.Clientul o poate apela sau nu
	 * in functie de implementare.
	 */
	public void setRecTime() {
		received = new Date();
	}

	/**
	 * Seteaza tipul mesajului.
	 * @param type tipul mesajului
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Returneaza identificatorul utilizatorului care agenerat mesajul (daca mesajul provne de la un
	 * client, nedefinit "" daca mesajul provine direct de la server ).
	 * @return identificatorul mesajului.
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Returneza un Object cu datele mesajului, sau null daca mesajul nu a avut date.
	 * @return datele mesajului
	 */
	public Object getData() {
		return data;
	}

	/**
	 * Returneaza timpul in care a fost receptionat mesajul.
	 * @return timpul receptionarii
	 */
	public Date getRecTime() {
		return received;
	}

	/**
	 * Returneaza tipul mesajului
	 * @return tipul mesajului
	 */
	public String getType() {
		return type;
	}

	/**
	 * realizeaza o afisare sub forma de sir de caractere a mesajului
	 */
	public String toString() {
		return "Type=" + type + " UId=" + user + " rectime=" + received.toString() + " data=" + (data != null?data.toString():"null");
	}
}