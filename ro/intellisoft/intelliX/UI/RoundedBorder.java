/**
 * User: Administrator
 * @Author: qurtach@intellisoft.ro
 * @Date: May 20, 2002
 * @Time: 10:42:55 AM
 */

package ro.intellisoft.intelliX.UI;

import javax.swing.border.AbstractBorder;
import java.awt.*;

public class RoundedBorder extends AbstractBorder {
	private int rx, ry;
	private Color backgroundColor = null;

	public RoundedBorder(int rx, int ry) {
		this.rx = rx;
		this.ry = ry;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		int rw = rx;
		int rh = ry;
		if (rw < 0) {
			rw = (width * 4) / 5;
		}
		if (rh < 0) {
			rh = (height * 4) / 5;
		}
		Graphics2D g2 = (Graphics2D) g;
		g2.setXORMode(Color.white);
		g2.setPaint(c.getBackground());
		g2.fillRoundRect(x, y, width - 1, height - 1, rw, rh);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaintMode();
		g2.setPaint(Color.white);
		g2.drawRoundRect(x + 1, y + 1, width - 2, height - 2, rw, rh);
		g2.setPaint(Color.black);
		g2.drawRoundRect(x, y, width - 2, height - 2, rw, rh);
	}
}

