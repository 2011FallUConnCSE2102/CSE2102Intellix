
package JavaParser.symtab;


/*******************************************************************************
 * An abstract class representing a symbol that can import packages.
 ******************************************************************************/
abstract class HasImports extends ScopedDef {
	//==========================================================================
	//==  Class Variables
	//==========================================================================

	/** A table of all packages imported by this symbol */
	private JavaHashtable imports;


	//==========================================================================
	//==  Methods
	//==========================================================================


	/** Constructor to set up an object that can have imports */
	HasImports(String name, // name of the symbol
			   Occurrence occ, // where it's defined
			   ScopedDef parentScope) {   // which scope owns it
		super(name, occ, parentScope);
	}


	/** Tell the symbol table that we are done with our imports */
	void closeImports(SymbolTable symbolTable) {
		symbolTable.closeImports();
	}


	/** Ask if this is a toplevel class or not
	 *  This is true if the parentScope is a package
	 */
	boolean isTopLevel() {
		return (getParentScope() instanceof PackageDef);
	}


	/** Tell the symbol table that we need to import some classes */
	void openImports(SymbolTable symbolTable) {
		symbolTable.openImports(imports);
	}


	/** Write information about the imported classes/packages to report */
	void reportImports(IndentingPrintWriter out) {
		if (imports != null) {
			imports.listIndented(out, "Imported classes/packages:");
			out.println();
		}
	}


	/** Resolve any referenced symbols  */
	void resolveTypes(SymbolTable symbolTable) {
		if (imports != null)            // resolve imported classes/packages
			imports.resolveTypes(symbolTable);

		if (isTopLevel())
			openImports(symbolTable); // make them available for lookup
		super.resolveTypes(symbolTable); // resolve class/interface contents
		// closeImports() is done in class resolution
	}


	/** Set the list of imported classes/packages */
	void setImports(JavaHashtable imports) {
		this.imports = imports;
	}
}