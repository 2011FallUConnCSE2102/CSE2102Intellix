
package JavaParser.symtab;

import javax.swing.tree.DefaultMutableTreeNode;


/*******************************************************************************
 * Definition of a variable in a source file.
 *  This can be member data in class,
 *  a local variable or a method parameter.
 ******************************************************************************/
class VariableDef extends Definition implements TypedDef {
	//==========================================================================
	//==  Class Variables
	//==========================================================================

	/** The type of the variable */
	private Definition type = null;


	//==========================================================================
	//==  Methods
	//==========================================================================


	/** Constructor to create a new variable symbol */
	VariableDef(String name, // the variable's name
				Occurrence occ, // where it was defined
				ClassDef type, // the type of the variable
				ScopedDef parentScope) {   // which scope owns it
		super(name, occ, parentScope);
		this.type = type;
	}


	/** get the type of the variable */
	public Definition getType() {
		return type;
	}


	/** Write information about this variable to the report */
	public void report(IndentingPrintWriter out) {
		// write the variable's name to the report
		out.println(getQualifiedName() + " (Variable)  " + getDef());
		out.indent();

		// write the variable's type to the report
		out.println("Type: " + type.getQualifiedName() + ((!(type instanceof PrimitiveDef) && !(type instanceof DummyClass)) ?
														  (" " + type.getDef()) : ""));

		// list all references to the variable
		listReferences(out);
		out.dedent();
	}

	public void report(DefaultMutableTreeNode out) {
		// write the package name
		DefaultMutableTreeNode def = new DefaultMutableTreeNode(this);
		out.add(def);

	}


	/** Resolve referenced symbols used by this variable */
	void resolveTypes(SymbolTable symbolTable) {
		if (type != null && type instanceof DummyClass) {
			// resolve the type of the variable
			Definition newType = (Definition) symbolTable.lookupDummy(type);
			if (newType != null) {
				newType.addReference(type.getOccurrence());
				type = newType;
			}
		}
		super.resolveTypes(symbolTable);
	}

	public String toString() {
		return type.toString() + " " + getName();//getQualifiedName();
	}
}