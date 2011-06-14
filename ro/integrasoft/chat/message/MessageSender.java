///////////////////////////////////////////////////////////////////////////////////////////////////
// MessageSender.java
// Author		: Farkas Lajos-Mihaly
// Date		: 3.I.1998
// Copyright	: 1998 (c) Integrasoft SRL,Romania
// Last Modified: 17.VII.1998 by Farkas Lajos
///////////////////////////////////////////////////////////////////////////////////////////////////

package ro.integrasoft.chat.message;

import java.io.IOException;
import java.net.Socket;

/**
 * Trimite mesaje serializate pe un socket
 */
public class MessageSender extends ObjectWriter {
	/**
	 * socket-ul de comunicatii
	 */
	private Socket s;
	//-----------------------------------------------------------------------------------------------------------------------------------
	/**
	 * constructorul clasei
	 * @param socket sockettul pe care se trimit datele
	 */
	public MessageSender(Socket s) throws IOException {
		super(s.getOutputStream());
		this.s = s;
		start();
	}

	//-----------------------------------------------------------------------------------------------------------------------------------
	/**
	 * nefolosit
	 */
	public void close() throws IOException {
	}

	/**
	 * pune un mesaj pe socket
	 * @param m mesajul de trimis pe socket
	 * @return nr de octeti pe care a fost scris mesajul
	 */
	public int addMessage(Message m) throws IOException {
		return addObject(m);
		//System.out.println("sender.addMessage.. done");//by ME
	}
}