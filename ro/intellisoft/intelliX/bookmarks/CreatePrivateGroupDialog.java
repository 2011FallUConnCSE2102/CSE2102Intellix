/**
 * User: Administrator
 * @Author: qurtach@intellisoft.ro
 * @Date: Jun 13, 2002
 * @Time: 1:15:27 PM
 */

package ro.intellisoft.intelliX.bookmarks;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.Vector;

public class CreatePrivateGroupDialog extends JDialog implements ActionListener {

	//private TextShareProcessor parent;
	private JButton jbAddUser = new JButton("Add User");
	private JButton jbRemoveUser = new JButton("Remove User");
	private JButton jbOkButton = new JButton("Ok");
	private JButton jbCancelButton = new JButton("Cancel");
	private boolean cancelPresed = true;

	/**panelul cu utilizatori selectati*/
	private DefaultListModel listModel = new DefaultListModel();
	private JList usersList = new JList(listModel);
	private JScrollPane scroller = new JScrollPane(usersList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	private Vector users = null;

	public CreatePrivateGroupDialog(JFrame relativeTo, Vector usersAlreadySelected) {
		super(relativeTo, "Create private text share...", true);
		this.setLocationRelativeTo(relativeTo);
		this.users = usersAlreadySelected;
		build();
	}

	/**Metoda care construieste interfata grafica*/
	private void build() {
		this.setResizable(false);
		GridBagLayout gridBag = new GridBagLayout();
		this.getContentPane().setLayout(gridBag);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.0;
		c.weighty = 0.0;

		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridwidth = 2;
		JLabel aLabel = new JLabel("User list:");
		gridBag.setConstraints(aLabel, c);
		this.getContentPane().add(aLabel);

		c.gridwidth = GridBagConstraints.REMAINDER;
		aLabel = new JLabel();
		gridBag.setConstraints(aLabel, c);
		this.getContentPane().add(aLabel);

		for (int i = 0; i < users.size(); i++)
			listModel.addElement(users.elementAt(i));
		c.weighty = 10.0;
		c.weightx = 10.0;
		c.gridheight = 5;
		c.gridwidth = 2;
		scroller.setMinimumSize(new Dimension(125, 125));
		scroller.setPreferredSize(new Dimension(125, 125));
		usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		gridBag.setConstraints(scroller, c);
		this.getContentPane().add(scroller);

		c.weighty = 0.0;
		c.weightx = 0.0;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		jbAddUser.addActionListener(this);
		gridBag.setConstraints(jbAddUser, c);
		this.getContentPane().add(jbAddUser);

		jbRemoveUser.addActionListener(this);
		gridBag.setConstraints(jbRemoveUser, c);
		this.getContentPane().add(jbRemoveUser);

		c.weighty = 1.0;
		aLabel = new JLabel();
		gridBag.setConstraints(aLabel, c);
		this.getContentPane().add(aLabel);
		c.weighty = 0.0;

		jbCancelButton.addActionListener(this);
		gridBag.setConstraints(jbCancelButton, c);
		this.getContentPane().add(jbCancelButton);

		jbOkButton.addActionListener(this);
		gridBag.setConstraints(jbOkButton, c);
		this.getContentPane().add(jbOkButton);

		this.pack();
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == jbOkButton) {
			//parent.makePrivateGoup(jtfGroupName.getText(), users);
			cancelPresed = false;
			this.hide();
		} else if (ae.getSource() == jbCancelButton) {
			//parent.makePrivateGoup("", users);
			this.hide();
		} else if (ae.getSource() == jbRemoveUser) {
			listModel.remove(usersList.getSelectedIndex());
			users.remove(usersList.getSelectedValue());
		} else if (ae.getSource() == jbAddUser) {
			String newUserName = JOptionPane.showInputDialog(this, "New user name please:", "Insert user in group", JOptionPane.QUESTION_MESSAGE);
			String errMsg = ro.intellisoft.intelliX.chat.ChatUtils.isValidName(newUserName);
			if (errMsg == null) {
				listModel.addElement(newUserName);
				users.addElement(newUserName);
			} else
				JOptionPane.showMessageDialog(this, errMsg, "Bad User Name!", JOptionPane.ERROR_MESSAGE);
		}
	}

	public boolean isCancelPresed() {
		return cancelPresed;
	}

	public Vector getSelectedUsers() {
		return users;
	}
}
