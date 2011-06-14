
package ro.intellisoft.intelliX.UI;

import ro.intellisoft.intelliX.IntelliX;
import ro.intellisoft.intelliX.User;

import javax.swing.*;
import java.awt.*;

/*
 * User: Qurtach
 * Date: Apr 3, 2002
 * Time: 2:50:32 PM
 */

public class UserLabel extends JLabel implements ListCellRenderer {
	IntelliX parent = null;

	public UserLabel(IntelliX parent) {
		this.parent = parent;
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		/*return new DefaultListCellRenderer().getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);*/
		this.setText(value.toString());
		this.setOpaque(true);
		if (value.equals(parent.getHermixLink().getNick())) {
			this.setForeground(Color.blue);
		} else {
			this.setForeground(Color.black);
		}
		if (isSelected) {
			this.setBackground(new Color(206, 206, 255));//Swing section color
		} else {
			this.setBackground(Color.white);
		}
		User u = parent.getHermixLink().getUserByName(value.toString());
		int status = u==null ? -1 : u.getUserStatus();
		setIcon(User.getIconForState(this, status));
		return this;
	}
}
