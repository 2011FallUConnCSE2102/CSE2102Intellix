
package ro.intellisoft.whiteboard;

/**
 * Title:        Clasa care stie sa salveze continutul wb-ului pe un alt thread.
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Intellisoft
 * @author Ovidiu Maxiniuc
 * @version 2.0
 */

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;


public class SVGSaver extends Thread {
	/**
	 * Whiteboard-ul pe care il salvez:
	 */
	private Whiteboard wb = null;

	/**
	 * PrintWriter'ul cu ajutorul caruia salvez.
	 */
	PrintWriter outFile = null;

	/**
	 * numele fisierului in care se face salvarea.
	 */
	private String filename = null;

	/**
	 * Constructorul care ia ca argumente whiteboard-ul ce va fi salvat.
	 * Stie singur sa deskida fereastra de dialog si sa selecteze numele
	 * si tipul fisierului.
	 * @param wb whiteboard-ul care se salveaza.
	 */
	public SVGSaver(Whiteboard wb) throws Exception {
		this.wb = wb;
		final JFileChooser fc = new JFileChooser();
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		fc.setDialogTitle("Choose the destination filename...");
		//construim filtrul de rigoare
		fc.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
			public boolean accept(java.io.File f) {
				if (f.isDirectory())
					return true;
				return f.getName().toLowerCase().endsWith(".svg");
			}

			public String getDescription() {
				return "SVG files (*.SVG)";
			}
		});
		if (fc.showSaveDialog(wb.getParent()) == JFileChooser.APPROVE_OPTION) {
			filename = fc.getSelectedFile().getAbsolutePath();
			if (!filename.toLowerCase().endsWith(".svg"))
				filename = filename + ".SVG";
			outFile = new PrintWriter(new BufferedWriter(new FileWriter(
					filename)));
		} else
			throw new Exception("User abort detected...");
	}

	/**
	 * Metoda care se lanseaza pe un thread separat si care face propriuzis
	 * salvarea.
	 */
	public void run() {
		outFile.println("<?xml version=\"1.0\" standalone=\"no\"?>\n" +
						"<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 20001102//EN\"\n" +
						"\"http://www.w3.org/TR/2000/CR-SVG-20001102/DTD/svg-20001102.dtd\">\n" +
						"<svg width=\"1023\" height=\"1023\">\n" +
						"<desc>\n" +
						"   Dump of Whiteboard 2.0 from NHApplet\n" +
						"   Date: " + (new Date()).toString() + " \n" +
						"</desc>\n" +
						"<defs>\n  <marker id=\"EndArrow\" viewBox=\"0 0 10 10\"" +
						" refX=\"0\" refY=\"5\" markerUnits=\"strokeWidth\"" +
						" markerWidth=\"4\" markerHeight=\"3\"  orient=\"auto\">" +
						" <path d=\"M 0 0 L 10 5 L 0 10 z\" />  </marker>\n" +

						"  <marker id=\"StartArrow\" viewBox=\"-10 -10 10 10\"" +
						" refX=\"0\" refY=\"-5\" markerUnits=\"strokeWidth\"" +
						" markerWidth=\"4\" markerHeight=\"3\"  orient=\"auto\">" +
						" <path d=\"M 0 0 L -10 -5 L 0 -10 z\" />  </marker>\n" +
						"</defs>\n\n");
		wb.saveFigures(outFile, filename);
		outFile.println("\n</svg>\n");
		outFile.close();
		wb.savedAs = filename;
	}
}