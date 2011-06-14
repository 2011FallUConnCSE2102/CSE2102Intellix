
package ro.intellisoft.whiteboard;

/**
 * Title:        BlinkCursor
 * Description:  Emuleaza un cursor care palpae.
 * Copyright:    Copyright (c) 2001
 * Company:      Intellisoft
 * @author Ovidiu Maxiniuc
 * @version 2.0
 */


public class BlinkCursor extends Thread {

	/**
	 * Whiteboard-ul pe care vom desena cursorul.
	 */
	private Whiteboard wb = null;

	/**
	 * Flag care imi indica daca mai am nevoie de cursor sa palpaie...
	 */
	private boolean necessary = true;

	/**
	 * Constructor:
	 * @param x coordonata x la care palpaie cursorul
	 * @param y coordonata y la care palpaie cursorul
	 * @param h inaltimea cursorului
	 */
	public BlinkCursor(Whiteboard wb) {
		this.wb = wb;

	}

	public void stopBlink() {
		necessary = false;
	}

	/**
	 * Metoda ce se lanseaza pe alt thread.
	 */
	public void run() {
		do {
			try {
				sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
			wb.alternateCursor();
		} while (necessary);//endless
	}
}