
package JavaParser.symtab;

import javax.swing.tree.DefaultMutableTreeNode;


/*******************************************************************************
 * Definition of a curly-brace-delimited block in a file.
 ******************************************************************************/
class BlockDef extends ScopedDef {
	//==========================================================================
	//==  Methods
	//==========================================================================


	/** Constructor to set up a block */
	BlockDef(String name, // name of the block (dummy)
			 Occurrence occ, // where it's defined
			 ScopedDef parentScope) {   // which scope owns it
		super(name, occ, parentScope);
	}


	/** Write information about this block to the report */
	public void report(IndentingPrintWriter out) {
		out.println("Unnamed Block: " + getDef());
		reportElements(out);
	}

	public void report(DefaultMutableTreeNode out) {
	}
}