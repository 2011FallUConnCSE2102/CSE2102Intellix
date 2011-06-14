
package ro.intellisoft.intelliX.UI;

/*
 * User: Administrator
 * @Author: qurtach@intellisoft.ro
 * @Date: Apr 24, 2002
 * @Time: 11:59:08 AM
 *
 * A modal dialog that will ask user for the prefered server adress, port and
 * other user peferences such as: nick, password, description, realname.
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ServerChooser extends JDialog implements ActionListener {
	private String serverNames[] = new String[]{"www.hermix.com"};
	private String serverPorts[] = new String[]{"10000"};

	private boolean isValidated = false;

	private JComboBox serverNameChooser = null;
	private JLabel serverNameLabel = new JLabel("Server:");
	private JComboBox serverPortChooser = null;
	private JLabel serverPortLabel = new JLabel("Port:");
	private JTextField nickField = new JTextField("guest");
	private JLabel nickLabel = new JLabel("Nick:");
	private JPasswordField passwordField = new JPasswordField("guest");
	private JLabel passwordLabel = new JLabel("Password:");
	private JTextField realNameField = new JTextField();
	private JLabel realNameLabel = new JLabel("Real name:");
	private JTextField descriptionField = new JTextField();
	private JLabel descriptionLabel = new JLabel("Description:");
	private JPanel buttonsPanel = new JPanel(new GridLayout(1, 4, 4, 0));
	private JPanel serverInfoPanel = new JPanel(new BorderLayout());
	private JPanel userInfoPanel = new JPanel(new BorderLayout());
	private MyButton extendedInformations = new MyButton("More >>");
	private MyButton okButton = new MyButton("Ok");
	private MyButton cancelButton = new MyButton("Cancel");
	private JPanel namePanel = new JPanel(new GridLayout(2, 1));
	private JPanel portPanel = new JPanel(new GridLayout(2, 1));
	private JPanel nickPanel = new JPanel(new GridLayout(2, 1));
	private JPanel passwordPanel = new JPanel(new GridLayout(2, 1));
	private JPanel nameAndDescriptionPanel = new JPanel(new GridLayout(4, 1));

	public ServerChooser(JFrame relativeTo, String servers[], String ports[], String userInfo[]) {
		super(relativeTo, "Connect to Hermix Server...", true);
		setLocationRelativeTo(relativeTo);
		if (servers != null) {
			serverNames = servers;
		}
		if (ports != null) {
			serverPorts = ports;
		}
		if (userInfo != null) {
			nickField.setText(userInfo[0]);
			passwordField.setText(userInfo[1]);
			descriptionField.setText(userInfo[2]);
			realNameField.setText(userInfo[3]);
		}
		serverNameChooser = new JComboBox(serverNames);
		serverPortChooser = new JComboBox(serverPorts);

		initGUI();
		this.setResizable(false);
	}

	private void initGUI() {
		this.getContentPane().setLayout(new BorderLayout(2, 2));
		this.getContentPane().add(serverInfoPanel, BorderLayout.NORTH);
		this.getContentPane().add(userInfoPanel, BorderLayout.CENTER);
		this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

		serverInfoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " Server's details "));
		namePanel.add(serverNameLabel);
		namePanel.add(serverNameChooser);
		serverNameChooser.setEditable(true);
		//serverNameChooser.setEditor(new MyComboBoxEditor());
		serverInfoPanel.add(namePanel, BorderLayout.CENTER);
		portPanel.add(serverPortLabel);
		portPanel.add(serverPortChooser);
		serverPortChooser.setEditable(true);
		//serverPortChooser.setEditor(new MyComboBoxEditor());
		serverInfoPanel.add(portPanel, BorderLayout.EAST);

		userInfoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), " User's details "));
		nickPanel.add(nickLabel);
		nickPanel.add(nickField);
		//nickField.setBorder(new RoundedBorder(17, -1));
		//nickField.setBackground(Color.lightGray);
		//nickField.setOpaque(false);
		nickField.addActionListener(this);
		nickField.setPreferredSize(new Dimension(nickField.getWidth(), nickField.getFontMetrics(nickField.getFont()).getHeight() + 7));
		userInfoPanel.add(nickPanel, BorderLayout.CENTER);
		passwordPanel.add(passwordLabel);
		passwordPanel.add(passwordField);
		//passwordField.setBorder(new RoundedBorder(17, -1));
		//passwordField.setBackground(Color.lightGray);
		//passwordField.setOpaque(false);
		passwordField.addActionListener(this);
		passwordField.setPreferredSize(new Dimension(passwordField.getWidth(), passwordField.getFontMetrics(passwordField.getFont()).getHeight() + 7));
		userInfoPanel.add(passwordPanel, BorderLayout.EAST);
		nameAndDescriptionPanel.add(realNameLabel);
		nameAndDescriptionPanel.add(realNameField);
		//realNameField.setBorder(new RoundedBorder(17, -1));
		//realNameField.setBackground(Color.lightGray);
		//realNameField.setOpaque(false);
		realNameField.addActionListener(this);
		realNameField.setPreferredSize(new Dimension(realNameField.getWidth(), realNameField.getFontMetrics(realNameField.getFont()).getHeight() + 7));
		nameAndDescriptionPanel.add(descriptionLabel);
		nameAndDescriptionPanel.add(descriptionField);
		//descriptionField.setBorder(new RoundedBorder(17, -1));
		//descriptionField.setBackground(Color.lightGray);
		//descriptionField.setOpaque(false);
		descriptionField.addActionListener(this);
		descriptionField.setPreferredSize(new Dimension(descriptionField.getWidth(), descriptionField.getFontMetrics(descriptionField.getFont()).getHeight() + 7));
		userInfoPanel.add(nameAndDescriptionPanel, BorderLayout.SOUTH);

		buttonsPanel.add(new JLabel());
		buttonsPanel.add(extendedInformations);
		extendedInformations.addActionListener(this);
		buttonsPanel.add(okButton);
		okButton.addActionListener(this);
		buttonsPanel.add(cancelButton);
		cancelButton.addActionListener(this);

		portPanel.setVisible(false);
		nameAndDescriptionPanel.setVisible(false);
		passwordPanel.setVisible(false);
		validate();
		int dx = (int) this.getPreferredSize().getWidth() + 8;
		int dy = (int) this.getPreferredSize().getHeight() + 27;
		this.setSize(dx, dy);
		validate();
	}

	/**Invoked when an action occurs.*/
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == extendedInformations) {
			boolean isExtended = extendedInformations.getText().equals("More >>");
			portPanel.setVisible(isExtended);
			nameAndDescriptionPanel.setVisible(isExtended);
			passwordPanel.setVisible(isExtended);
			extendedInformations.setText(isExtended?"Less <<":"More >>");
			this.setSize(this.getPreferredSize());
			validate();
		} else if (e.getSource() == cancelButton) {
			this.hide();
		} else {
			//<ENTER> or ok button I suppose
			this.hide();
			isValidated = true;
		}
	}

	/**returns true if the user has pressed Ok Button*/
	public boolean isValid() {
		return isValidated;
	}

	public String getNick() {
		return nickField.getText();
	}

	public String getDescription() {
		return descriptionField.getText();
	}

	public String getPassword() {
		return passwordField.getPassword().toString();
	}

	public String getRealName() {
		return realNameField.getText();
	}

	public String getServerName() {
		return serverNameChooser.getSelectedItem().toString();
	}

	public int getServerPort() {
		return Integer.parseInt(serverPortChooser.getSelectedItem().toString());
	}
}//end class ro.intellisoft.intelliX.ServerChooser


