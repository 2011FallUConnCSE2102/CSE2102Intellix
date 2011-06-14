
package ro.intellisoft.intelliX;

import JavaParser.TreeClass;
import JavaParser.symtab.Definition;
import JavaParser.symtab.Occurrence;
import JavaParser.symtab.SymbolTable;
import ro.intellisoft.intelliX.UI.*;
import ro.intellisoft.intelliX.chat.ChatFrame;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * User: Qurtach
 * Date: Mar 20, 2002
 * Time: 6:17:37 PM
 */

public class IntelliX implements ActionListener, ChangeListener {
	private static final String version = "v1.0 b1451 \u03B22 r\u212elease";
	private static final String copyright = "Copyright \u00A9 2002 Intelli Soft - www.intellisoft.ro";
	private JFrame mainFrame = new JFrame("InteliX " + version);
	private IntelliXProperties properties = new IntelliXProperties(this);

	private JMenuBar mainMenuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenuItem openFileMenu = new JMenuItem("Open...", KeyEvent.VK_O);
	private JMenu editMenu = new JMenu("Edit");
	private JMenu viewMenu = new JMenu("View");
	private JMenu toolsMenu = new JMenu("Tools");
	private JMenu helpMenu = new JMenu("Help");
	private JCheckBoxMenuItem navigatorViewMenu = new JCheckBoxMenuItem("File Explorer", true);
	private JCheckBoxMenuItem editorViewMenu = new JCheckBoxMenuItem("Editor", true);
	private JCheckBoxMenuItem chatViewMenu = new JCheckBoxMenuItem("Chat", false);
	private JMenuItem connectHermixMenu = new JMenuItem("Connect to Hermix...", KeyEvent.VK_H);
	private HermixLink hermixLink = null;

	private final JLabel statusBar = new JLabel("Ready...", JLabel.LEFT);
	private final JLabel positionInFile = new JLabel("--:--");
	private final StatusBarProgressBar statusBarProgressBar = new StatusBarProgressBar();

	private DefaultListModel userListModel = new DefaultListModel();
	private JList userList = new JList(userListModel);
	private JScrollPane userListScrollPanel = new JScrollPane(userList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	private final DefaultMutableTreeNode top = new DefaultMutableTreeNode("Project");
	private TreeClass projectTC = new TreeClass(top);
	private DefaultMutableTreeNode lastTreePathClicked = null;
	private final Hashtable editors = new Hashtable();
	private GeneralEditorPane dragSrc = null;
	private int dragPosition = -1;

	private JPanel structureView = new JPanel(new BorderLayout());

	private JTabbedPane navigatorTabbedPane = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);
	private JTabbedPane editorsTabbedPanel = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
	private JTabbedPane chatTabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
	//private JTabbedPane UMLTabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

	//private JSplitPane chatSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatTabbedPane, UMLTabbedPane);
	private JSplitPane editorsSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, navigatorTabbedPane, editorsTabbedPanel);
	private JSplitPane navigatorSplitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorsSplitPanel, chatTabbedPane);//chatSplitPanel);

	private String _hermixServer = "";
	private String _hermixGroup = "guest";
	private boolean connected = false;
	ChatFrame chat;

	private boolean sharedTextContainerMutex = false;
	private Hashtable sharedTextContainer = new Hashtable();
	private GeneralEditorPane oldDisplayedEditor = null;

	private LogoWindow logoWindow = new LogoWindow(this);
	private OpenFilesDialog openFilesDialog = new OpenFilesDialog(this);

	private final BackgroundParser backgroundParser = new BackgroundParser(this, 5000);
	private java.util.Vector alteredFiles = new java.util.Vector();
	private JToolBar t_toolBar = new JToolBar("Standard tools", JToolBar.HORIZONTAL);
	private MyButton t_openButton = new MyButton();
	private MyButton t_saveButton = new MyButton();
	private MyButton t_newButton = new MyButton();
	private MyButton t_exitButton = new MyButton();
	private MyButton t_connectButton = new MyButton();


	public IntelliX(String args[]) {
		mainFrame.setBounds(-800, -600, 700, 500);
		mainFrame.setJMenuBar(mainMenuBar);
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				performCleanUpAndSave();
			}
		});
		mainFrame.show();
		logoWindow.show();
		//TODO: open any file(s) in the args list: add them with properties.addFileOpen(?);
		properties.openFiles(openFilesDialog);

		mainMenuBar.add(fileMenu);
		JMenuItem aMenuItem = new JMenuItem("New...", KeyEvent.VK_N);
		aMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		fileMenu.add(aMenuItem);
		aMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				newFile();
			}
		});

		fileMenu.addSeparator();

		openFileMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		fileMenu.add(openFileMenu);
		openFileMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				openFile();
			}
		});

		aMenuItem = new JMenuItem("Close...", KeyEvent.VK_C);
		aMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.CTRL_MASK));
		fileMenu.add(aMenuItem);
		aMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				closeCurrentSourceFile();
			}
		});

		aMenuItem = new JMenuItem("Save...", KeyEvent.VK_S);
		aMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		aMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				saveFile();
			}
		});
		fileMenu.add(aMenuItem);
		aMenuItem = new JMenuItem("Save as...", KeyEvent.VK_A);
		aMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK));
		aMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				saveAsFile();
			}
		});
		fileMenu.add(aMenuItem);
		fileMenu.addSeparator();

		aMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		aMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK));
		aMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				performCleanUpAndSave();
			}
		});
		fileMenu.add(aMenuItem);

		mainMenuBar.add(editMenu);
		aMenuItem = new JMenuItem("Copy", KeyEvent.VK_C);
		aMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
		aMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GeneralEditorPane editorPane = (GeneralEditorPane) editorsTabbedPanel.getSelectedComponent();
				if (editorPane != null) {
					editorPane.doCopyAction(e.getSource());
				}
			}
		});
		editMenu.add(aMenuItem);

		aMenuItem = new JMenuItem("Cut", KeyEvent.VK_X);
		aMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		aMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GeneralEditorPane editorPane = (GeneralEditorPane) editorsTabbedPanel.getSelectedComponent();
				if (editorPane != null) {
					editorPane.doCutAction(e.getSource());
				}
			}
		});
		editMenu.add(aMenuItem);

		aMenuItem = new JMenuItem("Paste", KeyEvent.VK_V);
		aMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
		aMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GeneralEditorPane editorPane = (GeneralEditorPane) editorsTabbedPanel.getSelectedComponent();
				if (editorPane != null) {
					editorPane.doPasteAction(e.getSource());
				}
			}
		});
		editMenu.add(aMenuItem);

		editMenu.addSeparator();

		aMenuItem = new JMenuItem("Select all", KeyEvent.VK_A);
		aMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		aMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GeneralEditorPane editorPane = (GeneralEditorPane) editorsTabbedPanel.getSelectedComponent();
				if (editorPane != null) {
					editorPane.doSelectAllAction(e.getSource());
				}
			}
		});
		editMenu.add(aMenuItem);

		aMenuItem = new JMenuItem("Go to line...", KeyEvent.VK_G);
		aMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_MASK));
		aMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GeneralEditorPane editorPane = (GeneralEditorPane) editorsTabbedPanel.getSelectedComponent();
				if (editorPane != null) {
					editorPane.doGotoLine();
				}
			}
		});
		editMenu.add(aMenuItem);


		editorsTabbedPanel.addChangeListener(this);

		navigatorViewMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.ALT_MASK));
		editorViewMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.ALT_MASK));
		chatViewMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.ALT_MASK));
		navigatorViewMenu.addActionListener(this);
		editorViewMenu.addActionListener(this);
		chatViewMenu.addActionListener(this);
		viewMenu.add(navigatorViewMenu);
		viewMenu.add(editorViewMenu);
		viewMenu.add(chatViewMenu);
		mainMenuBar.add(viewMenu);
		mainMenuBar.add(toolsMenu);

		connectHermixMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.ALT_MASK));
		connectHermixMenu.addActionListener(this);
		toolsMenu.add(connectHermixMenu);

		navigatorSplitPanel.setDividerSize(2);
		editorsSplitPanel.setDividerSize(4);
		chatTabbedPane.setVisible(false);

		navigatorTabbedPane.addTab("Structure", structureView);

		aMenuItem = new JMenuItem("About", KeyEvent.VK_A);
		aMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		aMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JOptionPane.showMessageDialog(mainFrame, "Copyright @2002 Intelli Soft\nAuthors:\n               Bogdan Paduraru\n               Cristi Buzduga\n               Cristi Nantu Isac\n               Ovidiu Maxiniuc\n", "About IntelliX" + version, JOptionPane.INFORMATION_MESSAGE);
			}
		});
		helpMenu.add(aMenuItem);
		mainMenuBar.add(helpMenu);

		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.getContentPane().setLayout(new BorderLayout());
		mainFrame.getContentPane().add(navigatorSplitPanel, BorderLayout.CENTER);
		mainFrame.getContentPane().add(t_toolBar, BorderLayout.NORTH);
		t_toolBar.setFloatable(false);
		t_toolBar.setRollover(true);
		t_toolBar.add(t_newButton);
		ro.intellisoft.whiteboard.Whiteboard.setExactDimension(t_newButton, 24, 24);
		t_newButton.setToolTipText("Creates and opens a new file");
		t_newButton.setIcon(IntelliX.loadImageResource(t_newButton, "images/newfile.gif"));
		t_newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				newFile();
			}
		});
		t_toolBar.add(t_openButton);
		ro.intellisoft.whiteboard.Whiteboard.setExactDimension(t_openButton, 24, 24);
		t_openButton.setToolTipText("Opens an existing file from your computer");
		t_openButton.setIcon(IntelliX.loadImageResource(t_openButton, "images/openfile.gif"));
		t_openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				openFile();
			}
		});
		t_toolBar.add(t_saveButton);
		ro.intellisoft.whiteboard.Whiteboard.setExactDimension(t_saveButton, 24, 24);
		t_saveButton.setToolTipText("Saves all files opened in editor");
		t_saveButton.setIcon(IntelliX.loadImageResource(t_saveButton, "images/savefile.gif"));
		t_saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				saveFile();
			}
		});
		t_toolBar.addSeparator();
		t_toolBar.add(t_connectButton);
		ro.intellisoft.whiteboard.Whiteboard.setExactDimension(t_connectButton, 24, 24);
		t_connectButton.setToolTipText("Connect/disconnect from a Hermix server");
		t_connectButton.setIcon(IntelliX.loadImageResource(t_connectButton, "images/connected.gif"));
		t_connectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				if (!connected) {
					doConnectToHermix();
				} else {
					doDisconnectFromHermix();
					connectHermixMenu.setText("Connect to Hermix...");
					t_connectButton.setIcon(IntelliX.loadImageResource(t_connectButton, "images/connected.gif"));
					connected = false;
					hermixLink = null;
				}
			}
		});
		t_toolBar.addSeparator();
		t_toolBar.add(t_exitButton);
		ro.intellisoft.whiteboard.Whiteboard.setExactDimension(t_exitButton, 24, 24);
		t_exitButton.setToolTipText("Saves all files and exits");
		t_exitButton.setIcon(IntelliX.loadImageResource(t_exitButton, "images/closewindow.gif"));
		t_exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				performCleanUpAndSave();
			}
		});
		JPanel southPanel_1 = new JPanel(new BorderLayout());
		southPanel_1.add(statusBar, BorderLayout.CENTER);
		JPanel southPanel_2 = new JPanel(new BorderLayout());
		southPanel_1.add(southPanel_2, BorderLayout.EAST);
		southPanel_2.add(statusBarProgressBar, BorderLayout.CENTER);
		statusBarProgressBar.setVisible(false);
		//?
		southPanel_2.add(positionInFile, BorderLayout.EAST);
		positionInFile.setFont(new Font("Courier New", Font.PLAIN, 12));
		ro.intellisoft.whiteboard.Whiteboard.setExactDimension(positionInFile, 60, 20);
		positionInFile.setHorizontalAlignment(JLabel.CENTER);
		positionInFile.setBorder(BorderFactory.createLoweredBevelBorder());
		mainFrame.getContentPane().add(southPanel_1, BorderLayout.SOUTH);
		statusBar.setFont(new Font("Arial", Font.PLAIN, 11));
		userList.setCellRenderer(new UserLabel(this));
		editorsSplitPanel.setDividerLocation(0.25);
		backgroundParser.start();

		//test only:
		userList.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				int index = userList.locationToIndex(me.getPoint());
				if (index == -1) {
					return;
				}
				User gika = hermixLink.getUserByName(userListModel.getElementAt(index).toString());
				if (me.getButton() == MouseEvent.BUTTON3 && me.getClickCount() == 1) {
					if (gika == null) {
						//not on a user
						return;
					}
					gika.showMenu(me.getComponent(), me.getX(), me.getY());
				} else if (me.getButton() == MouseEvent.BUTTON1 && me.getClickCount() == 2) {
					//on double-click will toggle this user status:
					gika.toggleSound();
				}
			}
		});

		if (editorsTabbedPanel.getTabCount() >= 1) {
			((GeneralEditorPane) editorsTabbedPanel.getSelectedComponent()).gainFocus();
		}

		rearangeWindows();
	}//constructor IntelliX

	/**restore the position of the main window*/
	public void restorePosition() {
		mainFrame.setLocation(20, 10);
		if (!openFilesDialog.isEnded()) {
			openFilesDialog.setLocationRelativeTo(mainFrame);
			openFilesDialog.setVisible(true);
		}
		statusBarProgressBar.setVisible(true);
		mainFrame.repaint();
	}

	/**Invoked when the target of the listener has changed its state.*/
	public void stateChanged(ChangeEvent e) {
		if (oldDisplayedEditor != null) {
			oldDisplayedEditor.lostFocus();
		}
		oldDisplayedEditor = (GeneralEditorPane) editorsTabbedPanel.getSelectedComponent();
		if (oldDisplayedEditor != null) {
			oldDisplayedEditor.gainFocus();
		} else {
			//editorsTabbedPane is empty so
			setStructureView(null);
			setCaretPosition(-1, -1);
		}
		t_saveButton.setEnabled(editorsTabbedPanel.getTabCount() != 0); //cum draku imi dau seama ca nu mai sunt taburi?
	}

	public void updateUserList() {
		userList.repaint();
	}

	public JFrame getMainFrame() {
		return mainFrame;
	}

	/**returns a reference to the progress bar of the main window*/
	public StatusBarProgressBar getProgressBar() {
		return statusBarProgressBar;
	}

	private File getPackagePathFor(DefaultMutableTreeNode node) {
		if (node.getChildCount() == 0) {
			//f**k where are the files?
			return null;
		}
		for (int i = 0; i < node.getChildCount(); i++) {
			Object objCrt = ((DefaultMutableTreeNode) node.getChildAt(i)).getUserObject();
			if (objCrt.getClass().getName().equals("JavaParser.symtab.ClassDef")) {
				//found a file, return path and exit
				return ((Definition) objCrt).getOccurrence().getFile().getParentFile();
			}
		}
		//if we reached this point then crt project has only subpackages!
		//advance to the first child and search again
		File fileToReturn = getPackagePathFor((DefaultMutableTreeNode) node.getChildAt(0));
		return fileToReturn == null ? null : fileToReturn.getParentFile();
	}

	public void updateClassNavigationTree() {
		for (int i = 0; i < alteredFiles.size(); i++) {
			//get the file
			GeneralEditorPane f = (GeneralEditorPane) alteredFiles.elementAt(0);
			//pop it from container
			alteredFiles.removeElementAt(0);
			f.save();
		}
	}

	public void setIconForEditor(GeneralEditorPane editor, String icon) {
		try {
			editorsTabbedPanel.setIconAt(editorsTabbedPanel.indexOfComponent(editor), loadImageResource(logoWindow, icon));
		} catch (java.lang.ArrayIndexOutOfBoundsException boundsException) {/*do nothing, editor is not in the tabbed pane*/
		}
	}

	private void displayFile(Occurrence occurrence) {
		if (occurrence == null) {
			return;
		}
		GeneralEditorPane newEditor = null;
		String filename = occurrence.getFile().getAbsolutePath();
		if (!editors.containsKey(filename)) {
			//build and setup editor
			String fileType = GeneralEditorPane.getTypeByExtension(filename);
			newEditor = new GeneralEditorPane(this, fileType, filename);
			editors.put(filename, newEditor);
			if (newEditor.getFileType().equals("java")) {
				editorsTabbedPanel.addTab(occurrence.getFile().getName(), loadImageResource(logoWindow, "images/javafileicon.gif"), newEditor);
			} else {
				editorsTabbedPanel.addTab(occurrence.getFile().getName(), newEditor);
			}
		} else {
			newEditor = (GeneralEditorPane) editors.get(filename);
		}
		editorsTabbedPanel.setSelectedComponent(newEditor);
		newEditor.setCaretLine(occurrence.getLine() - 1);
		newEditor.requestFocusInWindow();
	}

	public void addMouseListenersAndRenderer(JTree aTree) {
		aTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) ((JTree) e.getSource()).getLastSelectedPathComponent();
				if (node == null || e.getClickCount() != 2) {
					return;
				}
				if (node.isLeaf()) {
					if (node.getUserObject() instanceof Definition) {
						Definition def = (Definition) node.getUserObject();
						displayFile(def.getOccurrence());
					}
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					//this means right-click
					int selRow = ((JTree) e.getSource()).getRowForLocation(e.getX(), e.getY());
					TreePath selPath = ((JTree) e.getSource()).getPathForLocation(e.getX(), e.getY());
					if ((selRow != -1) && (e.getClickCount() == 1)) {
						//pops-up a right-click menu:
						JPopupMenu aMenu = new JPopupMenu();
						if (selPath.getParentPath() == null) {
							return;
							//because project is disabled:
							// aMenu = project.getProjectMenu();
						} else {
							DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
							lastTreePathClicked = node;
							String objectToPutMenuOn = node.getUserObject().getClass().getName();
							if (objectToPutMenuOn.equals("JavaParser.symtab.ClassDef")) {
								//a file
								//aMenu = properties.getFileMenu();
							} else if (objectToPutMenuOn.equals("java.lang.String") && (node.getChildCount() != 0)) {
								//aMenu = properties.getPackageMenu();
							} else {
								//method/field/link=reference
								JMenuItem gotoMenuItem = new JMenuItem("Go to reference");
								gotoMenuItem.addActionListener(new ActionListener() {
									/**Invoked when an action occurs.*/
									public void actionPerformed(ActionEvent e) {
										Definition def = (Definition) lastTreePathClicked.getUserObject();
										displayFile(def.getOccurrence());
									}
								});
								aMenu.add(gotoMenuItem);
							}
						}
						aMenu.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		});
		aTree.setCellRenderer(new DefaultTreeCellRenderer() {
			public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
				super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				String imageName = null;
				String objectToPaint = ((DefaultMutableTreeNode) value).getUserObject().getClass().getName();
				if (objectToPaint.equals("JavaParser.symtab.ClassDef")) {
					imageName = "javafileicon";
				} else if (objectToPaint.equals("JavaParser.symtab.MethodDef")) {
					String parentJavaClass = ((DefaultMutableTreeNode) ((DefaultMutableTreeNode) value).getParent()).getUserObject().toString();
					if (value.toString().startsWith(parentJavaClass) && value.toString().indexOf('(') == parentJavaClass.length()) {
						imageName = "constructoricon";
					} else {
						imageName = "methodicon";
					}
				} else if (row == 0) {//((DefaultMutableTreeNode) value).getUserObject().equals("Project")) {// the project must always be on the first row == 0 ???
					imageName = "javacupicon";
				} else if (objectToPaint.equals("java.lang.String")) {//"JavaParser.symtab.PackageDef")) {
					//there are no more such things
					if (((DefaultMutableTreeNode) value).getChildCount() == 0) {
						imageName = "referenceicon";
					} else {
						imageName = "packageicon";
					}
				} else if (objectToPaint.equals("JavaParser.symtab.VariableDef")) {
					imageName = "fieldicon";
				} else {
					imageName = "referenceicon";
				}
				//other icons...
				this.setIcon(IntelliX.loadImageResource(this, "images/" + imageName + ".gif"));
				return this;
			}
		});
	}//addMouseListenersAndRenderer

	void addEditorPane(final GeneralEditorPane newEditor) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//String file = new File(newEditor.getAbsoluteFileName()).getCanonicalPath();
				//if (!containsFile(editors.keys(), new File(newEditor.getAbsoluteFileName()))) {
				if (!editors.containsKey(newEditor.getAbsoluteFileName())) {
					//build and setup editor
					editors.put(newEditor.getAbsoluteFileName(), newEditor);
					if (newEditor.getFileType().equals("java")) {
						editorsTabbedPanel.addTab(newEditor.getFileName(), loadImageResource(logoWindow, "images/javafileicon.gif"), newEditor);
					} else {
						editorsTabbedPanel.addTab(newEditor.getFileName(), newEditor);
					}
					editorsTabbedPanel.setSelectedComponent(newEditor);
					editorsTabbedPanel.setToolTipTextAt(editorsTabbedPanel.getSelectedIndex(), newEditor.getAbsoluteFileName());
					newEditor.requestFocusInWindow();
				} else {
					int res = JOptionPane.showConfirmDialog(getMainFrame(), newEditor.getFileName() + " is already open.\nDo you want to reload the file?", "File already open!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (res == JOptionPane.YES_OPTION) {
						GeneralEditorPane gep = (GeneralEditorPane) editors.put(newEditor.getAbsoluteFileName(), newEditor);
						if (gep != null) {
							editorsTabbedPanel.remove(gep);
						} //or else??
						editorsTabbedPanel.addTab(newEditor.getFileName(), newEditor);
						editorsTabbedPanel.setSelectedComponent(newEditor);
						newEditor.requestFocusInWindow();
					}
				}
				newEditor.requestFocusInWindow();
			}
		});

	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == editorViewMenu || ae.getSource() == navigatorViewMenu || ae.getSource() == chatViewMenu) {
			rearangeWindows();
		} else if (ae.getSource() == connectHermixMenu) {
			if (!connected) {
				doConnectToHermix();
			} else {
				doDisconnectFromHermix();
				t_connectButton.setIcon(IntelliX.loadImageResource(t_connectButton, "images/connected.gif"));
				connectHermixMenu.setText("Connect to Hermix...");
				connected = false;
				hermixLink = null;
			}
		}
	}

	private void rearangeWindows() {
		editorsTabbedPanel.setVisible(editorViewMenu.isSelected());
		navigatorTabbedPane.setVisible(navigatorViewMenu.isSelected());
		chatTabbedPane.setVisible(chatViewMenu.isSelected());
		editorsTabbedPanel.validate();
	}

	private boolean doConnectToHermix() {
		ServerChooser serverChooser = new ServerChooser(getMainFrame(), properties.hermixServers, properties.hermixPorts, properties.userInfo);
		serverChooser.show();
		if (serverChooser.isValid()) {
			_hermixServer = serverChooser.getServerName();
			properties.addHermixServer(_hermixServer);
			properties.updateUserInfo(new String[]{serverChooser.getNick(), serverChooser.getPassword(), serverChooser.getDescription(), serverChooser.getRealName()});
			userListModel.clear();
			hermixLink = new HermixLink(this);
			hermixLink.s_connect(_hermixServer, serverChooser.getServerPort(), serverChooser.getNick(), serverChooser.getPassword(), serverChooser.getRealName(), serverChooser.getDescription());
			log("Connecting to " + _hermixServer + " hermix server...");
			return true;
		} else {
			return false;
		}
	}

	private void doDisconnectFromHermix() {
		while (sharedTextContainerMutex) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException ie) {/*do nothing*/
			}
		}
		Enumeration keys = editors.keys();
		while (keys.hasMoreElements()) {
			Object aKey = keys.nextElement();
			GeneralEditorPane editorPane = (GeneralEditorPane) editors.get(aKey);
			editorPane.closeShare();
		}

		sharedTextContainerMutex = true;
		//TODO: toate ferestrele pentru care sunt moderator trebuie inchise
		sharedTextContainer.clear();
		sharedTextContainerMutex = false;
		chatTabbedPane.removeAll();
		chatTabbedPane.setVisible(false);
		//chatSplitPanel.setVisible(false);
		//chatSplitPanel.setDividerLocation(0.0);
		navigatorSplitPanel.setDividerLocation(0.0);
		navigatorTabbedPane.remove(userListScrollPanel);
		hermixLink.s_disconnect();
	}

	public void r_connected(String newNick, String rights) {
		if (rights == null || rights.equals("")) {
			rights = "normal";
		}
		System.out.println("Connected to Hermix server using " + newNick + " nick and having " + rights + " rights");
		log("Connected to Hermix server using " + newNick + " nick.");
		chatTabbedPane.setVisible(true);
		//chatSplitPanel.setVisible(true);
		//chatSplitPanel.setDividerLocation(0.0);
		navigatorSplitPanel.setDividerLocation(0.75);
		navigatorTabbedPane.addTab("Hermix", null, userListScrollPanel, "The list of users connected to Hermix server");
		navigatorTabbedPane.setSelectedComponent(userListScrollPanel);
		hermixLink.s_join_group(_hermixGroup);
		chat = new ChatFrame(this);
		chatTabbedPane.addTab("Chat", chat);
		//updates the label from the connect menu
		this.connectHermixMenu.setText("Disconnect from Hermix");
		t_connectButton.setIcon(IntelliX.loadImageResource(t_connectButton, "images/disconnected.gif"));
		connected = true;
	}

	/**Method call when a new user enters the group*/
	public void r_insert_user(String nick) {
		if (!userListModel.contains(nick)) {
			userListModel.addElement(nick);
			log(nick + " entered " + _hermixGroup + " group");
		}
	}

	public void r_change_nick(String newNick, String oldNick) {
		if (userListModel.contains(oldNick)) {
			userListModel.removeElement(oldNick);
			userListModel.addElement(newNick);
		}
		if (hermixLink.getNick().equals(newNick)) {
			properties.userInfo[0] = newNick;
		}
	}

	public void r_wake_up(String nick) {
		chat.wakeup(nick);
	}

	public void r_remove_user(String nick) {
		if (userListModel.contains(nick)) {
			userListModel.removeElement(nick);
			log(nick + " left " + _hermixGroup + " group");
		}
	}

	public void joinNewTextShare(String privateGroup, String owner) {
		GeneralEditorPane newEditor = new GeneralEditorPane(this, "txt");
		newEditor.setReadOnly(true);
		hermixLink.registerShareProcessor(privateGroup, newEditor.createShareProcessor(privateGroup, owner));
		int res = JOptionPane.showConfirmDialog(getMainFrame(), owner + " wants help and has opened a test share.\nDo you want to join and help him?", "Question", JOptionPane.YES_NO_OPTION);
		if (res == JOptionPane.NO_OPTION) {
			hermixLink.s_leave_group(privateGroup);
			hermixLink.unregisterShareProcessor(privateGroup);
		} else {
			//ad it to the others'
			addEditorPane(newEditor);
		}
	}

	public void r_join_group(String groupname) {
		log("Joined chat group " + groupname);
		hermixLink.s_get_users_from_group(_hermixGroup);
	}

	public void g_receive_text(String nick, String text) {
		chat.processChatMessage(text.trim(), nick);
	}

	/**Method call by API when a communication error appers*/
	public void r_communication_error(String errMsg, int code) {
		if (errMsg != null)
			log("SYS: (" + code + ") " + errMsg + "<BR>");
		else
			log("SYS: Error: " + code + ". No more information supplied<BR>");
	}//end  r_communication_error


	public void setCaretPosition(int line, int offset) {
		if (line < 0 || offset < 0) {
			positionInFile.setText("--:--");
		} else {
			positionInFile.setText(line + ":" + offset);
		}
	}

	public void log(final String aMessage) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				statusBar.setText(aMessage);
			}
		});
	}

	/**
	 * returns the computed symboltable for the specified file.
	 * May return null if file has not been parsed yet
	 */
	public SymbolTable getSymbolTableFor(String filename) {
		return (SymbolTable) properties.getSymbolTables().get(filename);
	}

	public void setStructureView(JScrollPane structureViewer) {
		structureView.removeAll();
		if (structureViewer == null) {
			structureView.add(new JPanel(), BorderLayout.CENTER);
		} else {
			structureView.add(structureViewer, BorderLayout.CENTER);
		}
		navigatorTabbedPane.repaint();
	}

	public HermixLink getHermixLink() {
		return hermixLink;
	}

	public static final String getVersion() {
		return version;
	}

	public static final String getCopyright() {
		return copyright;
	}

	public IntelliXProperties getProperties() {
		return properties;
	}

	public GeneralEditorPane getCurrentDragSource() {
		return dragSrc;
	}

	public void setCurrentDragSource(GeneralEditorPane dragSrc) {
		this.dragSrc = dragSrc;
	}

	public int getDragPosition() {
		return dragPosition;
	}

	public void setDragPosition(int dragPosition) {
		this.dragPosition = dragPosition;
	}

	public void saveAsFile() {
		if (editorsTabbedPanel.getTabCount() == 0) {
			log("Nothing to save...");
			return;
		}
		GeneralEditorPane crtEditor = (GeneralEditorPane) editorsTabbedPanel.getSelectedComponent();
		String newName = saveAsFile(crtEditor);
		int idx = editorsTabbedPanel.getSelectedIndex();
		String filename = crtEditor.getAbsoluteFileName();
		if (newName != null) {
			if (editors.containsKey(newName)) {
				editorsSplitPanel.remove((GeneralEditorPane) editors.remove(newName));
			}
			//updateEditorTab(crtEditor):
			editors.remove(filename);
			JComponent oldComponent = (JComponent) editors.put(newName, crtEditor);
			//inlocuieste tab-ul in editor tabbed pane
			editorsTabbedPanel.setTitleAt(idx, new File(newName).getName());
			editorsTabbedPanel.setToolTipTextAt(idx, newName);
			if (oldComponent != null) {
				editorsTabbedPanel.remove(oldComponent);
			}
			crtEditor.gainFocus();
			log(newName + " successfull saved...");
		}
	}

	public void saveFile() {
		if (editorsTabbedPanel.getTabCount() == 0) {
			log("Nothing to save...");
			return;
		}
		GeneralEditorPane crtEditor = (GeneralEditorPane) editorsTabbedPanel.getSelectedComponent();
		int idx = editorsTabbedPanel.getSelectedIndex();
		String filename = crtEditor.getAbsoluteFileName();

		if (!crtEditor.isEverSaved()) {
			String newName = saveAsFile(crtEditor);
			if (newName != null) {
				//updateEditorTab(crtEditor);
				editors.remove(filename);
				JComponent oldComponent = (JComponent) editors.put(newName, crtEditor);
				//inlocuieste tab-ul in editor tabbed pane
				editorsTabbedPanel.setTitleAt(idx, new File(newName).getName());
				editorsTabbedPanel.setToolTipTextAt(idx, newName);
				if (oldComponent != null) {
					editorsTabbedPanel.remove(oldComponent);
				}
				crtEditor.gainFocus();
				log(newName + " successfull saved...");
			}
		} else {
			if (crtEditor.isNotSaved()) {
				crtEditor.save();
				log(crtEditor.getAbsoluteFileName() + " successfull saved...");
			} else {
				log(crtEditor.getAbsoluteFileName() + " already saved...");
			}

		}
	}

	/**called when user closes the main window, AltX or Menu/Exit*/
	void performCleanUpAndSave() {
		System.out.println("Closing event catched...");
		backgroundParser.stopThread();
		if (connected)
			doDisconnectFromHermix();
		//updates open files
		properties.removeAllOpenFiles();
		Enumeration openFiles = editors.keys();
		while (openFiles.hasMoreElements()) {
			String filename = openFiles.nextElement().toString();
			GeneralEditorPane newEditor = (GeneralEditorPane) editors.get(filename);
			properties.addFileOpen(filename);
		}
		properties.save();
		System.exit(0);
	}

	/**create new file*/
	public void newFile() {
		//it should ask for some details about this file, perhaps a wizard
		//build and setup editor
		String defaultType = "txt";
		String res = JOptionPane.showInputDialog(this.getMainFrame(), "Please enter a new filename:", "New file dialog", JOptionPane.QUESTION_MESSAGE);
		if (res == null || res.equals("")) {
			return;//empty filename???
		}
		GeneralEditorPane newEditor = new GeneralEditorPane(this, GeneralEditorPane.getTypeByExtension(res), res);
		//add it to the others'
		addEditorPane(newEditor);
	}

	/**called to close current open file (Ctrl+F4)*/
	public boolean closeCurrentSourceFile() {
		if (editors.isEmpty()) {
			//nothing to close
			return true;
		}
		GeneralEditorPane crtEditor = (GeneralEditorPane) editorsTabbedPanel.getSelectedComponent();
		if (crtEditor == null) {
			return true;
		}
		crtEditor.closeAllBookmarksWindows();
		crtEditor.forwardWriteRights();
		String filenameToClose = crtEditor.getAbsoluteFileName();

		if (!crtEditor.isEverSaved()) {
			if (JOptionPane.showConfirmDialog(this.mainFrame, "File not saved.\nSave it now?", "I/O Question", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
				//new, unsaved file:
				if (saveAsFile(crtEditor) == null) {
					//user canceled operation
					return false;
				}
			}
		} else if (crtEditor.isNotSaved()) {
			if (JOptionPane.showConfirmDialog(this.mainFrame, "File not saved.\nSave it now?", "I/O Question", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
				crtEditor.save();
			}
		}
		//save if needed then remove from list of open files
		properties.removeFileOpen(filenameToClose);
		//editorsTabbedPane.removeTabAt(editorsTabbedPane.getSelectedIndex());
		editorsTabbedPanel.remove(crtEditor);
		editors.remove(filenameToClose);
		return true;
	}

	public void addAlteredFile(GeneralEditorPane f) {
		if (!alteredFiles.contains(f)) {
			alteredFiles.addElement(f);
			backgroundParser.arm();
		}
	}

	/**updates the tab for the editor component*/
	public void updateEditorTab(GeneralEditorPane editor) {
		if (editors.containsKey(editor.getAbsoluteFileName())) {
			JOptionPane.showMessageDialog(getMainFrame(), "There was a file with the same name already open.\nOld content is lost.", "Information", JOptionPane.INFORMATION_MESSAGE);
			for (int i = 0; i < editorsTabbedPanel.getTabCount(); i++) {
				if (((GeneralEditorPane) editorsTabbedPanel.getComponentAt(i)).getAbsoluteFileName().equals(editor.getAbsoluteFileName()) && editorsTabbedPanel.getComponentAt(i) != editor) {
					editorsTabbedPanel.remove(i);
					break;
				}
			}
		}

		Enumeration keys = editors.keys();
		while (keys.hasMoreElements()) {
			Object filename = keys.nextElement();
			if (editors.get(filename) == editor) {
				editors.remove(filename);
				editors.put(editor.getAbsoluteFileName(), editor);
				break;
			}
		}

		for (int i = 0; i < editorsTabbedPanel.getTabCount(); i++) {
			if (editorsTabbedPanel.getComponentAt(i) == editor) {
				editorsTabbedPanel.setTitleAt(i, editor.getFileName());
				editorsTabbedPanel.setToolTipTextAt(i, editor.getAbsoluteFileName());
				return;
			}
		}
		System.out.println(editor + " not found!");
	}


	/**
	 * Ask the user for a new name for the file from this editor
	 * The user may save to a file or ignore (the editor's content is lost)
	 * @returns the new file name if operation succeded saved or
	 * null if user changed his mind and another processing is needed.
	 */
	public String saveAsFile(GeneralEditorPane editor) {
		File choice = chooseKnownFileType(JFileChooser.SAVE_DIALOG, "Save new file to...", ".");
		if (choice != null) {
			String filePath = choice.getAbsolutePath();
			try {
				filePath = choice.getCanonicalPath();
			} catch (IOException e) {
			}//do nothing, filePath remains at the old value
			if (choice.exists()) {
				int res = JOptionPane.showConfirmDialog(this.mainFrame, "File already exists.\nOverwrite?", "Question", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (res == JOptionPane.YES_OPTION) {
					editor.saveAs(filePath);
					return filePath;
				} else if (res == JOptionPane.NO_OPTION) {
					//recursely call to choose another name:
					return saveAsFile(editor);
				} else {
					return null;
				}

			} else {
				editor.saveAs(filePath);
				return filePath;
			}
		} else {
			return null;
		}

	}

	/**Opens a file in the editor. Do not add it to project*/
	public void openFile() {
		File choice = chooseKnownFileType(JFileChooser.OPEN_DIALOG, "Open an existing file ...", ".");
		if (choice != null) {
			String filename = choice.getAbsolutePath();
			try {
				filename = choice.getCanonicalPath();
			} catch (IOException e) {
			}
			final String absoluteFilename = filename;
			new Thread() {
				public void run() {
					log("Opening " + absoluteFilename);
					openFileMenu.setEnabled(false);
					t_openButton.setEnabled(false);
					statusBarProgressBar.setValue(StatusBarProgressBar.INDETERMINABLE);
					GeneralEditorPane editor = null;
					editor = new GeneralEditorPane(getThis(), GeneralEditorPane.getTypeByExtension(absoluteFilename), absoluteFilename);
					addEditorPane(editor);
					statusBarProgressBar.setValue(StatusBarProgressBar.RESET);
					log(absoluteFilename + " successfully opened...");
					openFileMenu.setEnabled(true);
					t_openButton.setEnabled(true);
				}
			}.start();
		} else {
			log("Open process aborted");
		}
	}

	public final IntelliX getThis() {
		return this;
	}

	public static ImageIcon loadImageResource(Component comp, String imageName) {
		try {
			java.net.URLClassLoader urlClassLoader = (java.net.URLClassLoader) comp.getClass().getClassLoader();
			java.net.URL url = urlClassLoader.findResource(imageName);
			if (url != null) {
				return new ImageIcon(comp.getToolkit().createImage(url));
			}
		} catch (Exception e) {
		}
		System.out.println("File not found:" + imageName);
		return null;
	}

	public File chooseKnownFileType(int dialogType, String title, String initFile) {
		JFileChooser chooser = new JFileChooser(initFile);
		chooser.setDialogType(dialogType);
		chooser.setDialogTitle(title);
		chooser.addChoosableFileFilter(new FileFilter() {
			public boolean accept(java.io.File f) {
				return f.getName().toLowerCase().endsWith(".htm") || f.getName().toLowerCase().endsWith(".html") || f.isDirectory();
			}

			public String getDescription() {
				return "HyperText files (*.htm; *.html)";
			}
		});

		chooser.addChoosableFileFilter(new FileFilter() {
			public boolean accept(java.io.File f) {
				return f.getName().toLowerCase().endsWith(".txt") || f.isDirectory();
			}

			public String getDescription() {
				return "Text files (*.txt)";
			}
		});

		chooser.addChoosableFileFilter(new FileFilter() {
			public boolean accept(java.io.File f) {
				return f.getName().toLowerCase().endsWith(".xml") || f.isDirectory();
			}

			public String getDescription() {
				return "XML files (*.xml)";
			}
		});

		chooser.addChoosableFileFilter(new FileFilter() {
			public boolean accept(java.io.File f) {
				return f.getName().endsWith(".java") || f.isDirectory();
			}

			public String getDescription() {
				return "Java files (*.java)";
			}
		});

		int res = -1;
		if (dialogType == JFileChooser.OPEN_DIALOG) {
			res = chooser.showOpenDialog(mainFrame);
		} else {
			res = chooser.showSaveDialog(mainFrame);
		}
		if (res == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		} else {
			return null;
		}
	}

}

