
package JavaParser;

import JavaParser.symtab.Definition;
import JavaParser.symtab.Occurrence;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Element;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import java.io.FileReader;
import java.io.IOException;

public class TreeClass extends JPanel {
	private JEditorPane editor;
	private static boolean DEBUG = true;
	DefaultMutableTreeNode top;
	String currentFile = null;
	JTree tree;
	JScrollPane treeView;

	public TreeClass(DefaultMutableTreeNode aTop) {
		//super("Classes structure");

		//Create the nodes.
		top = aTop;

		//Create a tree that allows one selection at a time.
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		//Create the scroll pane and add the tree to it.
		treeView = new JScrollPane(tree);
/*
        //Create the editor viewing pane.
        editor = new JEditorPane();
        editor.setEditable(false);
        JScrollPane editorView = new JScrollPane(editor);
*/
/*        editor = new JEditorPane();
	    JavaEditorKit kit = new JavaEditorKit();
	    editor.setEditorKitForContentType("text/java", kit);
	    editor.setContentType("text/java");
	    editor.setBackground(Color.white);
	    editor.setFont(new Font("Courier", 0, 12));
	    editor.setEditable(true);

	    // PENDING(prinz) This should have a customizer and
	    // be serialized.  This is a bogus initialization.
	    JavaContext styles = kit.getStylePreferences();
	    Style s;
	    s = styles.getStyleForScanValue(Token.COMMENT.getScanValue());
	    StyleConstants.setForeground(s, new Color(102, 153, 153));
	    s = styles.getStyleForScanValue(Token.STRINGVAL.getScanValue());
	    StyleConstants.setForeground(s, new Color(102, 153, 102));
	    Color keyword = new Color(102, 102, 255);
	    for (int code = 70; code <= 130; code++) {
		s = styles.getStyleForScanValue(code);
		if (s != null) {
		    StyleConstants.setForeground(s, keyword);
		}
	    }

//	    editor.read(new FileReader(file), file);
        JScrollPane editorView = new JScrollPane(editor);



        //Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setTopComponent(treeView);
        splitPane.setBottomComponent(editorView);

        Dimension minimumSize = new Dimension(200, 150);
        editorView.setMinimumSize(minimumSize);
        treeView.setMinimumSize(minimumSize);
        splitPane.setDividerLocation(200); //XXX: ignored in some releases
                                           //of Swing. bug 4101306
        //workaround for bug 4101306:
        //treeView.setPreferredSize(new Dimension(100, 100));
        splitPane.setDividerSize(2);

        splitPane.setPreferredSize(new Dimension(700, 500));

        //Add the split pane to this frame.
        //getContentPane().
        add(splitPane, BorderLayout.CENTER);
*/
	}

	public void addSelectionListener(TreeSelectionListener tsl) {
		//Listen for when the selection changes.
		tree.addTreeSelectionListener(tsl);
		/*new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                                   tree.getLastSelectedPathComponent();

                if (node == null) return;

                Object nodeInfo = node.getUserObject();
                if (node.isLeaf()) {
                    Definition def = (Definition)node.getUserObject();
                    gotoDefinition(def);
                    if(DEBUG)
                        System.out.println(def.getOccurrence().getLocation());
                }
            }
        });*/
	}

	public JTree getTree() {
		return tree;
	}

	public JScrollPane getTreeView() {
		return treeView;
	}

	void gotoDefinition(Definition definition) {
		if (DEBUG) {
			System.out.println(definition.toString());
			System.out.println("editor goto -> " + definition.getOccurrence().getLocation());
		}
		displayFile(definition.getOccurrence());
	}

	private void displayFile(Occurrence occurrence) {
		int lineNumber = occurrence.getLine();
		String filename = "";
		try {
			filename = occurrence.getFile().getAbsolutePath();
			if (currentFile == null || !currentFile.equals(filename)) {
				currentFile = filename;
				editor.read(new FileReader(filename), filename);
			}
			Element elem = editor.getDocument().getDefaultRootElement().getElement(lineNumber - 1);
			editor.setCaretPosition(elem.getStartOffset());
			editor.requestFocusInWindow();
		} catch (IOException e) {
			System.err.println("Attempted to read a bad URL: " + filename);
		}
	}

	private void createNode(DefaultMutableTreeNode top, String newNode) {
		DefaultMutableTreeNode category = null;

		category = new DefaultMutableTreeNode(newNode);
		top.add(category);
	}

	public DefaultMutableTreeNode getTopNode() {
		return top;
	}

}
