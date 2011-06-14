
package JavaParser.symtab;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;

/*******************************************************************************
 * An occurrence of an indentifier in a file
 ******************************************************************************/
public class Occurrence implements Reportable {
	//==========================================================================
	//==  Class Variables
	//==========================================================================

	/** The file containing the occurrence */
	private File file;

	/** The line number containing the occurrence */
	private int line;


	//==========================================================================
	//==  Methods
	//==========================================================================


	/** Constructor to define a new occurrence */
	Occurrence(File file, int line) {
		this.file = file;
		this.line = line;
	}


	/** return a string representation of the occurrence */
	public String getLocation() {
		return "[" + file + ":" + line + "]";
	}

	public File getFile() {
		return file;
	}

	public int getLine() {
		return line;
	}

	/** Write information about this location to the report */
	public void report(IndentingPrintWriter out) {
		out.println("File: " + file.getAbsolutePath() + "  Line: " + line);
	}

	public void report(DefaultMutableTreeNode out) {
	}

	/** return a string representation of the occurrence */
	public String toString() {
		return "Occurrence [" + file + "," + line + "]";
	}
}