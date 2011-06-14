/**
 * User: Administrator
 * @Author: qurtach@intellisoft.ro
 * @Date: May 20, 2002
 * @Time: 10:42:09 AM
 */

package ro.intellisoft.intelliX.UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MyButton extends JButton implements MouseListener {

	public MyButton() {
		this("");
	}

	public MyButton(String label) {
		super(label);
		setBorder(new RoundedBorder(17, -1));
		setCursor(new Cursor(Cursor.HAND_CURSOR));
		setContentAreaFilled(false);
		setFocusPainted(false);
		addMouseListener(this);
		setPreferredSize(new Dimension(super.getFontMetrics(getFont()).stringWidth(getText()) + 30, super.getFontMetrics(getFont()).getHeight() + 7));
	}

	/**Invoked when the mouse enters a component.*/
	public void mouseEntered(MouseEvent e) {
		this.setForeground(Color.blue);
		this.setBackground(Color.white);
	}

	/**Invoked when the mouse exits a component.*/
	public void mouseExited(MouseEvent e) {
		this.setForeground(Color.black);
		this.setBackground(Color.lightGray);
	}

	/**Invoked when a mouse button has been pressed on a component.*/
	public void mousePressed(MouseEvent e) {
		this.setForeground(Color.red);
		this.setBackground(Color.gray);
	}

	/**Invoked when a mouse button has been released on a component.*/
	public void mouseReleased(MouseEvent e) {
		this.setForeground(Color.black);
	}

	/**Invoked when the mouse button has been clicked (pressed and released) on a component.*/
	public void mouseClicked(MouseEvent e) {
	}
}
