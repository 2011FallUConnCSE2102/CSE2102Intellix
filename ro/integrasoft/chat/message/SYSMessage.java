///////////////////////////////////////////////////////////////////////////////////////////////////
// SYSMessage.java
// Author		: Farkas Lajos-Mihaly
// Date		: 3.I.1998
// Copyright	: 1998 (c) Integrasoft SRL,Romania
// Last Modified: 3.I.1998 by Farkas Lajos
///////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * SYSMessage este clasa de mesaje sistem, dedicate pentru comunicarea dintre server si client.
 * Clasa nu include si comenzile de la si catre client.Tipul alocat acestor mesaje este <b>system</b>
 */

package ro.integrasoft.chat.message;

public class SYSMessage extends Message {
	/**
	 * Identificatorul mesajului.
	 */
	private int id;

	//mesaje care tin de login

	/**
	 * constructor
	 * @param id identificatorul mesajului
	 */
	public SYSMessage(int id) {
		super();
		setType("system");
		setMsgId(id);
	}

	/**
	 * constructor
	 * @param id identificatorul mesajului
	 * @param o		paramtrii mesajului
	 */
	public SYSMessage(int id, Object o) {
		super(o);
		setType("system");
		setMsgId(id);
	}

	/**
	 * Seteaza identificatorul mesajului
	 * @param id identificatorul mesajului
	 */
	public void setMsgId(int id) {
		this.id = id;
	}

	/**
	 * Returneaza identificatorul mesajului
	 * @return identificatorul mesajului
	 */
	public int getMsgId() {
		return this.id;
	}
}