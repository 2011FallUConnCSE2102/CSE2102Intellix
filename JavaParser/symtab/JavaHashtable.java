
package JavaParser.symtab;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;

/*******************************************************************************
 * An extension of the java.util.Hashtable that is used to
 * add some simple looup and type resolution
 ******************************************************************************/
class JavaHashtable extends java.util.Hashtable {
	//==========================================================================
	//==  Class Variables
	//==========================================================================

	/** prevent nested resolutions... */
	private boolean resolving = false;

	private static final int CLASS = 0;
	private static final int INTERFACE = 1;
	private static final int EITHER = 2;


	//==========================================================================
	//==  Methods
	//==========================================================================


	/** Constructor to create a new java hash table */
	JavaHashtable() {
		super();
	}


	/** List the names of all elements in the hashtable */
	void list(IndentingPrintWriter out) {
		Enumeration e = elements();
		while (e.hasMoreElements())
			out.println(((Definition) e.nextElement()).getQualifiedName());
	}


	/** list the names of all elements in the hashtable, but first print
	 *  a title for the section and indent the following lines
	 */
	void listIndented(IndentingPrintWriter out, String title) {
		if (title != null)
			out.println(title);
		out.indent();
		list(out);
		out.dedent();
	}


	/** Write information about each element in the hash table to the report */
	void report(IndentingPrintWriter out) {
		Enumeration e = elements();
		while (e.hasMoreElements())
			((Reportable) e.nextElement()).report(out);
	}

	void report(DefaultMutableTreeNode out) {
		Enumeration e = elements();
		while (e.hasMoreElements()) {
			Reportable reportable = (Reportable) e.nextElement();
			if ((reportable instanceof MethodDef) || (reportable instanceof ClassDef) ||

					(reportable instanceof VariableDef) || (reportable instanceof MultiDef) ||

					(reportable instanceof PackageDef))
				reportable.report(out);
			else
				System.out.println("report def : " + reportable.getClass().getName());
		}
	}


	/** Write information about each element in the hash table to the report,
	 *  but first write a header line for this section and indent
	 */
	void reportIndented(IndentingPrintWriter out, String title) {
		if (title != null)
			out.println(title);
		out.indent();
		report(out);
		out.dedent();
	}

	void reportIndented(DefaultMutableTreeNode out, String title) {

//        if (title != null)
//            out.println(title);
		report(out);
	}


	/** Resolve the types of dummy elements in the hash table */
	void resolveTypes(SymbolTable symbolTable) {
		if (!resolving) {
			resolving = true;
			// walk through each element in the hash table
			Enumeration e = elements();
			while (e.hasMoreElements()) {
				Definition d = (Definition) e.nextElement();

				// if the element is a Dummy class or dummy interface, we
				//   will replace it with the real definition
				if (d instanceof DummyClass) {
					Definition newD;

					// get its package name and look up the class/interace
					String pkg = ((DummyClass) d).getPackage();
					newD = symbolTable.lookupDummy(d);

					// if we found the class/interface,
					//    add a reference to it, and replace the current def
					//    with the one we found
					if (newD != null) {
						newD.addReference(d.getOccurrence());
						remove(d.getName());
						put(d.getName(), newD);
					}
				}

				// otherwise, ask it if it needs resolution
				else
					d.resolveTypes(symbolTable);
			}
		}
	}
}