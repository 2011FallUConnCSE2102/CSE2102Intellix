
package ro.integrasoft.chat.message;

/**
 * Title:        Integer Holder
 * Description:  Inglobeaza un intreg. Permite sa transmit si sa returnez un
 *                  intreg ca parametru al unei functii.
 * Copyright:    Copyright (c) 2001
 * Company:      Intellisoft SRL
 * @author       Ovidiu Maxiniuc
 * @version 1.0
 */

public class IntHolder {

	/**Valoarea propriuzisa*/
	private int value = 0;

	/** Constructor care nu face nimic.*/
	public IntHolder() {
	}

	/**
	 * Accesorii.
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * Accesorii.
	 */
	public int getValue() {
		return value;
	}
}