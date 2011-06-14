
package ro.intellisoft.whiteboard;

/**
 * Title:        Clasa care stie sa incarce continutul wb-ului pe un alt thread.
 *                10.10.2001 update: SVGLoader va incarca si proiecte de slideshow
 * Copyright:    Copyright (c) 2001
 * Company:      Intellisoft
 * @author Ovidiu Maxiniuc
 * @version 2.0
 */

import ro.intellisoft.XML.XMLMiniParser;

import javax.swing.*;
import java.io.File;


public class SVGLoader extends Thread {
	/**
	 * Whiteboard-ul in care urmeaza sa se incarce figurile.
	 */
	Whiteboard wb = null;

	/**
	 * Fisierul care va fi parsat:
	 */
	File f = null;

	/**
	 * grupul din Whiteboard pe cre fac afisarea!
	 */
	private String activeGroup = null;

	/**
	 * Constructorul care ia ca parametru whiteboard-ul pe care incarca.<br>
	 * Stie singur sa deskida fereastra de dialog si sa selecteze numele
	 * si tipul fisierului.
	 * @param wb whiteboard-ul care incarca
	 */
	public SVGLoader(Whiteboard wb, String aGroup) throws Exception {
		this.wb = wb;
		activeGroup = aGroup;
		final JFileChooser fc = new JFileChooser();
		fc.setDialogType(JFileChooser.OPEN_DIALOG);
		fc.setDialogTitle("Choose the filename you want to load...");
		//construim filtrul de rigoare
		fc.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
			public boolean accept(java.io.File f) {
				if (f.isDirectory())
					return true;
				return f.getName().toLowerCase().endsWith(".wss");
			}

			public String getDescription() {
				return "Whiteboard SlideShow project files (*.WSS)";
			}
		});
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
		if (fc.showOpenDialog(wb.getParent()) == JFileChooser.APPROVE_OPTION) {
			f = new File(fc.getSelectedFile().getAbsolutePath());
		} else
			throw new Exception("User abort detected...");
	}

	/**
	 * Constructorul care ia ca parametru whiteboard-ul pe care incarca.<br>
	 * @param wb whiteboard-ul care incarca
	 * @param file numele fisierului ce trebuie incarcat
	 */
	public SVGLoader(Whiteboard wb, File file, String aGroup){
		this.wb = wb;
		this.f = file;
		activeGroup = aGroup;
	}

	/**
	 * Metoda care se lanseaza pe un thread separat si care face propriuzis
	 * incarcarea. <br>
	 * in functie de tipul fisierului incarcat va incarca un SVG sau un proiect
	 * de SlideShow
	 */
	public void run() {
		try {
			//presupunem ca e un fis. .SVG
			XMLMiniParser xmlParser = new XMLMiniParser();
			xmlParser.parse(f, new SVGHandler(wb, f.getParent(), activeGroup));
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		wb.savedAs = f.getAbsolutePath();
	}
}