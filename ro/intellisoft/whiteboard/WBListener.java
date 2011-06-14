
package ro.intellisoft.whiteboard;

import ro.intellisoft.whiteboard.shapes.*;
import com.hermix.SC;

import javax.swing.event.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * Title:        Whiteboard Listener
 * Description:  A class which listen to the mouse/key and Components events from a Whiteboard.
 * Copyright:    Copyright (c) 2001
 * Company:      Intellisoft
 * @author Ovidiu Maxiniuc
 * @version 2.0
 */

public class WBListener implements MouseListener, MouseMotionListener, ActionListener, ChangeListener, KeyListener {

	/**White-board-ul parinte. <br>Pentru a putea preciza cine si ce a facut.*/
	Whiteboard wb = null;

	/**Retin coordonatele mouse-ului pentru prelucrari ulterioare. (de exemplu drag & drop)*/
	private int x1, y1, x2, y2, xc, yc;

	/**Numar de cate ori s-a miscat mouse-ul in operatia de DRAd&DROP..*/
	private int dragCounter = 0;

	/**Lacat pentru a nu avea feed-back in momentul cand facem Zoom cu soarecele.*/
	private boolean lockZoom = false;

	/**Retin figura pe care vreau sa o mut...*/
	private Figure selectedFigure = null;

	/**
	 * Salvez vechea stare a WB daca intentionez sa fac o actiune de temporara,
	 * de scurta durata: panning sau zoom-ing cu soarecele.
	 */
	private int lastWBStatus = wb.IDLE;

	/**?????**/
	private int subEditMode = 0;

	/**
	 * Flag care imi spune ca whiteboard-ul asteapta ca utilizatorul sa
	 * introduca un caracter la pozitia indicata. <br>
	 * Daca este negativa tastele apasate vor fi ignorate.
	 */
	private int wait4TextAtPosition = -1;

	/**Constructorul. <br> Ia ca parametru doar whiteboard-ul pe care il asculta..*/
	public WBListener(Whiteboard wb) {
		this.wb = wb;
	}

	/**Invoked when an action occurs.*/
	public void actionPerformed(ActionEvent e) {
		Object theActioner = e.getSource();

		if (wb.getStatus() == Whiteboard.EDIT) {
			if (editFigure(theActioner, wb.editingFigure)) {
				wb.repaint();
				return;
			}
		}

		if (theActioner == wb.jtbEllipse || theActioner == wb.jtbRectangle || theActioner == wb.jtbLine || theActioner == wb.jtbFreehand || theActioner == wb.jtbEdit || theActioner == wb.jtbKill || theActioner == wb.jtbImage) {
			if (wb.tempText != null && wb.tempText.getText().length() > 0) {
				wb.send(wb.tempText);
			}
			wb.tempText = null;
			wait4TextAtPosition = -1;
			wb.blinkCursor.stopBlink();
		}

		if (theActioner == wb.jtbBold) {
			wb.bold = wb.jtbBold.isSelected();
			if (wb.tempText != null)
				wb.tempText.setBold(wb.bold);
		} else if (theActioner == wb.jtbItalic) {
			wb.italic = wb.jtbItalic.isSelected();
			if (wb.tempText != null)
				wb.tempText.setItalic(wb.italic);
		} else if (theActioner == wb.jtbUnderline) {
			wb.underline = wb.jtbUnderline.isSelected();
			if (wb.tempText != null)
				wb.tempText.setUnderline(wb.underline);
		} else if (theActioner == wb.jtbStroke) {
			wb.stroke = wb.jtbStroke.isSelected();
			if (wb.tempText != null)
				wb.tempText.setStroke(wb.stroke);
		} else if (theActioner == wb.jtbLine) {
			wb.setStatus(SC.GRP_Line);
		} else if (theActioner == wb.jtbArc) {
			wb.setStatus(SC.GRP_Arc);
		} else if (theActioner == wb.jtbFreehand) {
			wb.setStatus(SC.GRP_Free);
			//resetam vechea figura:
			wb.freeHandCore = null;
		} else if (theActioner == wb.jtbRectangle) {
			wb.setStatus(SC.GRP_Rectangle);
		} else if (theActioner == wb.jtbEllipse) {
			wb.setStatus(SC.GRP_Circle);
		} else if (theActioner == wb.jtbGText) {
			wb.setStatus(SC.GRP_GText);
			//resetam vechea figura:
			wb.tempText = null;
			wait4TextAtPosition = 0;
		} else if (theActioner == wb.jtbImage) {
			wb.setStatus(SC.GRP_Image);
		} else if (theActioner == wb.jtbEdit) {
			wb.setStatus(wb.MOVE);
		} else if (theActioner == wb.jtbKill) {
			wb.setStatus(wb.KILL);
		} else if (theActioner == wb.jtbFillFigures) {
			wb.jbFillColor.setEnabled(wb.jtbFillFigures.isSelected());
		} else if (theActioner == wb.jcbFontChooser) {
			wb.fontFace = wb.jcbFontChooser.getSelectedItem().toString();
			if (wb.tempText != null)
				wb.tempText.setFont(wb.fontFace);
		} else if (theActioner == wb.jbSave || theActioner == wb.jmiWBSave) {
			try {
				new SVGSaver(wb).start();
			} catch (Exception ex) {}
		} else if (theActioner == wb.jbPrint || theActioner == wb.jmiWBPrint) {
			new Launcher(wb).start();
		}//print
		else if (theActioner == wb.jbLoad || theActioner == wb.jmiWBLoad) {
			try {
				new SVGLoader(wb, wb.getGroup()).start();
				//mesajul urmator va fi afisat de thread-ul pe care tocmai l-am lansat...
				//wb.statusBar.setText("File successful loaded...");
			} catch (Exception ex) {}
		} else if (theActioner == wb.jcbFontSizeChooser) {
			try {
				wb.fontSize = Integer.parseInt(wb.jcbFontSizeChooser.getSelectedItem().toString());
			} catch (Exception ex) {
				return;
			}
			if (wb.tempText != null){
				wb.tempText.setFontSize(wb.fontSize);
			}
		} else if (theActioner == wb.jcbLineStyleChooser) {
			String lineStyle = wb.jcbLineStyleChooser.getSelectedItem().toString();
			if (lineStyle.equals("Dashed")) {
				wb.lineType = Figure.DASHED_LINE;
			} else if (lineStyle.equals("Dotted")) {
				wb.lineType = Figure.DOTTED_LINE;
			} else {
				wb.lineType = Figure.SOLID_LINE;
			}
		} else if (theActioner == wb.jcbArrowStyleChooser) {
			String lineStyle = wb.jcbArrowStyleChooser.getSelectedItem().toString();
			if (lineStyle.equals("End Arrow")) {
				wb.arrowType = Figure.END_HEAD_ARROW;
			} else if (lineStyle.equals("Both Arrows")) {
				wb.arrowType = Figure.TWO_HEADED_ARROW;
			} else if (lineStyle.equals("Start Arrow")) {
				wb.arrowType = Figure.START_HEAD_ARROW;
			} else {
				wb.arrowType = Figure.NO_HEADED_ARROW;
			}
		} else if (theActioner == wb.jtbRoundedCorner) {
			if (wb.jtbRoundedCorner.isSelected()) {
				wb.cornerType = Figure.ROUNDED_CORNER;
			} else {
				wb.cornerType = Figure.NORMAL_CORNER;
			}
		} else if (theActioner == wb.jcbLineSizeChooser) {
			try {
				wb.size = Integer.parseInt(wb.jcbLineSizeChooser.getSelectedItem().toString());
			} catch (Exception ex) {
				return;
			}
		} else if (theActioner == wb.jbForegroundColor) {
			Color c = JColorChooser.showDialog(wb.getParent(), "Choose foreground color", wb.foregroundColor);
			if (c != null) {
				wb.foregroundColor = c;
				wb.jbForegroundColor.setForeground(c);
			}
		} else if (theActioner == wb.jbBackgroundColor) {
			Color c = JColorChooser.showDialog(wb.getParent(), "Choose background color", wb.foregroundColor);
			if (c != null) {
				wb.setBackground(c);
				wb.jbBackgroundColor.setForeground(c);
				wb.repaint();
				wb.send(new Background(c));
			}
		} else if (theActioner == wb.jbFillColor) {
			Color c = JColorChooser.showDialog(wb.getParent(), "Choose fill color", wb.foregroundColor);
			if (c != null) {
				wb.fillColor = c;
				wb.jbFillColor.setForeground(c);
			}
		} else if (theActioner == wb.jbNew || theActioner == wb.jmiWBNew) {
			wb.clear();
			wb.send(new Clear());
		} else if (theActioner == wb.jcbmiHighQuality) {
			wb.highQuality = wb.jcbmiHighQuality.isSelected();
			wb.repaint();
		} else if (theActioner == wb.jcbZoomChooser) {
			if (lockZoom)
				return;
			lockZoom = true;
			String zoom = "";
			wb.repaint();
			lockZoom = false;
		} else if (theActioner == wb.jbResetZoom) {
			wb.jsZoom.setValue(1); //100%
		} else if (theActioner == wb.jtbShowConfigToolbar) {
			if (wb.editable){
				wb.getToolbarConfigPane().setVisible(wb.jtbShowConfigToolbar.isSelected());
			}
		} else if (theActioner == wb.jtbShowToolToolbar) {
			if (wb.editable){
				wb.getToolbarPane().setVisible(wb.jtbShowToolToolbar.isSelected());
			}
		}
		//tratam send wb. content to... X group
		else if (theActioner instanceof JMenuItem) {
			String groupToSend = ((JMenuItem) theActioner).getText();
			if (!groupToSend.endsWith(" group"))
				return;//nu stiu sa tratez;
			groupToSend = groupToSend.substring(0, groupToSend.length() - 6);
			//distribuim obiectele de pe wb-ul meu la ceilalti din grupul cerut
			//numai daca utilizatotul este sigur de acest lucru:
			int response = JOptionPane.showConfirmDialog(wb.getParent(), "Are you sure?\nThis may overload the network!", "Send content to " + groupToSend + " group?", JOptionPane.OK_CANCEL_OPTION);
			if (response == JOptionPane.OK_OPTION)
				wb.sendAllFigures();
		}

		wb.repaint();
		//indiferent ce s-a intamplat trimitem focusul la textField-ul de mesaje ca
		//utilizatorul sa poata continua sa introduca mesaje sau sa apese <enter>
		if (wb.getStatus() == SC.GRP_GText)
			wb.requestFocus();
	}

	/**
	 * Invoked when the target of the listener has changed its state.
	 * @see ChangeListener#stateChanged
	 */
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == wb.jsZoom) {
			//verificam daca avem dreptul sa modificam ZOOM-ul
			if (wb.jsZoom.getValue() > 1) {
				double scaleTo = 9 + wb.jsZoom.getValue();
				wb.zoomViewer.setText(scaleTo / 10 + "0X");
				if (lockZoom)
					return;
				wb.zoomX = (float) (scaleTo * 10);
			} else {
				wb.zoomViewer.setText((99 + wb.jsZoom.getValue()) + "%");
				if (lockZoom)
					return;
				wb.zoomX = 99 + wb.jsZoom.getValue();
			}
			wb.zoomY = wb.zoomX /= 100.0;
			wb.setPreferredSize(new Dimension((int) (wb.zoomX * wb.getWbSize().getWidth()), (int) (wb.zoomY * wb.getWbSize().getHeight())));
			wb.setSize(new Dimension((int) (wb.zoomX * wb.getWbSize().getWidth()), (int) (wb.zoomY * wb.getWbSize().getHeight())));
		}//if (e.getSource...
	}//stateChanged


	/**
	 * Metoda apelata in momentul editarii unei figuri.
	 * Returneaza true ddaca evenimentul a fost tratat!
	 */
	private boolean editFigure(Object theActioner, Figure editingFigure) {
		/*
        * Sunt mai multe cazuri:
        * 1. s-a apasat un buton ce nu configureaza figura aleasa
        *       Line/Free/Rectangle/Ellipse/GTExt/Move/Kill/Load/Save/new
        *       sau editingFigure == null
        *       => se iese din modul de editare
        * 2. in functie de tipul figurii se testeaza butoanele care ne intereseaza
        *       se updateaza editingFigure si difference
        *       cazuri posibile  = #nr tipuri figuri
        * 3. restul se ignora
        */
		if (theActioner == wb.jtbEllipse || theActioner == wb.jtbGText || theActioner == wb.jbNew || theActioner == wb.jmiWBNew || theActioner == wb.jtbEdit || theActioner == wb.jtbKill || theActioner == wb.jtbLine || theActioner == wb.jtbFreehand || theActioner == wb.jbSave || theActioner == wb.jmiWBSave || theActioner == wb.jbLoad || theActioner == wb.jmiWBLoad || theActioner == wb.jbPrint || theActioner == wb.jmiWBPrint || theActioner == wb.jtbRectangle || theActioner == wb.jtbArc || editingFigure == null || theActioner == wb.jtbImage || theActioner == wb.jbBackgroundColor) {//cazul #1
			//iesim din modul de editare:
			Transformer t = editingFigure.getTransformer();
			if (wb.editingFigure instanceof Arc)
				t.saveArc((Arc) (wb.editingFigure));
			wb.send(t);
			wb.editingFigure = null;
			wb.blinkCursor.stopBlink();
			wb.setStatus(Whiteboard.IDLE);
			return false;
		} else if (theActioner == wb.jcbmiHighQuality)
			return false;//permit sa skimb calitatea afisarii in timpul editarii

		if (theActioner == wb.jbResetImageSize) { //cazul 2.0
			if (wb.editingFigure instanceof ro.intellisoft.whiteboard.shapes.Image) {
				Dimension d = wb.editingFigure.getPreferedSize(-1, -1);
				wb.editingFigure.transform(Figure.ANY_CORNER_HIT, d.width / 2, d.height / 2);
				return true;
			}
		} else if (editingFigure instanceof GText) { //cazul 2.1
			GText f = (GText) editingFigure;
			if (theActioner == wb.jtbBold) {
				f.setBold(wb.jtbBold.isSelected());
			} else if (theActioner == wb.jtbItalic) {
				f.setItalic(wb.jtbItalic.isSelected());
			} else if (theActioner == wb.jtbUnderline) {
				f.setUnderline(wb.jtbUnderline.isSelected());
			} else if (theActioner == wb.jtbStroke) {
				f.setStroke(wb.jtbStroke.isSelected());
			} else if (theActioner == wb.jcbFontChooser) {
				wb.fontFace = wb.jcbFontChooser.getSelectedItem().toString();
				f.setFont(wb.fontFace);
			} else if (theActioner == wb.jcbFontSizeChooser) {
				try {
					wb.fontSize = Integer.parseInt(wb.jcbFontSizeChooser.getSelectedItem().toString());
				} catch (Exception ex) {
					return true;
				}
				f.setFontSize(wb.fontSize);
			} else if (theActioner == wb.jbForegroundColor) {
				Color c = JColorChooser.showDialog(wb.getParent(), "Choose foreground color", wb.foregroundColor);
				if (c != null) {
					wb.foregroundColor = c;
					wb.jbForegroundColor.setForeground(c);
					editingFigure.setForeground(wb.foregroundColor);
				}
			}
			return true;
		}//end with Gtext

		//toate celelalte figuri au stil, culoare si grosime de linie:
		if (theActioner == wb.jcbLineStyleChooser) {
			String lineStyle = wb.jcbLineStyleChooser.getSelectedItem().toString();
			if (lineStyle.equals("Dashed")) {
				wb.lineType = Figure.DASHED_LINE;
				editingFigure.setStyle(Figure.LINE_STYLE_MASK, Figure.DASHED_LINE);
			} else if (lineStyle.equals("Dotted")) {
				wb.lineType = Figure.DASHED_LINE;
				editingFigure.setStyle(Figure.LINE_STYLE_MASK, Figure.DOTTED_LINE);
			} else {
				wb.lineType = Figure.SOLID_LINE;
				editingFigure.setStyle(Figure.LINE_STYLE_MASK, Figure.SOLID_LINE);
			}
			return true;
		}
		if (theActioner == wb.jcbLineSizeChooser) {
			try {
				wb.size = Integer.parseInt(wb.jcbLineSizeChooser.getSelectedItem().toString());
			} catch (Exception ex) {
				return true;
			}
			editingFigure.setSize(wb.size);
			return true;
		}
		if (theActioner == wb.jbForegroundColor) {
			Color c = JColorChooser.showDialog(wb.getParent(), "Choose foreground color", wb.foregroundColor);
			if (c != null) {
				wb.foregroundColor = c;
				wb.jbForegroundColor.setForeground(c);
				editingFigure.setForeground(c);
			}
			return true;
		}

		//luam in continuare figurile care au mai ramas si proprietatile specifice
		if (editingFigure instanceof Ellipse) { //caz 2.2
			Ellipse f = (Ellipse) editingFigure;
			if (theActioner == wb.jtbFillFigures) {
				wb.jbFillColor.setEnabled(wb.jtbFillFigures.isSelected());
				f.setFillColor(wb.jtbFillFigures.isSelected()?wb.fillColor:null);
			} else if (theActioner == wb.jbFillColor) {
				Color c = JColorChooser.showDialog(wb.getParent(), "Choose fill color", wb.fillColor);
				if (c != null) {
					wb.fillColor = c;
					wb.jbFillColor.setForeground(c);
					f.setFillColor(c);
				}
			}
			return true;
		}//end ellipse

		if (editingFigure instanceof ro.intellisoft.whiteboard.shapes.Rectangle) {
			ro.intellisoft.whiteboard.shapes.Rectangle f = (ro.intellisoft.whiteboard.shapes.Rectangle) editingFigure;
			if (theActioner == wb.jtbFillFigures) {
				wb.jbFillColor.setEnabled(wb.jtbFillFigures.isSelected());
				f.setFillColor(wb.jtbFillFigures.isSelected()?wb.fillColor:null);
			} else if (theActioner == wb.jbFillColor) {
				Color c = JColorChooser.showDialog(wb.getParent(), "Choose fill color", wb.fillColor);
				if (c != null) {
					wb.fillColor = c;
					wb.jbFillColor.setForeground(c);
					f.setFillColor(c);
				}
			} else if (theActioner == wb.jtbRoundedCorner) {
				if (wb.jtbRoundedCorner.isSelected()) {
					wb.cornerType = Figure.ROUNDED_CORNER;
					f.setStyle(Figure.CORNER_MASK, wb.cornerType);
				} else {
					wb.cornerType = Figure.NORMAL_CORNER;
					f.setStyle(Figure.CORNER_MASK, wb.cornerType);
				}
			}
			return true;
		}//end rectangle...

		//au mai ramas linia si freehand-ul care suporta sageti...
		if (theActioner == wb.jcbArrowStyleChooser) {
			String lineStyle = wb.jcbArrowStyleChooser.getSelectedItem().toString();
			if (lineStyle.equals("End Arrow")) {
				wb.arrowType = Figure.END_HEAD_ARROW;
				editingFigure.setStyle(Figure.ARROW_MASK, wb.arrowType);
			} else if (lineStyle.equals("Both Arrows")) {
				wb.arrowType = Figure.TWO_HEADED_ARROW;
				editingFigure.setStyle(Figure.ARROW_MASK, wb.arrowType);
			} else if (lineStyle.equals("Start Arrow")) {
				wb.arrowType = Figure.START_HEAD_ARROW;
				editingFigure.setStyle(Figure.ARROW_MASK, wb.arrowType);
			} else {
				wb.arrowType = Figure.NO_HEADED_ARROW;
				editingFigure.setStyle(Figure.ARROW_MASK, wb.cornerType);
			}
			return true;
		}//arrows

		//nu a mai ramas decat freehand-ul cu coltul patrat
		if (theActioner == wb.jtbRoundedCorner) {
			if (wb.jtbRoundedCorner.isSelected()) {
				wb.cornerType = Figure.ROUNDED_CORNER;
				editingFigure.setStyle(Figure.CORNER_MASK, wb.cornerType);
			} else {
				wb.cornerType = Figure.NORMAL_CORNER;
				editingFigure.setStyle(Figure.CORNER_MASK, wb.cornerType);
			}
		}//corner style

		//restul ignoram
		return true;
	}

	/**Invoked when a key has been pressed.*/
	public void keyPressed(KeyEvent e) {
		if (wb.getStatus() == Whiteboard.EDIT) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				//Iesim din modul de editare si salvam
				Transformer t = wb.editingFigure.getTransformer();
				if (wb.editingFigure instanceof Arc)
					t.saveArc((Arc) (wb.editingFigure));
				wb.send(t);
				wb.editingFigure = null;
				wb.setStatus(Whiteboard.IDLE);
				wb.blinkCursor.stopBlink();
			}
		}
	}

	/**Invoked when a key has been released.*/
	public void keyReleased(KeyEvent e) {
		if (wb.editingFigure != null && e.getKeyChar() == KeyEvent.VK_DELETE) {
			wb.send(new Eraser(wb.editingFigure.getUID()));
			wb.remove(wb.editingFigure);
			wb.blinkCursor.stopBlink();
			return;
		}
	}

	/**Invoked when a key has been typed*/
	public void keyTyped(KeyEvent e) {
		if (wb.tempText == null || wait4TextAtPosition < 0) {
			//s-a apsat din greseala, nu prelucrez nuimic
			return;
		}
		//verific pe rand ce tasta s-a apaat:
		if (e.getKeyChar() == KeyEvent.VK_ENTER) {
			//<ENTER>: salvez figura si init
			wb.send(wb.tempText);
			wb.tempText = null;
			wb.blinkCursor.stopBlink();
			wait4TextAtPosition = -1;
		} else if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
			//e BACKSPACE, sterg ultima litera (daca sirul nu e vid)
			String s = wb.tempText.getText();
			if (s.length() > 0) {
				s = s.substring(0, s.length() - 1);
				wb.tempText.setText(s);
				wb.repaint();
			}
			//altfel nu facem nika
		} else {
			//daca am ajuns aici atunci e un caracter oarecare
			//deocamdata sunt acceptata orice fel de caractere
			//cu exceptzia <ENTER> si <BACKSPace>
			wb.tempText.setText(wb.tempText.getText() + (char) e.getKeyChar());
			wb.repaint();
		}
	}


	/**
	 * Invoked when a mouse button is pressed on a component and then dragged.
	 */
	public void mouseDragged(MouseEvent e) {
		//nu tratam decat mesaje din partea wb-ului
		if (e.getSource() != wb)
			return;
		dragCounter++;
		int status = wb.getStatus();
		Graphics2D g2 = (Graphics2D) wb.getGraphics();
		if (status != wb.ZOOM && status != wb.MOVE && status != wb.SHIFT) {
			g2.scale(wb.zoomX, wb.zoomY);
			g2.setPaint(wb.foregroundColor);
		}
		if (status != SC.GRP_Free)
			g2.setXORMode(wb.backgroundColor);
		//luam fiecare stare in parte si o tratam
		if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
			//popupmenu.show()
		} else {
			if (status == Whiteboard.EDIT) {
				if (dragCounter == 1) {
					x2 = e.getX();
					y2 = e.getY();
					//verificam daca la inceputul miscarii soarecelui
					// ne aflam pe figura pe care o editam.
					int pPosition = wb.editingFigure.contains((int) (e.getX() / wb.zoomX), (int) (e.getY() / wb.zoomY));
					if (pPosition != Figure.OUT_OF_BOUNDS) {
						selectedFigure = wb.editingFigure;
						subEditMode = pPosition;
					} else
						selectedFigure = null;
				} else if (selectedFigure != null) {
					if ((subEditMode & Figure.ANY_CORNER_HIT) == 0) {
						selectedFigure.translateWith((int) ((e.getX() - x2) / wb.zoomX), (int) ((e.getY() - y2) / wb.zoomY));
					} else {
						int xc = selectedFigure.getPosition().x + selectedFigure.getWidth() / 2;
						int yc = selectedFigure.getPosition().y + selectedFigure.getHeight() / 2;
						//parametrii care vor fi folositi la scalare:
						int paramX = xc - (int) (e.getX() / wb.zoomX);
						int paramY = yc - (int) (e.getY() / wb.zoomY);
						if (selectedFigure.getType() != SC.GRP_Line) {
							paramX = Math.abs(paramX);
							paramY = Math.abs(paramY);
						}
						//figura perfecta?
						if ((e.getModifiers() & MouseEvent.CTRL_MASK) != 0) {
							Dimension prefSize = wb.editingFigure.getPreferedSize(paramX, paramY);
							paramX = prefSize.width;
							paramY = prefSize.height;
						}
						if (selectedFigure.getType() == SC.GRP_Arc)
							selectedFigure.transform(subEditMode, (int) (e.getX() / wb.zoomX), (int) (e.getY() / wb.zoomY));
						else
							selectedFigure.transform(subEditMode & Figure.ANY_CORNER_HIT, paramX, paramY);
					}
					wb.repaint();
					x2 = e.getX();
					y2 = e.getY();
				}
			} else if (status == Whiteboard.MOVE) {
				if (dragCounter == 1) {
					selectedFigure = wb.getFigureAt((int) (e.getX() / wb.zoomX), (int) (e.getY() / wb.zoomY));
					x2 = e.getX();
					y2 = e.getY();
				}
				if (selectedFigure == null)
					return;//daca nu am 'prins' nici o figura...
				selectedFigure.translateWith((int) ((e.getX() - x2) / wb.zoomX), (int) ((e.getY() - y2) / wb.zoomY));
				wb.repaint();
				x2 = e.getX();
				y2 = e.getY();
			}//status=move
			else if (status == Whiteboard.SHIFT) {
				x2 = e.getX();
				y2 = e.getY();
				//de acu' tre sa translatez numai coltzu din stanga sus
				Point p = ((JViewport) wb.getParent()).getViewPosition();
				p.translate(x1 - x2, y1 - y2);
				if (p.x < 0)
					p.x = 0;
				if (p.y < 0)
					p.y = 0;
				((JViewport) wb.getParent()).setViewPosition(p);
				//wb.repaint();
			}//status=shift
			else if (status == Whiteboard.ZOOM) {
				if (dragCounter == 1) {
					x2 = x1;
					y2 = y1;
				}
				g2.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1) + 1, Math.abs(y2 - y1) + 1);
				x2 = e.getX();
				y2 = e.getY();
				g2.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1) + 1, Math.abs(y2 - y1) + 1);
			}//status=ZOOM
			/*
             * de aici incep sa simulez figurile pe ecran...
             */
			else if (status == SC.GRP_Line || status == SC.GRP_Arc) {
				if (dragCounter > 1)
					new Line((int) (x1 / wb.zoomX), (int) (y1 / wb.zoomY), (int) (x2 / wb.zoomX), (int) (y2 / wb.zoomY), wb.size, wb.arrowType | wb.lineType, wb.foregroundColor, 0L).draw(g2);
				x2 = e.getX();
				y2 = e.getY();
				new Line((int) (x1 / wb.zoomX), (int) (y1 / wb.zoomY), (int) (x2 / wb.zoomX), (int) (y2 / wb.zoomY), wb.size, wb.arrowType | wb.lineType, wb.foregroundColor, 0L).draw(g2);
			}//status==line
			else if (status == Whiteboard.CARC) {
				new Arc((int) (x1 / wb.zoomX), (int) (y1 / wb.zoomY), (int) (xc / wb.zoomX), (int) (yc / wb.zoomY), (int) (x2 / wb.zoomX), (int) (y2 / wb.zoomY), wb.size, wb.arrowType | wb.lineType, wb.foregroundColor, 0L).draw(g2);
				xc = 2 * e.getX() - (x1 + x2) / 2;
				yc = 2 * e.getY() - (y1 + y2) / 2;
				new Arc((int) (x1 / wb.zoomX), (int) (y1 / wb.zoomY), (int) (xc / wb.zoomX), (int) (yc / wb.zoomY), (int) (x2 / wb.zoomX), (int) (y2 / wb.zoomY), wb.size, wb.arrowType | wb.lineType, wb.foregroundColor, 0L).draw(g2);
			}//status==CARC
			else if (status == SC.GRP_Rectangle || status == SC.GRP_Circle || status == SC.GRP_Image) {
				//stergem veche figura:
				if (status == SC.GRP_Rectangle || status == SC.GRP_Image)
					g2.drawRect((int) (Math.min(x1, x2) / wb.zoomX), (int) (Math.min(y1, y2) / wb.zoomY), (int) (Math.abs(x2 - x1) / wb.zoomX), (int) (Math.abs(y2 - y1) / wb.zoomY));
				else
					g2.drawOval((int) (Math.min(x1, x2) / wb.zoomX), (int) (Math.min(y1, y2) / wb.zoomY), (int) (Math.abs(x2 - x1) / wb.zoomX), (int) (Math.abs(y2 - y1) / wb.zoomY));
				//luam noile puncte:
				x2 = e.getX();
				y2 = e.getY();
				if ((e.getModifiers() & MouseEvent.CTRL_MASK) != 0) {
					//facem figura perfecta: latura mai mica o marim,
					//pastram latura mai mare ca element de masura
					//            Math.abs(_1-_2)
					//trebuie sa vedem in ce parte marim latura mai mica
					if (Math.abs(x2 - x1) >= Math.abs(y2 - y1))
						y2 = y1 + (y2 > y1?Math.abs(x2 - x1):-Math.abs(x2 - x1));
					else
						x2 = x1 + (x2 > x1?Math.abs(y2 - y1):-Math.abs(y2 - y1));
				}
				//redesenam noua figura:
				if (status == SC.GRP_Rectangle || status == SC.GRP_Image)
					g2.drawRect((int) (Math.min(x1, x2) / wb.zoomX), (int) (Math.min(y1, y2) / wb.zoomY), (int) (Math.abs(x2 - x1) / wb.zoomX), (int) (Math.abs(y2 - y1) / wb.zoomY));
				else
					g2.drawOval((int) (Math.min(x1, x2) / wb.zoomX), (int) (Math.min(y1, y2) / wb.zoomY), (int) (Math.abs(x2 - x1) / wb.zoomX), (int) (Math.abs(y2 - y1) / wb.zoomY));
			}//status==rectangle or ellipse
			else if (status == SC.GRP_Free) {
				//desenez segmentul curent:
				x2 = e.getX();
				y2 = e.getY();
				g2.drawLine((int) (x1 / wb.zoomX), (int) (y1 / wb.zoomY), (int) (x2 / wb.zoomX), (int) (y2 / wb.zoomY));
				//salvez punctul curent in vector...:
				wb.freeHandCore.add(new Point((int) (x2 / wb.zoomX), (int) (y2 / wb.zoomY)));
				//initializez punctul de inceput pentru segmentul urmator...
				x1 = x2;
				y1 = y2;
			}//status==line
		}//mouse modifiers
	}//mouseDragged

	/**
	 * Invoked when the mouse button has been moved on a component
	 * (with no buttons no down).
	 */
	public void mouseMoved(MouseEvent e) {
		//nu tratam decat mesaje din partea wb-ului
		if (e.getSource() != wb)
			return;
		Graphics2D g2 = (Graphics2D) wb.getGraphics();
		g2.scale(wb.zoomX, wb.zoomY);
		g2.setPaint(wb.foregroundColor);
		g2.setXORMode(wb.backgroundColor);
		if (wb.getStatus() == Whiteboard.CARC) {
			new Arc((int) (x1 / wb.zoomX), (int) (y1 / wb.zoomY), (int) (xc / wb.zoomX), (int) (yc / wb.zoomY), (int) (x2 / wb.zoomX), (int) (y2 / wb.zoomY), wb.size, wb.arrowType | wb.lineType, wb.foregroundColor, 0L).draw(g2);
			xc = 2 * e.getX() - (x1 + x2) / 2;
			yc = 2 * e.getY() - (y1 + y2) / 2;
			new Arc((int) (x1 / wb.zoomX), (int) (y1 / wb.zoomY), (int) (xc / wb.zoomX), (int) (yc / wb.zoomY), (int) (x2 / wb.zoomX), (int) (y2 / wb.zoomY), wb.size, wb.arrowType | wb.lineType, wb.foregroundColor, 0L).draw(g2);
		}//status==CARC
	}//mouseMoved

	/**
	 * Invoked when the mouse has been clicked on a component.
	 */
	public void mouseClicked(MouseEvent e) {
		//nu tratam decat mesaje din partea wb-ului
		if (e.getSource() != wb)
			return;
	}

	/**
	 * Invoked when the mouse enters a component.
	 */
	public void mouseEntered(MouseEvent e) {
		//nu tratam decat mesaje din partea wb-ului
		if (e.getSource() != wb)
			return;
		wb.setCursor(wb.mouseCursor);
	}

	/**
	 * Invoked when the mouse exits a component.
	 */
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Invoked when a mouse button has been pressed on a component.
	 */
	public void mousePressed(MouseEvent e) {
		//nu tratam decat mesaje din partea wb-ului
		if (e.getSource() != wb)
			return;
		wb.requestFocusInWindow();
		int status = wb.getStatus();

		//salvez coordonatele pentru prelucrari ulterioare:
		//numai daca nu sunt la CARC, altfel pierd capatele arcului
		if (status != Whiteboard.CARC) {
			x1 = x2 = e.getX();
			y1 = y2 = e.getY();
		}
		dragCounter = 0;
		if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
			//voi pune meniul daca va fi necesar...
			return;
		}

		if ((e.getModifiers() & MouseEvent.SHIFT_MASK) != 0) {
			//activam modul de Panning:
			lastWBStatus = wb.getStatus();
			wb.setStatus(wb.SHIFT);
		} else if ((e.getModifiers() & MouseEvent.ALT_MASK) != 0) {
			//activam modul de Zoom'ing:
			lastWBStatus = wb.getStatus();
			wb.setStatus(wb.ZOOM);
		} else if (status == SC.GRP_Free) {
			//initializam vectorul cu punctele:
			wb.freeHandCore = new java.util.Vector(10, 50);
			wb.freeHandCore.add(new Point((int) (x1 / wb.zoomX), (int) (y1 / wb.zoomY)));
		} else if (status == Whiteboard.CARC) {
			/*do nothing*/
		} else if (status == Whiteboard.EDIT) {
			Figure hitFigure = wb.getFigureAt((int) (x1 / wb.zoomX), (int) (y1 / wb.zoomY));
			if (hitFigure != null && hitFigure != wb.editingFigure) {
				//am selectat o alta figura pentru editare..
				Transformer t = wb.editingFigure.getTransformer();
				if (wb.editingFigure instanceof Arc){
					t.saveArc((Arc) (wb.editingFigure));
				}
				wb.send(t);
				wb.blinkCursor.stopBlink();
				wb.setStatus(Whiteboard.IDLE);

				wb.editingFigure = hitFigure;
				//ramanem in modul de editare...
				wb.setStatus(Whiteboard.EDIT);
			}
		} else if (status == Whiteboard.MOVE) {
			Figure hitFigure = wb.getFigureAt((int) (x1 / wb.zoomX), (int) (y1 / wb.zoomY));
			if (hitFigure != null){
				wb.editingFigure = hitFigure;
				wb.setStatus(Whiteboard.EDIT);
			}
		} else
			;//???
	}


	/**Invoked when a mouse button has been released on a component.*/
	public void mouseReleased(MouseEvent e) {
		//nu tratam decat mesaje din partea wb-ului
		if (e.getSource() != wb)
			return;
		//verificam daca est meniul contextual:
		if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0) {
			wb.showMenu(e.getX(), e.getY());
			return;
		}
		//luam fiecare stare in parte si o tratam
		int status = wb.getStatus();
		if (status == wb.SHIFT) {
			//am terminat shiftarea, ma intorc la vechea stare...
			wb.setStatus(lastWBStatus);
			//si nu mai fac nika...
			return;
		} else if (status == wb.ZOOM) {
			//calculez coeficientul de redimensionare
			double coef = x1 == x2 ? 1 : wb.getParent().getWidth() / (Math.abs(x1 - x2 * 1.0));
			//informez applet-ul ca ma voi redimensiona:
			if (wb.zoomX * coef > 10 || wb.zoomX * coef * 100 < 1)
				coef = 1; //nu admitem scalare asa de mica/mare
			//si ma redimensionez:
			wb.zoomY = wb.zoomX = (float) (wb.zoomX * coef);

			wb.setPreferredSize(new Dimension((int) (wb.zoomX * wb.getWbSize().getWidth()), (int) (wb.zoomY * wb.getWbSize().getHeight())));
			wb.setSize(new Dimension((int) (wb.zoomX * wb.getWbSize().getWidth()), (int) (wb.zoomY * wb.getWbSize().getHeight())));

			//System.out.println(wb.zoomY);
			//setam scroll-slider-ul cu marimea gasita-ul
			lockZoom = true;
			if (wb.zoomX > 1)
				wb.jsZoom.setValue((int) wb.zoomX * 10);
			else
				wb.jsZoom.setValue((int) ((1 + wb.zoomX) * 100));
			lockZoom = false;

			//de acu' tre sa translatez numai coltzu din stanga sus
			Point p = new Point((int) (Math.min(x1, x2) * coef), (int) (Math.min(y1, y2) * coef));
			if (p.x < 0)
				p.x = 0;
			if (p.y < 0)
				p.y = 0;
			((JViewport) wb.getParent()).setViewPosition(p);
			//am terminat ZOOMarea, ma intorc la vechea stare...
			wb.setStatus(lastWBStatus);
			//redesenez:
			//wb.repaint();
			//si nu mai fac nika...
			return;
		}

		if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) == 0) {
			if (status == wb.KILL) {
				Figure f = wb.getFigureAt((int) (e.getX() / wb.zoomX), (int) (e.getY() / wb.zoomY));
				if (f != null) {
					wb.send(new Eraser(f.getUID()));
					wb.remove(f);
					wb.repaint();
				}
			}//if (status=kill)
			if (status == wb.MOVE) {
				if (selectedFigure != null) {
					com.hermix.Rect r = new com.hermix.Rect();
					r.x1 = selectedFigure.getPosition().x;
					r.x2 = selectedFigure.getWidth();
					r.y1 = selectedFigure.getPosition().y;
					r.y2 = selectedFigure.getHeight();
					wb.send(new Transformer(SC.GRP_TMove_Figure, selectedFigure.getUID(), r, null, -1, null, null, 0, null, 0, 0));
				}
			}//if (status=MOVE)
			else if (status == Whiteboard.EDIT) {
				if (wb.editingFigure != null) {
/*                    if (wb.editingFigure.getType()!=SC.GRP_Arc){
                        wb.difference.translateTo(wb.editingFigure.getPosition().x,
                            wb.editingFigure.getPosition().y);
                        wb.difference.transform(-1,wb.editingFigure.getWidth(),
                            wb.editingFigure.getHeight());
                    }
                    else//este un arc, tre sa salvam 3 coordonate...
                        wb.difference.saveArc((Arc)(wb.editingFigure));
  */
				}
			} else if (status == SC.GRP_Line) {
				int caract = wb.arrowType | wb.lineType;
				//salvam linia:
				Line l = new Line((int) (x1 / wb.zoomX), (int) (y1 / wb.zoomY), (int) (x2 / wb.zoomX), (int) (y2 / wb.zoomY), wb.size, caract, wb.foregroundColor, wb.hc);
				wb.add(l);
				wb.send(l);
			}//if status=Line
			else if (status == SC.GRP_Arc) {
				xc = x2;
				yc = y2;
				wb.setStatus(Whiteboard.CARC);
			}//if status=ARC
			else if (status == Whiteboard.CARC) {
				int caract = wb.arrowType | wb.lineType;
				//salvam Curva:
				Arc a = new Arc((int) (x1 / wb.zoomX), (int) (y1 / wb.zoomY), (int) (xc / wb.zoomX), (int) (yc / wb.zoomY), (int) (x2 / wb.zoomX), (int) (y2 / wb.zoomY), wb.size, caract, wb.foregroundColor, wb.hc);
				wb.add(a);
				wb.send(a);
				wb.setStatus(SC.GRP_Arc);
			}//if status=CARC
			else if (status == SC.GRP_Free) {
				int caract = wb.arrowType | wb.lineType | wb.cornerType;
				//salvam mazgaleala:
				FreeHand fh = new FreeHand(wb.freeHandCore, wb.size, caract, wb.foregroundColor, wb.hc);
				wb.add(fh);
				wb.send(fh);
				//gata, nu mai avem nevoie de asta:
				wb.freeHandCore = null;
			}//if status=freehand
			else if (status == SC.GRP_Rectangle) {
				int caract = wb.cornerType | wb.lineType;
				//aducem la coordonatele wb:
				x1 /= wb.zoomX;
				x2 /= wb.zoomX;
				y1 /= wb.zoomY;
				y2 /= wb.zoomY;
				//salvam figura
				ro.intellisoft.whiteboard.shapes.Rectangle r = new ro.intellisoft.whiteboard.shapes.Rectangle(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2), wb.size, caract, wb.foregroundColor, wb.jtbFillFigures.isSelected()? wb.fillColor: null, wb.hc);
				wb.add(r);
				wb.send(r);
			}//if status=Rectangle
			else if (status == SC.GRP_Circle) {
				int caract = wb.lineType;
				//aducem la coordonatele wb:
				x1 /= wb.zoomX;
				x2 /= wb.zoomX;
				y1 /= wb.zoomY;
				y2 /= wb.zoomY;
				//salvam figura
				Ellipse el = new Ellipse(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2), wb.size, caract, wb.foregroundColor, wb.jtbFillFigures.isSelected()? wb.fillColor: null, wb.hc);
				wb.add(el);
				wb.send(el);
			}//if status=ellipse
			else if (status == SC.GRP_GText) {
				//daca deja scrisesem ceva.. salvez:
				if (wb.tempText != null && wb.tempText.getText().length() > 0) {
					wb.send(wb.tempText);
				}
				wait4TextAtPosition = 0;
				//initializam textul temporar:
				wb.tempText = new GText((int) (x1 / wb.zoomX), (int) (y1 / wb.zoomY), wb.fontSize, 0, wb.foregroundColor, "", wb.fontFace, wb.hc);
				wb.add(wb.tempText);
				wb.blinkCursor.stopBlink();
				wb.blinkCursor = new BlinkCursor(wb);
				wb.blinkCursor.start();
				wb.tempText.setBold(wb.bold);
				wb.tempText.setItalic(wb.italic);
				wb.tempText.setUnderline(wb.underline);
				wb.tempText.setStroke(wb.stroke);
			} else if (status == SC.GRP_Image) {
				//ca sa am dimensiunea pusa pe ecran in timpul alegerii fisierulei
				ro.intellisoft.whiteboard.shapes.Rectangle futureImage = new ro.intellisoft.whiteboard.shapes.Rectangle(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2), 0, 0, Color.black, null, -1);
				wb.add(futureImage);
				final JFileChooser fc = new JFileChooser();
				fc.setDialogType(JFileChooser.OPEN_DIALOG);
				fc.setDialogTitle("Choose the file to open...");
				//daca avem nevoie de fisiere imagini adaugam filtru de rigoare
				fc.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
					public boolean accept(java.io.File f) {
						if (f.isDirectory())
							return true;
						String s = f.getName().toLowerCase();
						if (s.endsWith(".jpeg"))
							return true;
						if (s.endsWith(".jpg"))
							return true;
						if (s.endsWith(".jif"))
							return true;
						return s.endsWith(".jfif");
					}

					public String getDescription() {
						return "JPEG files (*.JPG, *.JPEG, *.JIF, *.JFIF)";
					}
				});
				fc.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
					public boolean accept(java.io.File f) {
						if (f.isDirectory())
							return true;
						String s = f.getName().toLowerCase();
						return s.endsWith(".gif");
					}

					public String getDescription() {
						return "GIF files (*.GIF)";
					}
				});
				if (fc.showOpenDialog(wb.getParent()) == JFileChooser.APPROVE_OPTION) {
					//citesc continutul fisierului:
					ImageIcon imgIcon = new ImageIcon(fc.getSelectedFile().getAbsolutePath());
					//creez obiectul la mine si il trimit si la ceilalti:
					ro.intellisoft.whiteboard.shapes.Image img = new ro.intellisoft.whiteboard.shapes.Image(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2), wb.hc * 0x100000000L + fc.getSelectedFile().getName().hashCode());
					wb.add(img);
					wb.send(img);
					//pun imaginea la mine pe wb
					wb.setImage(wb.hc * 0x100000000L + fc.getSelectedFile().getName().hashCode(), imgIcon.getImage());
					//o trimit si la ceilalti:
					if (wb.sender != null && wb.sender != null){
						wb.sender.longFileNames.add(fc.getSelectedFile().getAbsolutePath());
						wb.sender.s_send_file("" + fc.getSelectedFile().length(), "0", wb.getGroup(), fc.getSelectedFile().getName());
					}
				}
				wb.remove(futureImage);
			}
		}//if (..button stanga
	}//mouseReleased
}//WBListener