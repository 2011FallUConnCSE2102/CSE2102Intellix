
package ro.intellisoft.whiteboard;

/**
 * Title:        Thread Launcher...
 * Description:  Thread care se porneste la cerere si face lucruri de background...
 * Copyright:    Copyright (c) 2001
 * Company:      Intellisoft SRL
 * @author Intellisoft
 * @version 1.0
 */

import com.hermix.HermixApi;
import ro.intellisoft.whiteboard.shapes.Figure;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.util.Vector;


public class Launcher extends Thread {

	private int status = 0;

	private Whiteboard wb = null;
	private Figure f = null;
	private HermixApi sender = null;
	private String group = null;
	private Vector shapes = null;
	private Color backgroundColor = null;

	public Launcher(Whiteboard wb) {
		this.wb = wb;
		this.status = 1;
	}

	public Launcher(Figure f, HermixApi sender, String group) {
		this.f = f;
		this.sender = sender;
		this.group = group;
		this.status = 2;
	}

	public Launcher(Vector shapes, HermixApi sender, String group, Color backgroundColor) {
		this.sender = sender;
		this.group = group;
		this.shapes = shapes;
		this.backgroundColor = backgroundColor;
		this.status = 3;
	}

	/**
	 * Metoda care se lamseaza pe alt thread. <BR>
	 * In functie de starea in care a fost construit, face altceva...
	 */
	public void run() {
		if (status == 1) {
			//initializam job-ul
			PrinterJob printJob = PrinterJob.getPrinterJob();
			//dialog pentru alegerea formatului de pagina
			PageFormat pageFormat = new PageFormat();
			Paper paper = new Paper();
			paper.setSize(72 * 8.27, 72 * 11.69); //A4
			paper.setImageableArea(72 / 2.45, 72 / 2.45, 72 * 8.27 - 2 * 72 / 2.45, 72 * 11.69 - 2 * 72 / 2.45);//A4 cu marginea de 1 cm
			pageFormat.setPaper(paper);
			pageFormat = printJob.pageDialog(pageFormat);
			printJob.setPrintable(wb, pageFormat);
			if (printJob.printDialog()) {
				try {
					//printJob.validatePage(printJob.defaultPage());????
					printJob.print();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} else if (status == 2) {
			try {
				f.send(sender, group);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (status == 3) {
			new ro.intellisoft.whiteboard.shapes.Background(backgroundColor).send(sender, group);
			for (int i = 0; i < shapes.size(); i++)
				try {
					((Figure) shapes.elementAt(i)).send(sender, group);
				} catch (Exception e) {
				}
			//To Do Here: de trimis si imaginea de pe bg.
		}
	}
}