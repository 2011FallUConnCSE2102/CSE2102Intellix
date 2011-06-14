///////////////////////////////////////////////////////////////////////////////////////////////////
// GRPMessage.java
// Author		: Farkas Lajos-Mihaly
// Date		: 3.I.1998
// Copyright	: 1998 (c) Integrasoft SRL,Romania
// Last Modified: 3.I.1998 by Farkas Lajos
///////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * GRPMessage este clasa care implementeaza mesajele care se vehiculeaza pe un grup de discutii.
 * Tipul alocat este <b>group</b>.
 */

package ro.integrasoft.chat.message;


public class GRPMessage extends Message {

	private int msgid;

	/**
	 * constructor
	 * @param msgid identificatorul mesajului
	 */
	public GRPMessage(int msgid) {
		super();
		setType("group");
		this.msgid = msgid;
	}

	/**
	 * constructor
	 * @param msgid identificatorul mesajului
	 * @param o			parametrii mesajului
	 */
	public GRPMessage(int msgid, Object o) {
		super(o);
		setType("group");
		this.msgid = msgid;
	}

	/**
	 * seteaza identificatorul mesajului
	 */
	public void setMsgId(int msgid) {
		this.msgid = msgid;
	}

	/**
	 * returneaza identificatorul mesajului
	 */
	public int getMsgId() {
		return msgid;
	}
}