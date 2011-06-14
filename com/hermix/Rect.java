/**
 * Last update bu qurtach: 08.10.2001
 * Clasa care foloseste la trimiterea mesajelor pe whiteboard.
 * Figurile se incadreaza intr-un dreptunghi de coordonate specificate.
 */

package com.hermix;

import java.io.Serializable;

public class Rect implements Serializable{
	public int x1;
	public int y1;
	public int x2;
	public int y2;

	/**
	 * By qurtach:
	 * Construictor null. pentru compatibilitate cu versiulile mai vechi.
	 */
	public Rect() {
	}

	/**
	 * By qurtach:
	 * Constructor care face si initializarea campurilor clasei.
	 */
	public Rect(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}
}