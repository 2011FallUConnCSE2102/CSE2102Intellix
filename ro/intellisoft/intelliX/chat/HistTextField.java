
package ro.intellisoft.intelliX.chat;

/**
 * Title:        Unified Hermix Applet
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Intellisoft SRL
 * @author
 * @version 1.0
 */

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

public class HistTextField extends TextField implements KeyListener {

	private Vector history = new Vector();
	private int index = 0;

	public HistTextField() {
		this.addKeyListener(this);
	}

	public void addToHistory(String text) {
		text = text.trim();
		if (history.contains(text))
			return;
		history.addElement(text);
		index = history.size();
		if (history.size() >= 11) {
			history.removeElementAt(0);
			index--;
		}
	}

	public String getHistory() {
		String hist = "";
		if (!history.isEmpty()) {
			if (index <= 1)
				index = 1;
			else if (index > history.size()) {
				index = history.size();
				return "";
			}
			hist = (String) history.elementAt(index - 1);
		}
		return hist;
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			this.setText(getHistory());
			index--;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			index++;
			this.setText(getHistory());
		} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			this.setText("");
			index = history.size();
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			if (!this.getText().equals("")) {
				this.addToHistory(this.getText());
				index = history.size();
			}
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}
}
