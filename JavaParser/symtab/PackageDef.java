
package JavaParser.symtab;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.StringTokenizer;

/*******************************************************************************
 * Definition of a package.
 ******************************************************************************/
class PackageDef extends ScopedDef {
	//==========================================================================
	//==  Methods
	//==========================================================================


	/** Constructor to create a package object */
	PackageDef(String name, // the name of the package
			   Occurrence occ, // where it was defined (NULL)
			   ScopedDef parentScope) {    // which scope owns it
		super(name, occ, parentScope);
	}


	/** Write information about this package to the report */
	public void report(IndentingPrintWriter out) {
		// write the package name
		out.println("Package: " + getQualifiedName());
		out.println("-----------------------------------------");
		out.indent();

		// list all references to this package (in import statements)
		listReferences(out);

		// if we found any definitions in this package, report them
		if (hasElements())
			reportElements(out);
		else
			out.println("(No definitions parsed)");
		out.dedent();
		out.println();
	}

	public void report(DefaultMutableTreeNode out, int oldImplementation) {
		// write the package name
		DefaultMutableTreeNode packageDef = new DefaultMutableTreeNode(this);//getQualifiedName());
		out.add(packageDef);

		// list all references to this package (in import statements)
//        listReferences(out);

		// if we found any definitions in this package, report them
		if (hasElements())
			reportElements(packageDef);
//        else
//            out.println("(No definitions parsed)");
	}

	/**
	 * Reports a package to a tree
	 * @author qurtach@intellisoft.ro
	 * What it does: check if a package already exists and handle these 2 cases in different way
	 * uses sub-packages
	 */
	public void report(DefaultMutableTreeNode out) {
		if (!this.hasElements()) {
			//nothing to do, empty packet =?= just a reference
			return;
		} else if (this.toString().equals("~default~")) {
			//classes without a package definition go to the root of the project
			reportElements(out);
			return;
		}

		String crtPackage = this.toString();
		StringTokenizer st = new StringTokenizer(crtPackage, ".", false);

		DefaultMutableTreeNode lastLevel = out;
		while (st.hasMoreTokens()) {
			String packChunk = st.nextToken();
			boolean found = false;
			for (int i = 0; i < lastLevel.getChildCount(); i++) {
				if (((DefaultMutableTreeNode) lastLevel.getChildAt(i)).getUserObject().equals(packChunk)) {
					//found a prefix
					found = true;
					lastLevel = (DefaultMutableTreeNode) lastLevel.getChildAt(i);
					break;
				}
			}
			if (!found) {
				//create it now!
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(packChunk);
				lastLevel.add(newNode);
				lastLevel = newNode;
			}

		}

		DefaultMutableTreeNode packageDef = lastLevel;
		//out.add(packageDef);

		if (hasElements()) {
			reportElements(packageDef);
		}
	}

}