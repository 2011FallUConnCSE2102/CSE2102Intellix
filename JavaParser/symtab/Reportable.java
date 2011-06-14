
package JavaParser.symtab;

import javax.swing.tree.DefaultMutableTreeNode;


/*******************************************************************************
 * This interface is used as a handle to all classes that can be reported
 ******************************************************************************/
interface Reportable {

	/** Write information about an object to the cross-reference report */
	void report(IndentingPrintWriter out);

	void report(DefaultMutableTreeNode out);
}