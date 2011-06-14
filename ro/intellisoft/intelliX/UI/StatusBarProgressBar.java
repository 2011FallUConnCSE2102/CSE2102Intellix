/**
 * User: Administrator
 * @Author: qurtach@intellisoft.ro
 * @Date: Jul 1, 2002
 * @Time: 4:24:05 PM
 */

package ro.intellisoft.intelliX.UI;

import javax.swing.*;
import java.awt.*;

public class StatusBarProgressBar extends JProgressBar{
	public static final int RESET = -1;
	public static final int INDETERMINABLE = -2;

	public StatusBarProgressBar() {
		super(JProgressBar.HORIZONTAL, 0, 100);
		super.setStringPainted(true);
		super.setString("(no action in progress)");
		//super.setIndeterminate(true);
		super.setBorder(BorderFactory.createLoweredBevelBorder());
		ro.intellisoft.whiteboard.Whiteboard.setExactDimension(this, 135, 20);
	}

	public void setValue(int n) {
		if (n>=0 && n<=100){
			super.setValue(n);
			super.setString(" "+n+"% ");
		} else if (n == RESET){
			super.setIndeterminate(false);
			super.setValue(0);
			super.setString("(no action in progress)");
		} else if (n == INDETERMINABLE){
			super.setString("(unknown time limit)");
			super.setIndeterminate(true);
			super.setValue(0);
		}

	}
}
