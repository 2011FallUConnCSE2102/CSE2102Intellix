
package JavaParser.symtab;


import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;

/*******************************************************************************
 * A definition of a method in a class
 ******************************************************************************/
class MethodDef extends ScopedDef implements TypedDef {
	//==========================================================================
	//==  Class Variables
	//==========================================================================

	/** The return type of the method */
	private Definition type = null;

	/** A list of formal parameters to the method */
	private JavaVector parameters;

	/** A list of exceptions that can be thrown */
	private JavaVector exceptions;


	//==========================================================================
	//==  Methods
	//==========================================================================


	/** Constructor to create a method definition object */
	MethodDef(String name, // the name of the method
			  Occurrence occ, // where it was defined
			  ClassDef type, // the return type of the method
			  ScopedDef parentScope) {   // which scope owns it
		super(name, occ, parentScope);
		this.type = type;
	}


	/** Add a thrown exception to the method's exception list */
	void add(ClassDef excep) {
		if (exceptions == null) // lazy instantiation
			exceptions = new JavaVector();
		exceptions.addElement(excep);
	}


	/** Add a parameter to the method's parameter list */
	void add(VariableDef param) {
		if (parameters == null) // lazy instantiation
			parameters = new JavaVector();
		parameters.addElement(param);
	}


	/** Find out how many parameters this method has */
	int getParamCount() {
		if (parameters == null)
			return 0;
		return parameters.size();
	}


	/** Return the return type of the method */
	public Definition getType() {
		return type;
	}


	/** lookup the name as a local variable or local class in this class */
	Definition lookup(String name, int numParams) {
		if (numParams == -1) {
			// look for it in the method's scope
			Definition d = super.lookup(name, numParams);
			if (d != null) return d;

			// otherwise, look in the parameters for the method
			if (parameters != null) {
				Enumeration e = parameters.elements();
				while (e.hasMoreElements()) {
					d = (Definition) e.nextElement();
					if (d.getName().equals(name))
						return d;
				}
			}
		}
		return null;
	}


	/** Write information about this method to the report */
	public void report(IndentingPrintWriter out) {
		// Write the method's name
		out.println(getQualifiedName() + " (Method) " + getDef());
		out.indent();

		// list all references to the method
		listReferences(out);

		// if it has a return type, report it
		if (type != null)
			out.println("Return type: " + type.getQualifiedName() + " " + type.getDef());

		// if it has paramaters, list them
		if (parameters != null)
			parameters.reportIndented(out, "Parameters:");

		// if it throws exceptions, list them
		if (exceptions != null)
			exceptions.reportIndented(out, "Thrown exceptions:");

		// report any variables or classes defined within this method
		reportElements(out);

		out.dedent();
	}

	public void report(DefaultMutableTreeNode out) {
		// Write the method's name
		String methodName = getQualifiedName();
		String returnType = null;
		// if it has paramaters, list them
//        if (parameters != null)
//            parameters.reportIndented(out,"Parameters:");
		// if it has a return type, report it
		if (type != null)
			returnType = type.getQualifiedName() + " " + type.getDef();

		DefaultMutableTreeNode methodNameNode = new DefaultMutableTreeNode(this);
		//methodName+"():"+(returnType==null?"":returnType));
		out.add(methodNameNode);

		// list all references to the method
		//listReferences(out);


		// if it throws exceptions, list them
//        if (exceptions != null)
//            exceptions.reportIndented(out,"Thrown exceptions:");

		// report any variables or classes defined within this method
		//reportElements(out);
	}


	/** Resolve references to other symbols for pass 2 */
	void resolveTypes(SymbolTable symbolTable) {
		// if we have parameters and/or exceptions, resolve them
		if (parameters != null) parameters.resolveTypes(symbolTable);
		if (exceptions != null) exceptions.resolveTypes(symbolTable);

		// if we have a return type, resolve it
		if (type != null && type instanceof DummyClass) {
			Definition newType = symbolTable.lookupDummy(type);
			if (newType != null) {
				newType.addReference(type.getOccurrence());
				type = newType;
			}
		}

		// perform resolution for our superclass
		super.resolveTypes(symbolTable);
	}


	/** set the list of exceptions that this method can throw */
	void setExceptions(JavaVector exceptions) {
		this.exceptions = exceptions;
	}

	public String toString() {
		StringBuffer method = new StringBuffer(getName());
		//show parameters
		method.append("(");

		if (parameters != null) {
			int i = 0;
			for (; i < parameters.size() - 1; i++) {
				VariableDef def = (VariableDef) parameters.elementAt(i);
				method.append(def);
				method.append(",");
			}
			method.append((VariableDef) parameters.elementAt(i));
		}

		method.append(")");

		if (type != null)
			method.append(":" + type.getQualifiedName());
		//+" " + type.getDef());
		return method.toString();
		//return getClass().getName() + " [" + getQualifiedName() + "]";
	}

}