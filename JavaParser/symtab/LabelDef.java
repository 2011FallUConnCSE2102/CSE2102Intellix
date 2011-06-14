
package JavaParser.symtab;

import javax.swing.tree.DefaultMutableTreeNode;


/*******************************************************************************
 * A label that appears in the source file.
 ******************************************************************************/
class LabelDef extends Definition {
	//==========================================================================
	//==  Methods
	//==========================================================================


	/** Constructor to create a new label symbol */
	LabelDef(String name, // name of the label
			 Occurrence occ, // where it was defined
			 ScopedDef parentScope) {   // scope containing the def
		super(name, occ, parentScope);
	}


	/** Write information about the label to the report */
	public void report(IndentingPrintWriter out) {
		// state that this is a label
		out.println(getQualifiedName() + " (Label) " + getDef());

		// list all references to this label
		listReferences(out);
	}

	public void report(DefaultMutableTreeNode out) {
	}
}