///////////////////////////////////////////////////////////////////////////////////////////////////
// ERRMessage.java
// Author		: Farkas Lajos-Mihaly
// Date		: 3.I.1998
// Copyright	: 1998 (c) Integrasoft SRL,Romania
// Last Modified: 17.VIII.1998 by Farkas Lajos
///////////////////////////////////////////////////////////////////////////////////////////////////

package ro.integrasoft.chat.message;

/**
 * ERRMessage este clasa care implementeaza mesajele care se vehiculeaza pe un grup de discutii.
 * Tipul alocat este <b>error</b>.
 */

public class ERRMessage extends Message {

	private int errId;

	/**
	 * constructor
	 */
	public ERRMessage() {
		super();
		setType("error");
		errId = -1;
	}

	/**
	 * constructor
	 * @param errId identificatorul mesajului
	 */
	public ERRMessage(int errId) {
		super();
		setType("error");
		this.errId = errId;
	}

	/**
	 * constructor
	 * @param data paramtetrii mesajului
	 */
	public ERRMessage(Object data) {
		super();
		setType("error");
		setData(data);
	}

	/**
	 * constructor
	 * @param errId	identificatorul mesajului
	 * @param data	parametrii mesajului
	 */
	public ERRMessage(int errId, Object data) {
		super();
		setType("error");
		setData(data);
		this.errId = errId;
	}

	/**
	 * returneaza identificatorul mesajului
	 */
	public int getErrId() {
		return errId;
	}

	/**
	 * seteaza identificatorul mesajului
	 */
	public void setErrId(int errId) {
		this.errId = errId;
	}
}