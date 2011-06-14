/**
 * User: Administrator
 * @Author: qurtach@intellisoft.ro
 * @Date: Jul 2, 2002
 * @Time: 1:22:18 PM
 */

package ro.intellisoft.intelliX.UI;

import ro.intellisoft.intelliX.IntelliX;

import javax.swing.*;
import java.awt.*;

/**An unclosing dialog with a progress bar showing the files taht are loading*/
public class OpenFilesDialog  extends JDialog{
	private IntelliX IDE;
	private boolean ended = false;
	private JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
	private JLabel fileLabel = new JLabel("- some file -");

	public OpenFilesDialog(IntelliX IDE) throws HeadlessException {
		super(IDE.getMainFrame(), "Action in progress...", true);
		this.IDE = IDE;
		//user cannot close this:
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setLocation(400, 300);
		this.setUndecorated(true);
		JPanel contentPane = new JPanel(new BorderLayout(10, 10));
		contentPane.setBorder(BorderFactory.createRaisedBevelBorder());
		JLabel aLabel = new JLabel();
		contentPane.add(aLabel, BorderLayout.NORTH);
		aLabel = new JLabel();
		contentPane.add(aLabel, BorderLayout.SOUTH);
		aLabel = new JLabel();
		contentPane.add(aLabel, BorderLayout.EAST);
		aLabel = new JLabel();
		contentPane.add(aLabel, BorderLayout.WEST);
        this.setContentPane(contentPane);
		contentPane = new JPanel(new BorderLayout(10, 10));
		this.getContentPane().add(contentPane, BorderLayout.CENTER);
		JTextArea textArea = new JTextArea("The files left open when you've close IntelliX\nare now loading.\nPlease Wait...");
		textArea.setOpaque(false);
		textArea.setBorder(BorderFactory.createEmptyBorder());
		textArea.setEditable(false);
		textArea.setFocusable(false);
		textArea.setFont(fileLabel.getFont());
		contentPane.add(textArea, BorderLayout.CENTER);
		JPanel infoPanel = new JPanel(new BorderLayout(10, 10));
		contentPane.add(infoPanel, BorderLayout.SOUTH);
		infoPanel.add(progressBar, BorderLayout.NORTH);
		progressBar.setIndeterminate(true);
		infoPanel.add(new JLabel("Crt. file: "), BorderLayout.WEST);
		infoPanel.add(fileLabel, BorderLayout.CENTER);
		this.pack();
	}

	public boolean isEnded() {
		return ended;
	}

	public void end() {
		this.ended = true;
		this.setVisible(false);
		this.dispose();
	}

	public void setFile(String fileName){
		if (fileName.length()>30){
			fileName = fileName.substring(fileName.length()-30);
		}
		final String truncated = "..."+fileName;
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				fileLabel.setText(truncated);
			}
		});
	}
}
