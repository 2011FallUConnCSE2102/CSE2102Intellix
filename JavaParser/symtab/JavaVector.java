
package JavaParser.symtab;

import JavaParser.JavaToken;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;

/*******************************************************************************
 * An extended Vector class to provide simple lookup and type resolution
 * methods
 ******************************************************************************/
public class JavaVector extends java.util.Vector {
	//==========================================================================
	//==  Class Variables
	//==========================================================================

	/** prevent nested resolutions... */
	private boolean resolvingRefs = false;
	private boolean resolvingTypes = false;


	//==========================================================================
	//==  Methods
	//==========================================================================


	/** Constructor to create a new Java vector */
	public JavaVector() {
		super();
	}


	/** Add a new element to the vector (used for debugging) */
	public void addElement(Definition o) {
		super.addElement(o);
		if (o == null)
			throw new IllegalArgumentException("null element added to vector");

	}


	/** get an element from the */
	public Definition getElement(String name) {
		Enumeration e = elements();
		while (e.hasMoreElements()) {
			Definition d = (Definition) e.nextElement();
			if (d.getName().equals(name))
				return d;
		}
		return null;
	}


	/** list the names of all elements in the vector */
	void list(IndentingPrintWriter out) {
		Enumeration e = elements();
		while (e.hasMoreElements())
			out.println(((Definition) e.nextElement()).getQualifiedName());
	}


	/** list the names of all elements in the vector, but first print
	 *  a title for the section and indent the following lines
	 */
	void listIndented(IndentingPrintWriter out, String title) {
		if (title != null)
			out.println(title);
		out.indent();
		list(out);
		out.dedent();
	}


	/** Write information about each element in the vector to the report, */
	void report(IndentingPrintWriter out) {
		Enumeration e = elements();
		while (e.hasMoreElements())
			((Reportable) e.nextElement()).report(out);
	}

	void report(DefaultMutableTreeNode out) {
		Enumeration e = elements();
		while (e.hasMoreElements())
			((Reportable) e.nextElement()).report(out);
	}


	/** Write information about each element in the vector to the report,
	 *  but first write a header line for this section and indent
	 */
	void reportIndented(IndentingPrintWriter out, String title) {
		if (title != null)
			out.println(title);
		out.indent();
		report(out);
		out.dedent();
	}


	/** Resolve references that are stored as JavaTokens */
	public void resolveRefs(SymbolTable symbolTable) {
		if (!resolvingRefs) {
			resolvingRefs = true;
			// resolve each element in the list
			Enumeration e = elements();
			while (e.hasMoreElements()) {
				JavaToken t = (JavaToken) e.nextElement();
				Definition d = symbolTable.lookup(t.getText(), t.getParamCount());
				if (d == null)
					d = symbolTable.findPackage(t.getText());
				if (d != null)
					d.addReference(new Occurrence(t.getFile(), t.getLine()));
			}
		}
	}


	/** Resolve the types of dummy elements in the vector */
	public void resolveTypes(SymbolTable symbolTable) {
		if (!resolvingTypes) {
			resolvingTypes = true;
			Enumeration e = elements();
			while (e.hasMoreElements()) {
				Definition d = (Definition) e.nextElement();
				if (d instanceof DummyClass) {
					String pkg = ((DummyClass) d).getPackage();
					Definition newD = symbolTable.lookupDummy(d);
					if (newD != null) {
						newD.addReference(d.getOccurrence());
						removeElement(d);
						addElement(newD);
					}
				} else
					d.resolveTypes(symbolTable);
			}
		}
	}
}