/*

package jeditor;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.File;
import java.io.FileReader;


/**
public class JavaKitTest {

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("need filename argument");
			System.exit(1);
		}
		try {
			JEditorPane editor = new JEditorPane();
			JavaEditorKit kit = new JavaEditorKit();
			editor.setEditorKitForContentType("text/java", kit);
			editor.setContentType("text/java");
			editor.setBackground(Color.white);
			editor.setFont(new Font("Courier", 0, 12));
			editor.setEditable(true);
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

			File file = new File(args[0]);
			editor.read(new FileReader(file), file);
			JScrollPane scroller = new JScrollPane();
			JViewport vp = scroller.getViewport();
			vp.add(editor);
			vp.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);

			JFrame f = new JFrame("JavaEditorKit: " + args[0]);
			f.getContentPane().setLayout(new BorderLayout());
			f.getContentPane().add("Center", scroller);
			f.pack();
			f.setSize(600, 600);
			f.setVisible(true);
			f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}