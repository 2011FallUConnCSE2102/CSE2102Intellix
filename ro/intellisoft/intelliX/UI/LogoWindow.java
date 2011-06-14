/**
 * User: Administrator
 * @Author: qurtach@intellisoft.ro
 * @Date: May 15, 2002
 * @Time: 3:59:01 PM
 */

package ro.intellisoft.intelliX.UI;

import ro.intellisoft.intelliX.IntelliX;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogoWindow extends JWindow implements ActionListener {
	private IntelliX IDE;
	private Timer timer = new Timer(4*1000, this);

	public LogoWindow(IntelliX IDE) {
		this.IDE = IDE;
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((d.width - 400) / 2, (d.height - 300) / 2, 400, 300);
		show();
		timer.setRepeats(false);
		timer.start();
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) {
		g.drawImage(IDE.loadImageResource(this, "images/logo.gif").getImage(), 0, 0, this);
		g.drawString(IDE.getVersion(), 5, 15);
		g.drawString(IDE.getCopyright(), 5+66, 295);
	}

	/**Invoked when timer action occurs.*/
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==timer){
			hide();
			timer.stop();
			timer.removeActionListener(this);
		}
	}

	public void hide() {
		super.hide();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				IDE.restorePosition();
			}
		});
	}
}
