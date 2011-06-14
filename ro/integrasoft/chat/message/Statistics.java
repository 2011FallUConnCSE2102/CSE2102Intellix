
package ro.integrasoft.chat.message;

/**
 * Title:        Statistic Module
 * Description:  Modul care afiseaza informatii statistice referitoare
 *                  la traficul pe retea...
 * Copyright:    Copyright (c) 2001
 * Company:      Intellisoft SRL
 * @author Maxiniuc Ovidiu
 * @version 2.0
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Statistics extends Thread implements ActionListener {

	/**
	 * Nr de secunde pentru istoria care se afiseaza...
	 */
	public static final int HISTORY = 30;

	/**
	 * Tablou cu dimensiunile memoriei alocate/ocupate
	 */
	private int[][] memory = new int[2][HISTORY];

	/**
	 * Tablou cu vitezele atinse in ultimele HISTORY sec la trimitere...
	 */
	private double[][] upSpeed = new double[9][HISTORY];

	/**
	 * Tablou cu vitezele atinse in ultimele 10 sec la primire...
	 */
	private double[][] downSpeed = new double[9][HISTORY];

	/**
	 * Tablou cu informatiile primite in ultima secunda...
	 */
	private int[] lastSecondDownload = new int[9];

	/**
	 * Tablou cu informatiile trimise in ultima secunda...
	 */
	private int[] lastSecondUpload = new int[9];

	/**
	 * Tablou cu valorile de Upload.
	 */
	private double[] upload = new double[9];

	/**
	 * Tablou cu valorile de Download
	 */
	private double[] download = new double[9];

	/**
	 * Fereastra de dialog pe care se afiseaza statistica
	 */
	private JDialog frame = null;

	/**
	 * Constanta simbolica ce desemneaza locatia de Upload System
	 * @see Statistics#add(int, int)
	 */
	public static final int UP_SYSTEM = 0x00;

	/**
	 * Constanta simbolica ce desemneaza locatia de Upload Chat
	 * @see Statistics#add(int, int)
	 */
	public static final int UP_CHAT = 0x01;

	/**
	 * Constanta simbolica ce desemneaza locatia de Upload Whiteboard
	 * @see Statistics#add(int, int)
	 */
	public static final int UP_WHITEBOARD = 0x02;

	/**
	 * Constanta simbolica ce desemneaza locatia de Upload File
	 * @see Statistics#add(int, int)
	 */
	public static final int UP_FILE = 0x03;

	/**
	 * Constanta simbolica ce desemneaza locatia de Upload Audio
	 * @see Statistics#add(int, int)
	 */
	public static final int UP_AUDIO = 0x04;

	/**
	 * Constanta simbolica ce desemneaza locatia de Upload video
	 * @see Statistics#add(int, int)
	 */
	public static final int UP_VIDEO = 0x05;

	/**
	 * Constanta simbolica ce desemneaza locatia de Upload Total
	 * @see Statistics#add(int, int)
	 */
	public static final int UP_TOTAL = 0x06;

	/**
	 * Constanta simbolica ce desemneaza locatia de Download System
	 * @see Statistics#add(int, int)
	 */
	public static final int DOWN_SYSTEM = 0x10;

	/**
	 * Constanta simbolica ce desemneaza locatia de Download Chat
	 * @see Statistics#add(int, int)
	 */
	public static final int DOWN_CHAT = 0x11;

	/**
	 * Constanta simbolica ce desemneaza locatia de Download Whiteboard
	 * @see Statistics#add(int, int)
	 */
	public static final int DOWN_WHITEBOARD = 0x12;

	/**
	 * Constanta simbolica ce desemneaza locatia de Download File
	 * @see Statistics#add(int, int)
	 */
	public static final int DOWN_FILE = 0x13;

	/**
	 * Constanta simbolica ce desemneaza locatia de Download Audio
	 * @see Statistics#add(int, int)
	 */
	public static final int DOWN_AUDIO = 0x14;

	/**
	 * Constanta simbolica ce desemneaza locatia de Download video
	 * @see Statistics#add(int, int)
	 */
	public static final int DOWN_VIDEO = 0x15;

	/**
	 * Constanta simbolica ce desemneaza locatia de Download Total
	 * @see Statistics#add(int, int)
	 */
	public static final int DOWN_TOTAL = 0x16;


	/**
	 * Forma in care bagam celelalte panele cu informatii.
	 */
	private JTabbedPane jTabbedPane = new JTabbedPane();

	/**
	 * Panelul cu informatii de upload
	 */
	private StatisticPane uploadPane = new StatisticPane(upload, upSpeed);

	/**
	 * Panelul cu informatii de download
	 */
	private StatisticPane downloadPane = new StatisticPane(download, downSpeed);

	/**
	 * Panelul cu informatii despre memorie
	 */
	private MemoryPane memoryPane = new MemoryPane(memory);

	/**
	 * Constructor. <br>
	 * Deoarece avem de afisat un dialog modal trebuie sa stim numele parintelui.
	 */
	public Statistics() {
		frame = new JDialog();
		frame.setTitle("Statistics ...");
		frame.setModal(false);
		frame.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		frame.setResizable(false);
		frame.setBounds(220, 280, 420, 230);
		frame.getContentPane().setLayout(null);

		JButton OKButton = new JButton("Ok");
		OKButton.setBounds(329, 176, 80, 22);
		frame.getContentPane().add(OKButton);
		OKButton.addActionListener(this);

		JButton ResetButton = new JButton("Reset");
		ResetButton.setBounds(240, 176, 80, 22);
		frame.getContentPane().add(ResetButton);
		ResetButton.addActionListener(this);


		frame.getContentPane().add(jTabbedPane);
		jTabbedPane.setBounds(2, 0, 410, 167);
		jTabbedPane.addTab("Upload", null, uploadPane);
		jTabbedPane.addTab("Download", null, downloadPane);
		jTabbedPane.addTab("Memory", null, memoryPane);
		//jTabbedPane.addTab("Individuals", null, new JPanel());
	}

	public void run() {
		for (; ;) {
			try {
				this.sleep(1000);
			} catch (Exception ex) {
			}
			//translatam informatiile referitoare la vitezele pe fiecare secunda:
			for (int i = HISTORY - 1; i > 0; i--) {
				for (int j = UP_SYSTEM; j <= UP_TOTAL; j++) {
					downSpeed[j][i] = downSpeed[j][i - 1];
					upSpeed[j][i] = upSpeed[j][i - 1];
				}
				memory[0][i] = memory[0][i - 1];
				memory[1][i] = memory[1][i - 1];
			}
			//calculam viteza in ultima secunda:
			for (int i = 0; i <= 6; i++) {
				downSpeed[i][0] = lastSecondDownload[i] / 1024.0;
				upSpeed[i][0] = lastSecondUpload[i] / 1024.0;
				lastSecondDownload[i] = 0;
				lastSecondUpload[i] = 0;
			}
			//memoram informatiile despre memorie din ultima secunda:
			memory[0][0] = (int) (Runtime.getRuntime().totalMemory() / 1024); //in KB
			memory[1][0] = (int) ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024); //in KB
			frame.repaint();
		}
	}

	public void add(int amount, int where) {
		if (where >= UP_SYSTEM && where <= UP_VIDEO) {
			upload[where] += amount;
			//adaug la rublica respectiva
			lastSecondUpload[where] += amount;
			//si la total
			upload[UP_TOTAL] += amount;
			lastSecondUpload[UP_TOTAL] += amount;
		} else if (where >= DOWN_SYSTEM && where <= DOWN_VIDEO) {
			download[where - 0x10] += amount;
			//adaug la rublica respectiva
			lastSecondDownload[where - 0x10] += amount;
			//si la total
			download[UP_TOTAL] += amount;
			lastSecondDownload[UP_TOTAL] += amount;
		} else
			System.out.println("Statistik error: trying to update " + amount + " bytes of unknown " + where);
	}

	public void show() {
		frame.show();
	}

	private void reset() {
		for (int i = UP_SYSTEM; i <= UP_TOTAL; i++) {
			lastSecondUpload[i] = lastSecondDownload[i] = 0;
			download[i] = upload[i] = 0.0;
		}
		frame.repaint();
	}

	public void actionPerformed(ActionEvent ae) {
		if (((JButton) ae.getSource()).getText().equals("Ok")) {
			frame.hide();
		} else if (((JButton) ae.getSource()).getText().equals("Reset")) {
			reset();
			frame.repaint();
		}
	}

	private static final String doubleToString(double value) {
		double rounded = ((int) (value * 100.00)) / 100.0;
		return rounded + "";
	}

}


class StatisticPane extends JPanel implements ActionListener {
	//aici nu se fac calcule, dar avem nevoie de informatii pentru a le afisa..
	double[][] speed = null;
	double[] amount = null;

	JLabel[] amountLabels = new JLabel[7];
	JCheckBox[] sensorEnablers = new JCheckBox[7];

	private static final Color[] colors = {new Color(204, 0, 0), new Color(255, 51, 204), new Color(0, 0, 153), new Color(0, 153, 0), new Color(128, 128, 128), new Color(255, 153, 0), new Color(0, 0, 0)};

	private JLabel addLabel(String label, int x, int y, int w, int h, Color c) {
		JLabel aLabel = new JLabel(label);
		aLabel.setBounds(x, y, w, h);
		aLabel.setForeground(c);
		this.add(aLabel);
		return aLabel;
	}

	private JCheckBox addCheckBox(String label, int x, int y, int w, int h, Color c) {
		JCheckBox aJCheckBox = new JCheckBox(label, true);
		aJCheckBox.setBounds(x, y, w, h);
		aJCheckBox.setForeground(c);
		aJCheckBox.setOpaque(false);
		aJCheckBox.addActionListener(this);
		this.add(aJCheckBox);
		return aJCheckBox;
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() instanceof JCheckBox)
			this.repaint();
	}

	public StatisticPane(double[] amount, double[][] speed) {
		this.amount = amount;
		this.speed = speed;
		this.setLayout(null);
		sensorEnablers[0] = addCheckBox("System", 3, 5, 70, 20, colors[0]);
		sensorEnablers[1] = addCheckBox("Chat", 3, 22, 70, 20, colors[1]);
		sensorEnablers[2] = addCheckBox("Wboard", 3, 39, 70, 20, colors[2]);
		sensorEnablers[3] = addCheckBox("File", 3, 56, 70, 20, colors[3]);
		sensorEnablers[4] = addCheckBox("Audio", 3, 73, 70, 20, colors[4]);
		sensorEnablers[5] = addCheckBox("Video", 3, 90, 70, 20, colors[5]);
		sensorEnablers[6] = addCheckBox("Total", 3, 115, 70, 20, colors[6]);

		for (int i = 0; i < 6; i++) {
			amountLabels[i] = addLabel(to2fixed(0) + " kB", 50, 5 + i * 17, 70, 20, colors[i]);
			amountLabels[i].setHorizontalAlignment(JLabel.RIGHT);
		}
		amountLabels[6] = addLabel(to2fixed(0) + " kB", 50, 116, 70, 20, colors[6]);
		amountLabels[6].setHorizontalAlignment(JLabel.RIGHT);
	}

	public void paint(Graphics g) {
		super.paint(g); //ca sa desenam labelele
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(Color.white);
		g2.fillRect(128, 3, 275, 132);
		g2.setPaint(Color.gray);
		g2.drawRect(127, 2, 277, 134);

		//calculam mmaximum
		double maximum = 1.0;
		for (int i = 0; i <= 6; i++)
			for (int j = 0; j < Statistics.HISTORY; j++)
				if (speed[i][j] > maximum)
					maximum = (int) speed[i][j] + 1;
		//punem scara din 5 in 5:
		if (maximum != 1)
			maximum += 5 - (maximum % 5);
		//si facem scalarea in functie de acesta:
		double sUnit = 105.0 / maximum;
		int tUnit = 250 / Statistics.HISTORY;

		g2.setPaint(Color.black);
		g2.drawString("kBps", 152, 13);
		if (maximum > 9)
			g2.drawString((int) maximum + "", 132, 17);
		else
			g2.drawString((int) maximum + "", 138, 17);
		double middle = maximum / 2.0;
		if (middle > 8) {
			if (middle % 5 != 0)
				middle = (int) (middle + 5 - (middle % 5));
			g2.drawString((int) middle + "", 132, (int) (124 - 105 * middle / maximum));
		} else {
			g2.drawString(middle + "", 128, (int) (124 - 105 * middle / maximum));
		}

		g2.drawString(Statistics.HISTORY + "", 382, 132);
		g2.drawString("0", 139, 132);
		g2.drawString("sec", 382, 116);
		g2.drawString(Statistics.HISTORY + "", 382, 132);

		float dash[] = {10.0f};
		g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 20.0f));
		g2.drawLine(145, (int) (119 - 105 * middle / maximum), 390, (int) (119 - 105 * middle / maximum));
		g2.drawLine(145, 14, 390, 14);

		g2.setStroke(new BasicStroke(1.5f));
		g2.setFont(new Font("Arial", Font.BOLD, 12));
		g2.drawLine(148, 5, 148, 120);
		g2.drawLine(148, 120, 400, 120);
		g2.drawLine(3, 114, 120, 114);

		for (int i = 0; i <= 6; i++) {
			int translate = 0; //daca este totalul il translatam 'in sus' cu
			//doi pixele ca sa pot deosebi pe total de celalalt daca am numai un senzor
			translate = i == 6 ? -1 : 1;

			//daca numarul e prea mare il transformam in MB!:
			if (amount[i] / 1024 < 1000)
				amountLabels[i].setText(to2fixed(amount[i] / 1024) + " kB");
			else
				amountLabels[i].setText(to2fixed(amount[i] / 1024 / 1024) + " MB");
			//desenam curba numai daca senzorul este activat!-
			if (!sensorEnablers[i].isSelected())
				continue;//luam urmatorul senzor!
			g2.setPaint(colors[i]);
			for (int j = 1; j < Statistics.HISTORY; j++)
				g2.drawLine(149 + tUnit * (j - 1), (int) (119 - sUnit * speed[i][j - 1]) + translate, 149 + tUnit * j, (int) (119 - sUnit * speed[i][j]) + translate);
		}
	}

	static String to2fixed(double x) {
		x = ((int) (x * 100)) / 100.0;
		String toReturn = x + "";
		if (toReturn.indexOf('.') == -1)
			return toReturn + ".00";
		else if (toReturn.indexOf('.') == toReturn.length() - 2)
			return toReturn + "0";
		else
			return toReturn;
	}
}

class MemoryPane extends JPanel {
	//aici nu se fac calcule, dar avem nevoie de informatii pentru a le afisa..
	int[][] amount = null;
	JLabel amountLabelAllocated = null;
	JLabel amountLabelUsed = null;
	JLabel procentUsed = null;

	public MemoryPane(int[][] amount) {
		this.amount = amount;
		this.setLayout(null);
		this.setLayout(null);
		addLabel("Allocated", 3, 26, 70, 20, Color.blue);
		addLabel("Used", 3, 60, 70, 20, Color.magenta);
		procentUsed = addLabel("70,00% used", 3, 110, 120, 20, Color.black);
		procentUsed.setHorizontalAlignment(JLabel.CENTER);
		amountLabelAllocated = addLabel("0 kB", 50, 26, 70, 20, Color.blue);
		amountLabelAllocated.setHorizontalAlignment(JLabel.RIGHT);
		amountLabelUsed = addLabel("0 kB", 50, 60, 70, 20, Color.magenta);
		amountLabelUsed.setHorizontalAlignment(JLabel.RIGHT);
	}

	private JLabel addLabel(String label, int x, int y, int w, int h, Color c) {
		JLabel aLabel = new JLabel(label);
		aLabel.setBounds(x, y, w, h);
		aLabel.setForeground(c);
		this.add(aLabel);
		return aLabel;
	}


	public void paint(Graphics g) {
		super.paint(g); //ca sa desenam labelele
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setPaint(Color.white);
		g2.fillRect(128, 3, 275, 132);
		g2.setPaint(Color.gray);
		g2.drawRect(127, 2, 277, 134);

		//calculam mmaximum
		int maximum = 0; //in KB!
		for (int j = 0; j < Statistics.HISTORY; j++)
			if (amount[0][j] > maximum)
				maximum = amount[0][j] + 1024;//un MB in plus!
		//punem scara din 5 in 5 MB
		if (maximum != 1024)
			maximum += 5120 - (maximum % 5120);
		//si facem scalarea in functie de acesta:
		double sUnit = 105.0 / maximum;
		int tUnit = 250 / Statistics.HISTORY;

		g2.setPaint(Color.black);
		g2.drawString("MB", 152, 13);
		if (maximum / 1024 > 9)
			g2.drawString((int) maximum / 1024 + "", 132, 17);
		else
			g2.drawString((int) maximum / 1024 + "", 138, 17);

		g2.drawString(Statistics.HISTORY + "", 382, 132);
		g2.drawString("0", 139, 132);
		g2.drawString("sec", 382, 116);

		float dash[] = {10.0f};
		g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 20.0f));
		g2.drawLine(145, 14, 390, 14);

		//desenam systemul de coordonate:
		g2.setStroke(new BasicStroke(1.5f));
		g2.setFont(new Font("Arial", Font.BOLD, 12));
		g2.drawLine(148, 5, 148, 120);
		g2.drawLine(148, 120, 400, 120);
		//tragem linia de sumarizare
		g2.drawLine(3, 95, 120, 95);

		g2.setPaint(Color.blue);
		for (int j = 1; j < Statistics.HISTORY; j++)
			g2.drawLine(149 + tUnit * (j - 1), (int) (119 - sUnit * amount[0][j - 1]), 149 + tUnit * j, (int) (119 - sUnit * amount[0][j]));
		amountLabelAllocated.setText(StatisticPane.to2fixed(amount[0][0] / 1024.0) + " MB");

		g2.setPaint(Color.magenta);
		for (int j = 1; j < Statistics.HISTORY; j++)
			g2.drawLine(149 + tUnit * (j - 1), (int) (119 - sUnit * amount[1][j - 1]), 149 + tUnit * j, (int) (119 - sUnit * amount[1][j]));
		amountLabelUsed.setText(StatisticPane.to2fixed(amount[1][0] / 1024.0) + " MB");
		procentUsed.setText(StatisticPane.to2fixed(amount[1][0] * 100.0 / amount[0][0]) + "% used");
	}
}