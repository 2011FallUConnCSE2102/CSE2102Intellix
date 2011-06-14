///////////////////////////////////////////////////////////////////////////////////////////////////
// MessageReader.java
// Author		: Farkas Lajos-Mihaly
// Date		: 3.I.1998
// Copyright	: 1998 (c) Integrasoft SRL,Romania
// Last Modified: 17.VII.1998 by Farkas Lajos
///////////////////////////////////////////////////////////////////////////////////////////////////

package ro.integrasoft.chat.message;

import java.io.IOException;
import java.net.Socket;

/**
 * Aceasta clasa citeste obiecte serializate de pe un socket de comunicatii
 */
public class MessageReader extends ObjectReader {
	/**
	 * socket-ul de comunicatii de pe care se citesc mesajele
	 */
	private Socket s;

	//folosit pentru a termina 'cu bine' thread -ul
	private boolean stopp = false;

	//-----------------------------------------------------------------------------------------------------------------------------------

	/**
	 * constructorul clasei
	 */
	public MessageReader(Socket s) throws IOException {
		super(s.getInputStream());
		this.s = s;
		start();
	}

	//-----------------------------------------------------------------------------------------------------------------------------------
	/**
	 * returneaza urmatorul mesaj de pe socket.Daca nu este mesaj disponibil, se asteapta
	 * un mesaj pina cind se intrerupe conexiunea, sau se inchide explicit
	 */
	public Message nextMessage(IntHolder size) throws Exception {
		Message m;
		m = (Message) nextObject(size);
		return m;
	}

	public void stopThread() {
		stopp = true;
	}


	/**
	 * nefolosit
	 */
	public void close() throws IOException {
		try {
			s.close();
		} catch (Exception e) {
		}
	}
}