/**
 * User: Administrator
 * @Author: qurtach@intellisoft.ro
 * @Date: May 20, 2002
 * @Time: 10:43:33 AM
 */

package ro.intellisoft.intelliX.UI;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import java.awt.*;

public class MyComboBoxEditor extends BasicComboBoxEditor {
	MyComboBoxEditor() {
		super();
		JTextField oldEditor = editor;
		editor = new JTextField(editor.getText());
		editor.setFont(oldEditor.getFont());
		editor.setBorder(new RoundedBorder(17, -1));
		editor.setOpaque(false);
		editor.setBackground(Color.lightGray);
		editor.setPreferredSize(new Dimension(editor.getWidth(), editor.getFontMetrics(editor.getFont()).getHeight() + 7));
	}
}

