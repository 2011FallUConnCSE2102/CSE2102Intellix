// $ANTLR 2.7.1: "java.g" -> "JavaXref.java"$

package JavaParser;

import JavaParser.symtab.DummyClass;
import JavaParser.symtab.JavaVector;
import JavaParser.symtab.SymbolTable;
import antlr.*;
import antlr.collections.impl.BitSet;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class JavaXref extends antlr.LLkParser implements JavaXrefTokenTypes {

	// these static variables are used to tell what kind of compound
	// statement is being parsed (see the compoundStatement rule
	static final int BODY = 1;
	static final int CLASS_INIT = 2;
	static final int INSTANCE_INIT = 3;
	static final int NEW_SCOPE = 4;

	// We need a symbol table to track definitions
	private SymbolTable symbolTable;

	// Define a main
	public static void main(String[] args) {
		DefaultMutableTreeNode node = buildTree(args);

		JFrame frame = new JFrame("test");
		TreeClass tc = new TreeClass(node);
		frame.getContentPane().add(tc);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		frame.pack();
		frame.setVisible(true);
	}

	//build symbol table tree for directory or files from args
	public static DefaultMutableTreeNode buildTree(String[] args) {
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Project");

		// Use a try/catch block for parser exceptions
		try {
			// create a new symbol table
			SymbolTable symbolTable = new SymbolTable();

			// if we have at least one command-line argument
			if (args.length > 0) {
				System.err.println("Parsing...");

				// for each directory/file specified on the command line
				for (int i = 0; i < args.length; i++)
					doFile(new File(args[i]), symbolTable); // parse it

				System.err.println("Resolving types...");

				// resolve the types of all symbols in the symbol table
				symbolTable.resolveTypes();
			} else
				System.err.println("Usage: java JavaParser.JavaXref " + "<directory name>");

			symbolTable.report(top);

			TreeClass frame = new TreeClass(top);
		} catch (Exception e) {
			System.err.println("exception: " + e);
			e.printStackTrace(System.err);   // so we can get stack trace
		}
		return top;
	}

	// This method decides what action to take based on the type of
	//   file we are looking at
	public static void doFile(File f, SymbolTable symbolTable) throws Exception {
		// If this is a directory, walk each file/dir in that directory
		if (f.isDirectory()) {
			String files[] = f.list();
			for (int i = 0; i < files.length; i++)
				doFile(new File(f, files[i]), symbolTable);
		}

		// otherwise, if this is a java file, parse it!
		else if ((f.getName().length() > 5) && f.getName().substring(f.getName().length() - 5).equals(".java")) {
			symbolTable.setFile(f);
			System.err.println("   " + f.getAbsolutePath());
			parseFile(new FileInputStream(f), symbolTable);
		}
	}

	// Here's where we do the real work...
	public static void parseFile(InputStream s, SymbolTable symbolTable) throws Exception {
		try {
			// Create a scanner that reads from the input stream passed to us
			JavaLexer lexer = new JavaLexer(s);

			// Tell the scanner to create tokens of class JavaToken
			//System.out.println(System.getProperty("java.version"));
			lexer.setTokenObjectClass("JavaParser.JavaToken");

			// Create a parser that reads from the scanner
			JavaXref parser = new JavaXref(lexer);

			// Tell the parser to use the symbol table passed to us
			parser.setSymbolTable(symbolTable);

			// start parsing at the compilationUnit rule
			parser.compilationUnit();
		} catch (Exception e) {
			System.err.println("parser exception: " + e);
			//e.printStackTrace();   // so we can get stack trace
		}
	}

	// Tell the parser which symbol table to use
	public void setSymbolTable(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}


	//-------------------------------------------------------------------------
	// Symboltable adapter methods
	// The following methods are provided to give a single set of entry
	//   calls into the symbol table.  This makes it easy to add debugging
	//   code that will track all calls to symbolTable.popScope, for instance
	// These are direct pass-through calls to the symbolTable, but a
	//   few have special function.
	//-------------------------------------------------------------------------

	public void popScope() {
		symbolTable.popScope();
	}

	public void endFile() {
		symbolTable.popAllScopes();
	}

	public void defineBlock(JavaToken tok) {
		symbolTable.defineBlock(tok);
	}

	public void definePackage(JavaToken t) {
		symbolTable.definePackage(t);
	}

	public void defineLabel(JavaToken t) {
		symbolTable.defineLabel(t);
	}

	public void useDefaultPackage() {
		symbolTable.useDefaultPackage();
	}

	public void reference(JavaToken t) {
		symbolTable.reference(t);
	}

	public void setNearestClassScope() {
		symbolTable.setNearestClassScope();
	}

	public void endMethodHead(JavaVector exceptions) {
		symbolTable.endMethodHead(exceptions);
	}

	public DummyClass dummyClass(JavaToken theClass) {
		return symbolTable.getDummyClass(theClass);
	}


	public void defineClass(JavaToken theClass, JavaToken superClass, JavaVector interfaces) {
		symbolTable.defineClass(theClass, superClass, interfaces);
	}

	public void defineInterface(JavaToken theInterface, JavaVector subInterfaces) {
		symbolTable.defineInterface(theInterface, subInterfaces);
	}

	public void defineVar(JavaToken theVariable, JavaToken type) {
		symbolTable.defineVar(theVariable, type);
	}

	public void defineMethod(JavaToken theMethod, JavaToken type) {
		symbolTable.defineMethod(theMethod, type);
	}

	public void addImport(JavaToken id, String className, String packageName) {
		symbolTable.addImport(id, className, packageName);
	}


	protected JavaXref(TokenBuffer tokenBuf, int k) {
		super(tokenBuf, k);
		tokenNames = _tokenNames;
	}

	public JavaXref(TokenBuffer tokenBuf) {
		this(tokenBuf, 2);
	}

	protected JavaXref(TokenStream lexer, int k) {
		super(lexer, k);
		tokenNames = _tokenNames;
	}

	public JavaXref(TokenStream lexer) {
		this(lexer, 2);
	}

	public JavaXref(ParserSharedInputState state) {
		super(state, 2);
		tokenNames = _tokenNames;
	}

	public final void compilationUnit() throws RecognitionException, TokenStreamException {
		{
			switch (LA(1)) {
				case LITERAL_package:
					{
						packageDefinition();
						break;
					}
				case EOF:
				case SEMI:
				case LITERAL_import:
				case LITERAL_private:
				case LITERAL_public:
				case LITERAL_protected:
				case LITERAL_static:
				case LITERAL_transient:
				case LITERAL_final:
				case LITERAL_abstract:
				case LITERAL_native:
				case LITERAL_threadsafe:
				case LITERAL_synchronized:
				case LITERAL_const:
				case LITERAL_class:
				case LITERAL_interface:
					{
						if (inputState.guessing == 0) {
							useDefaultPackage();
						}
						break;
					}
				default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
			}
		}
		{
			_loop4:
			do {
				if ((LA(1) == LITERAL_import)) {
					importDefinition();
				} else {
					break _loop4;
				}

			} while (true);
		}
		{
			_loop6:
			do {
				if ((_tokenSet_0.member(LA(1)))) {
					typeDefinition();
				} else {
					break _loop6;
				}

			} while (true);
		}
		match(Token.EOF_TYPE);
		if (inputState.guessing == 0) {
			endFile();
		}
	}

	public final void packageDefinition() throws RecognitionException, TokenStreamException {

		JavaToken id;

		try {      // for error handling
			match(LITERAL_package);
			id = identifier();
			match(SEMI);
			if (inputState.guessing == 0) {
				definePackage(id);
			}
		} catch (RecognitionException ex) {
			if (inputState.guessing == 0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_1);
			} else {
				throw ex;
			}
		}
	}

	public final void importDefinition() throws RecognitionException, TokenStreamException {
		try {      // for error handling
			match(LITERAL_import);
			identifierStar();
			match(SEMI);
		} catch (RecognitionException ex) {
			if (inputState.guessing == 0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_1);
			} else {
				throw ex;
			}
		}
	}

	public final void typeDefinition() throws RecognitionException, TokenStreamException {
		try {      // for error handling
			switch (LA(1)) {
				case LITERAL_private:
				case LITERAL_public:
				case LITERAL_protected:
				case LITERAL_static:
				case LITERAL_transient:
				case LITERAL_final:
				case LITERAL_abstract:
				case LITERAL_native:
				case LITERAL_threadsafe:
				case LITERAL_synchronized:
				case LITERAL_const:
				case LITERAL_class:
				case LITERAL_interface:
					{
						modifiers();
						{
							switch (LA(1)) {
								case LITERAL_class:
									{
										classDefinition();
										break;
									}
								case LITERAL_interface:
									{
										interfaceDefinition();
										break;
									}
								default:
									{
										throw new NoViableAltException(LT(1), getFilename());
									}
							}
						}
						break;
					}
				case SEMI:
					{
						match(SEMI);
						break;
					}
				default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
			}
		} catch (RecognitionException ex) {
			if (inputState.guessing == 0) {
				reportError(ex);
				consume();
				consumeUntil(_tokenSet_2);
			} else {
				throw ex;
			}
		}
	}

	public final JavaToken identifier() throws RecognitionException, TokenStreamException {
		JavaToken t;

		Token id1 = null;
		Token id2 = null;
		t = null;

		id1 = LT(1);
		match(IDENT);
		if (inputState.guessing == 0) {
			t = (JavaToken) id1;
		}
		{
			_loop22:
			do {
				if ((LA(1) == DOT)) {
					match(DOT);
					id2 = LT(1);
					match(IDENT);
					if (inputState.guessing == 0) {
						t.setText(t.getText() + "." + id2.getText());
					}
				} else {
					break _loop22;
				}

			} while (true);
		}
		return t;
	}

	public final void identifierStar() throws RecognitionException, TokenStreamException {

		Token id = null;
		Token id2 = null;
		String className = "";
		String packageName = "";

		id = LT(1);
		match(IDENT);
		if (inputState.guessing == 0) {
			className = id.getText();
		}
		{
			_loop25:
			do {
				if ((LA(1) == DOT) && (LA(2) == IDENT)) {
					match(DOT);
					id2 = LT(1);
					match(IDENT);
					if (inputState.guessing == 0) {
						packageName += "." + className;
						className = id2.getText();
					}
				} else {
					break _loop25;
				}

			} while (true);
		}
		{
			switch (LA(1)) {
				case DOT:
					{
						match(DOT);
						match(STAR);
						if (inputState.guessing == 0) {
							packageName += "." + className;
							className = null;
						}
						break;
					}
				case SEMI:
					{
						break;
					}
				default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
			}
		}
		if (inputState.guessing == 0) {

			// put the overall name in the token's text
			if (packageName.equals(""))
				id.setText(className);
			else if (className == null)
				id.setText(packageName.substring(1));
			else
				id.setText(packageName.substring(1) + "." + className);

			// tell the symbol table about the import
			addImport((JavaToken) id, className, packageName);

		}
	}

	public final void modifiers() throws RecognitionException, TokenStreamException {


		{
			_loop14:
			do {
				if (((LA(1) >= LITERAL_private && LA(1) <= LITERAL_const))) {
					modifier();
				} else {
					break _loop14;
				}

			} while (true);
		}
	}

	public final void classDefinition() throws RecognitionException, TokenStreamException {

		Token id = null;
		JavaToken superClass = null;
		JavaVector interfaces = null;

		match(LITERAL_class);
		id = LT(1);
		match(IDENT);
		{
			switch (LA(1)) {
				case LITERAL_extends:
					{
						match(LITERAL_extends);
						superClass = identifier();
						break;
					}
				case LCURLY:
				case LITERAL_implements:
					{
						break;
					}
				default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
			}
		}
		{
			switch (LA(1)) {
				case LITERAL_implements:
					{
						interfaces = implementsClause();
						break;
					}
				case LCURLY:
					{
						break;
					}
				default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
			}
		}
		if (inputState.guessing == 0) {
			defineClass((JavaToken) id, superClass, interfaces);
		}
		classBlock();
		if (inputState.guessing == 0) {
			popScope();
		}
	}

	public final void interfaceDefinition() throws RecognitionException, TokenStreamException {

		Token id = null;
		JavaVector superInterfaces = null;

		match(LITERAL_interface);
		id = LT(1);
		match(IDENT);
		{
			switch (LA(1)) {
				case LITERAL_extends:
					{
						superInterfaces = interfaceExtends();
						break;
					}
				case LCURLY:
					{
						break;
					}
				default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
			}
		}
		if (inputState.guessing == 0) {
			defineInterface((JavaToken) id, superInterfaces);
		}
		classBlock();
		if (inputState.guessing == 0) {
			popScope();
		}
	}

	public final void declaration() throws RecognitionException, TokenStreamException {

		JavaToken type;

		modifiers();
		type = typeSpec();
		variableDefinitions(type);
	}

	public final JavaToken typeSpec() throws RecognitionException, TokenStreamException {
		JavaToken t;

		t = null;

		t = type();
		{
			_loop17:
			do {
				if ((LA(1) == LBRACK)) {
					match(LBRACK);
					match(RBRACK);
					if (t != null)
						t.setText(t.getText() + "[]");
				} else {
					break _loop17;
				}

			} while (true);
		}
		return t;
	}

	public final void variableDefinitions(JavaToken type) throws RecognitionException, TokenStreamException {


		variableDeclarator(type);
		{
			_loop48:
			do {
				if ((LA(1) == COMMA)) {
					match(COMMA);
					variableDeclarator(type);
				} else {
					break _loop48;
				}

			} while (true);
		}
	}

	public final void modifier() throws RecognitionException, TokenStreamException {


		switch (LA(1)) {
			case LITERAL_private:
				{
					match(LITERAL_private);
					break;
				}
			case LITERAL_public:
				{
					match(LITERAL_public);
					break;
				}
			case LITERAL_protected:
				{
					match(LITERAL_protected);
					break;
				}
			case LITERAL_static:
				{
					match(LITERAL_static);
					break;
				}
			case LITERAL_transient:
				{
					match(LITERAL_transient);
					break;
				}
			case LITERAL_final:
				{
					match(LITERAL_final);
					break;
				}
			case LITERAL_abstract:
				{
					match(LITERAL_abstract);
					break;
				}
			case LITERAL_native:
				{
					match(LITERAL_native);
					break;
				}
			case LITERAL_threadsafe:
				{
					match(LITERAL_threadsafe);
					break;
				}
			case LITERAL_synchronized:
				{
					match(LITERAL_synchronized);
					break;
				}
			case LITERAL_const:
				{
					match(LITERAL_const);
					break;
				}
			default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
		}
	}

	public final JavaToken type() throws RecognitionException, TokenStreamException {
		JavaToken t;

		t = null;

		switch (LA(1)) {
			case IDENT:
				{
					t = identifier();
					break;
				}
			case LITERAL_void:
			case LITERAL_boolean:
			case LITERAL_byte:
			case LITERAL_char:
			case LITERAL_short:
			case LITERAL_int:
			case LITERAL_float:
			case LITERAL_long:
			case LITERAL_double:
				{
					t = builtInType();
					break;
				}
			default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
		}
		return t;
	}

	public final JavaToken builtInType() throws RecognitionException, TokenStreamException {
		JavaToken t;

		Token bVoid = null;
		Token bBoolean = null;
		Token bByte = null;
		Token bChar = null;
		Token bShort = null;
		Token bInt = null;
		Token bFloat = null;
		Token bLong = null;
		Token bDouble = null;
		t = null;

		switch (LA(1)) {
			case LITERAL_void:
				{
					bVoid = LT(1);
					match(LITERAL_void);
					if (inputState.guessing == 0) {
						t = (JavaToken) bVoid;
					}
					break;
				}
			case LITERAL_boolean:
				{
					bBoolean = LT(1);
					match(LITERAL_boolean);
					if (inputState.guessing == 0) {
						t = (JavaToken) bBoolean;
					}
					break;
				}
			case LITERAL_byte:
				{
					bByte = LT(1);
					match(LITERAL_byte);
					if (inputState.guessing == 0) {
						t = (JavaToken) bByte;
					}
					break;
				}
			case LITERAL_char:
				{
					bChar = LT(1);
					match(LITERAL_char);
					if (inputState.guessing == 0) {
						t = (JavaToken) bChar;
					}
					break;
				}
			case LITERAL_short:
				{
					bShort = LT(1);
					match(LITERAL_short);
					if (inputState.guessing == 0) {
						t = (JavaToken) bShort;
					}
					break;
				}
			case LITERAL_int:
				{
					bInt = LT(1);
					match(LITERAL_int);
					if (inputState.guessing == 0) {
						t = (JavaToken) bInt;
					}
					break;
				}
			case LITERAL_float:
				{
					bFloat = LT(1);
					match(LITERAL_float);
					if (inputState.guessing == 0) {
						t = (JavaToken) bFloat;
					}
					break;
				}
			case LITERAL_long:
				{
					bLong = LT(1);
					match(LITERAL_long);
					if (inputState.guessing == 0) {
						t = (JavaToken) bLong;
					}
					break;
				}
			case LITERAL_double:
				{
					bDouble = LT(1);
					match(LITERAL_double);
					if (inputState.guessing == 0) {
						t = (JavaToken) bDouble;
					}
					break;
				}
			default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
		}
		return t;
	}

	public final JavaVector implementsClause() throws RecognitionException, TokenStreamException {
		JavaVector inters;

		inters = new JavaVector();
		JavaToken id;

		match(LITERAL_implements);
		id = identifier();
		if (inputState.guessing == 0) {
			inters.addElement(dummyClass(id));
		}
		{
			_loop41:
			do {
				if ((LA(1) == COMMA)) {
					match(COMMA);
					id = identifier();
					if (inputState.guessing == 0) {
						inters.addElement(dummyClass(id));
					}
				} else {
					break _loop41;
				}

			} while (true);
		}
		return inters;
	}

	public final void classBlock() throws RecognitionException, TokenStreamException {


		match(LCURLY);
		{
			_loop35:
			do {
				switch (LA(1)) {
					case LITERAL_void:
					case LITERAL_boolean:
					case LITERAL_byte:
					case LITERAL_char:
					case LITERAL_short:
					case LITERAL_int:
					case LITERAL_float:
					case LITERAL_long:
					case LITERAL_double:
					case IDENT:
					case LITERAL_private:
					case LITERAL_public:
					case LITERAL_protected:
					case LITERAL_static:
					case LITERAL_transient:
					case LITERAL_final:
					case LITERAL_abstract:
					case LITERAL_native:
					case LITERAL_threadsafe:
					case LITERAL_synchronized:
					case LITERAL_const:
					case LITERAL_class:
					case LITERAL_interface:
					case LCURLY:
						{
							field();
							break;
						}
					case SEMI:
						{
							match(SEMI);
							break;
						}
					default:
						{
							break _loop35;
						}
				}
			} while (true);
		}
		match(RCURLY);
	}

	public final JavaVector interfaceExtends() throws RecognitionException, TokenStreamException {
		JavaVector supers;

		JavaToken id;
		supers = new JavaVector();

		match(LITERAL_extends);
		id = identifier();
		if (inputState.guessing == 0) {
			supers.addElement(dummyClass(id));
		}
		{
			_loop38:
			do {
				if ((LA(1) == COMMA)) {
					match(COMMA);
					id = identifier();
					if (inputState.guessing == 0) {
						supers.addElement(dummyClass(id));
					}
				} else {
					break _loop38;
				}

			} while (true);
		}
		return supers;
	}

	public final void field() throws RecognitionException, TokenStreamException {

		JavaToken type;

		if ((_tokenSet_3.member(LA(1))) && (_tokenSet_4.member(LA(2)))) {
			modifiers();
			{
				switch (LA(1)) {
					case LITERAL_class:
						{
							classDefinition();
							break;
						}
					case LITERAL_interface:
						{
							interfaceDefinition();
							break;
						}
					default:
						if ((LA(1) == IDENT) && (LA(2) == LPAREN)) {
							methodHead(null);
							compoundStatement(BODY);
						} else if (((LA(1) >= LITERAL_void && LA(1) <= IDENT)) && (_tokenSet_5.member(LA(2)))) {
							type = typeSpec();
							{
								if ((LA(1) == IDENT) && (LA(2) == LPAREN)) {
									methodHead(type);
									{
										switch (LA(1)) {
											case LCURLY:
												{
													compoundStatement(BODY);
													break;
												}
											case SEMI:
												{
													match(SEMI);
													if (inputState.guessing == 0) {
														popScope();
													}
													break;
												}
											default:
												{
													throw new NoViableAltException(LT(1), getFilename());
												}
										}
									}
								} else if ((LA(1) == IDENT) && (_tokenSet_6.member(LA(2)))) {
									variableDefinitions(type);
									match(SEMI);
								} else {
									throw new NoViableAltException(LT(1), getFilename());
								}

							}
						} else {
							throw new NoViableAltException(LT(1), getFilename());
						}
				}
			}
		} else if ((LA(1) == LITERAL_static) && (LA(2) == LCURLY)) {
			match(LITERAL_static);
			compoundStatement(CLASS_INIT);
		} else if ((LA(1) == LCURLY)) {
			compoundStatement(INSTANCE_INIT);
		} else {
			throw new NoViableAltException(LT(1), getFilename());
		}

	}

	public final void methodHead(JavaToken type) throws RecognitionException, TokenStreamException {

		Token method = null;
		JavaVector exceptions = null;

		method = LT(1);
		match(IDENT);
		if (inputState.guessing == 0) {
			defineMethod((JavaToken) method, type);
		}
		match(LPAREN);
		{
			switch (LA(1)) {
				case LITERAL_void:
				case LITERAL_boolean:
				case LITERAL_byte:
				case LITERAL_char:
				case LITERAL_short:
				case LITERAL_int:
				case LITERAL_float:
				case LITERAL_long:
				case LITERAL_double:
				case IDENT:
				case LITERAL_final:
					{
						parameterDeclarationList();
						break;
					}
				case RPAREN:
					{
						break;
					}
				default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
			}
		}
		match(RPAREN);
		{
			_loop62:
			do {
				if ((LA(1) == LBRACK)) {
					match(LBRACK);
					match(RBRACK);
				} else {
					break _loop62;
				}

			} while (true);
		}
		{
			switch (LA(1)) {
				case LITERAL_throws:
					{
						exceptions = throwsClause();
						break;
					}
				case SEMI:
				case LCURLY:
					{
						break;
					}
				default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
			}
		}
		if (inputState.guessing == 0) {
			endMethodHead(exceptions);
		}
	}

	public final void compoundStatement(int scopeType) throws RecognitionException, TokenStreamException {

		Token lc = null;

		lc = LT(1);
		match(LCURLY);
		if (inputState.guessing == 0) {
			// based on the scopeType we are processing
			switch (scopeType) {
				// if it's a new block, tell the symbol table
				case NEW_SCOPE:
					defineBlock((JavaToken) lc);
					break;

					// if it's a class initializer or instance initializer,
					//   treat it like a method with a special name
				case CLASS_INIT:
					lc.setText("~class-init~");
					defineMethod(null, (JavaToken) lc);
					endMethodHead(null);
					break;
				case INSTANCE_INIT:
					lc.setText("~instance-init~");
					defineMethod(null, (JavaToken) lc);
					endMethodHead(null);
					break;

					// otherwise, it's a body, so do nothing special
			}

		}
		{
			_loop76:
			do {
				if ((_tokenSet_7.member(LA(1)))) {
					statement();
				} else {
					break _loop76;
				}

			} while (true);
		}
		if (inputState.guessing == 0) {
			popScope();
		}
		match(RCURLY);
	}

	public final void variableDeclarator(JavaToken type) throws RecognitionException, TokenStreamException {

		Token id = null;

		id = LT(1);
		match(IDENT);
		{
			_loop51:
			do {
				if ((LA(1) == LBRACK)) {
					match(LBRACK);
					match(RBRACK);
				} else {
					break _loop51;
				}

			} while (true);
		}
		{
			switch (LA(1)) {
				case ASSIGN:
					{
						match(ASSIGN);
						initializer();
						break;
					}
				case SEMI:
				case COMMA:
					{
						break;
					}
				default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
			}
		}
		if (inputState.guessing == 0) {
			defineVar((JavaToken) id, type);
		}
	}

	public final void initializer() throws RecognitionException, TokenStreamException {


		switch (LA(1)) {
			case LITERAL_void:
			case LITERAL_boolean:
			case LITERAL_byte:
			case LITERAL_char:
			case LITERAL_short:
			case LITERAL_int:
			case LITERAL_float:
			case LITERAL_long:
			case LITERAL_double:
			case IDENT:
			case LPAREN:
			case MINUS:
			case INC:
			case DEC:
			case BNOT:
			case LNOT:
			case LITERAL_this:
			case LITERAL_super:
			case LITERAL_true:
			case LITERAL_false:
			case LITERAL_null:
			case LITERAL_new:
			case NUM_INT:
			case CHAR_LITERAL:
			case STRING_LITERAL:
			case NUM_FLOAT:
				{
					expression();
					break;
				}
			case LCURLY:
				{
					arrayInitializer();
					break;
				}
			default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
		}
	}

	public final void arrayInitializer() throws RecognitionException, TokenStreamException {


		match(LCURLY);
		{
			switch (LA(1)) {
				case LITERAL_void:
				case LITERAL_boolean:
				case LITERAL_byte:
				case LITERAL_char:
				case LITERAL_short:
				case LITERAL_int:
				case LITERAL_float:
				case LITERAL_long:
				case LITERAL_double:
				case IDENT:
				case LCURLY:
				case LPAREN:
				case MINUS:
				case INC:
				case DEC:
				case BNOT:
				case LNOT:
				case LITERAL_this:
				case LITERAL_super:
				case LITERAL_true:
				case LITERAL_false:
				case LITERAL_null:
				case LITERAL_new:
				case NUM_INT:
				case CHAR_LITERAL:
				case STRING_LITERAL:
				case NUM_FLOAT:
					{
						initializer();
						{
							_loop56:
							do {
								if ((LA(1) == COMMA) && (_tokenSet_8.member(LA(2)))) {
									match(COMMA);
									initializer();
								} else {
									break _loop56;
								}

							} while (true);
						}
						{
							switch (LA(1)) {
								case COMMA:
									{
										match(COMMA);
										break;
									}
								case RCURLY:
									{
										break;
									}
								default:
									{
										throw new NoViableAltException(LT(1), getFilename());
									}
							}
						}
						break;
					}
				case RCURLY:
					{
						break;
					}
				default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
			}
		}
		match(RCURLY);
	}

	public final void expression() throws RecognitionException, TokenStreamException {


		assignmentExpression();
	}

	public final void parameterDeclarationList() throws RecognitionException, TokenStreamException {


		parameterDeclaration();
		{
			_loop69:
			do {
				if ((LA(1) == COMMA)) {
					match(COMMA);
					parameterDeclaration();
				} else {
					break _loop69;
				}

			} while (true);
		}
	}

	public final JavaVector throwsClause() throws RecognitionException, TokenStreamException {
		JavaVector exceptions;

		JavaToken id;
		exceptions = new JavaVector();

		match(LITERAL_throws);
		id = identifier();
		if (inputState.guessing == 0) {
			exceptions.addElement(dummyClass(id));
		}
		{
			_loop66:
			do {
				if ((LA(1) == COMMA)) {
					match(COMMA);
					id = identifier();
					if (inputState.guessing == 0) {
						exceptions.addElement(dummyClass(id));
					}
				} else {
					break _loop66;
				}

			} while (true);
		}
		return exceptions;
	}

	public final void parameterDeclaration() throws RecognitionException, TokenStreamException {

		Token id = null;
		JavaToken type;

		{
			switch (LA(1)) {
				case LITERAL_final:
					{
						match(LITERAL_final);
						break;
					}
				case LITERAL_void:
				case LITERAL_boolean:
				case LITERAL_byte:
				case LITERAL_char:
				case LITERAL_short:
				case LITERAL_int:
				case LITERAL_float:
				case LITERAL_long:
				case LITERAL_double:
				case IDENT:
					{
						break;
					}
				default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
			}
		}
		type = typeSpec();
		id = LT(1);
		match(IDENT);
		{
			_loop73:
			do {
				if ((LA(1) == LBRACK)) {
					match(LBRACK);
					match(RBRACK);
				} else {
					break _loop73;
				}

			} while (true);
		}
		if (inputState.guessing == 0) {
			defineVar((JavaToken) id, type);
		}
	}

	public final void statement() throws RecognitionException, TokenStreamException {

		Token id = null;
		Token bid = null;
		Token cid = null;
		int count = -1;

		switch (LA(1)) {
			case LCURLY:
				{
					compoundStatement(NEW_SCOPE);
					break;
				}
			case LITERAL_if:
				{
					match(LITERAL_if);
					match(LPAREN);
					expression();
					match(RPAREN);
					statement();
					{
						if ((LA(1) == LITERAL_else) && (_tokenSet_7.member(LA(2)))) {
							match(LITERAL_else);
							statement();
						} else if ((_tokenSet_9.member(LA(1))) && (_tokenSet_10.member(LA(2)))) {
						} else {
							throw new NoViableAltException(LT(1), getFilename());
						}

					}
					break;
				}
			case LITERAL_for:
				{
					match(LITERAL_for);
					match(LPAREN);
					{
						switch (LA(1)) {
							case LITERAL_void:
							case LITERAL_boolean:
							case LITERAL_byte:
							case LITERAL_char:
							case LITERAL_short:
							case LITERAL_int:
							case LITERAL_float:
							case LITERAL_long:
							case LITERAL_double:
							case IDENT:
							case LITERAL_private:
							case LITERAL_public:
							case LITERAL_protected:
							case LITERAL_static:
							case LITERAL_transient:
							case LITERAL_final:
							case LITERAL_abstract:
							case LITERAL_native:
							case LITERAL_threadsafe:
							case LITERAL_synchronized:
							case LITERAL_const:
							case LPAREN:
							case MINUS:
							case INC:
							case DEC:
							case BNOT:
							case LNOT:
							case LITERAL_this:
							case LITERAL_super:
							case LITERAL_true:
							case LITERAL_false:
							case LITERAL_null:
							case LITERAL_new:
							case NUM_INT:
							case CHAR_LITERAL:
							case STRING_LITERAL:
							case NUM_FLOAT:
								{
									forInit();
									break;
								}
							case SEMI:
								{
									break;
								}
							default:
								{
									throw new NoViableAltException(LT(1), getFilename());
								}
						}
					}
					match(SEMI);
					{
						switch (LA(1)) {
							case LITERAL_void:
							case LITERAL_boolean:
							case LITERAL_byte:
							case LITERAL_char:
							case LITERAL_short:
							case LITERAL_int:
							case LITERAL_float:
							case LITERAL_long:
							case LITERAL_double:
							case IDENT:
							case LPAREN:
							case MINUS:
							case INC:
							case DEC:
							case BNOT:
							case LNOT:
							case LITERAL_this:
							case LITERAL_super:
							case LITERAL_true:
							case LITERAL_false:
							case LITERAL_null:
							case LITERAL_new:
							case NUM_INT:
							case CHAR_LITERAL:
							case STRING_LITERAL:
							case NUM_FLOAT:
								{
									expression();
									break;
								}
							case SEMI:
								{
									break;
								}
							default:
								{
									throw new NoViableAltException(LT(1), getFilename());
								}
						}
					}
					match(SEMI);
					{
						switch (LA(1)) {
							case LITERAL_void:
							case LITERAL_boolean:
							case LITERAL_byte:
							case LITERAL_char:
							case LITERAL_short:
							case LITERAL_int:
							case LITERAL_float:
							case LITERAL_long:
							case LITERAL_double:
							case IDENT:
							case LPAREN:
							case MINUS:
							case INC:
							case DEC:
							case BNOT:
							case LNOT:
							case LITERAL_this:
							case LITERAL_super:
							case LITERAL_true:
							case LITERAL_false:
							case LITERAL_null:
							case LITERAL_new:
							case NUM_INT:
							case CHAR_LITERAL:
							case STRING_LITERAL:
							case NUM_FLOAT:
								{
									count = expressionList();
									break;
								}
							case RPAREN:
								{
									break;
								}
							default:
								{
									throw new NoViableAltException(LT(1), getFilename());
								}
						}
					}
					match(RPAREN);
					statement();
					break;
				}
			case LITERAL_while:
				{
					match(LITERAL_while);
					match(LPAREN);
					expression();
					match(RPAREN);
					statement();
					break;
				}
			case LITERAL_do:
				{
					match(LITERAL_do);
					statement();
					match(LITERAL_while);
					match(LPAREN);
					expression();
					match(RPAREN);
					match(SEMI);
					break;
				}
			case LITERAL_break:
				{
					match(LITERAL_break);
					{
						switch (LA(1)) {
							case IDENT:
								{
									bid = LT(1);
									match(IDENT);
									if (inputState.guessing == 0) {
										reference((JavaToken) bid);
									}
									break;
								}
							case SEMI:
								{
									break;
								}
							default:
								{
									throw new NoViableAltException(LT(1), getFilename());
								}
						}
					}
					match(SEMI);
					break;
				}
			case LITERAL_continue:
				{
					match(LITERAL_continue);
					{
						switch (LA(1)) {
							case IDENT:
								{
									cid = LT(1);
									match(IDENT);
									if (inputState.guessing == 0) {
										reference((JavaToken) cid);
									}
									break;
								}
							case SEMI:
								{
									break;
								}
							default:
								{
									throw new NoViableAltException(LT(1), getFilename());
								}
						}
					}
					match(SEMI);
					break;
				}
			case LITERAL_return:
				{
					match(LITERAL_return);
					{
						switch (LA(1)) {
							case LITERAL_void:
							case LITERAL_boolean:
							case LITERAL_byte:
							case LITERAL_char:
							case LITERAL_short:
							case LITERAL_int:
							case LITERAL_float:
							case LITERAL_long:
							case LITERAL_double:
							case IDENT:
							case LPAREN:
							case MINUS:
							case INC:
							case DEC:
							case BNOT:
							case LNOT:
							case LITERAL_this:
							case LITERAL_super:
							case LITERAL_true:
							case LITERAL_false:
							case LITERAL_null:
							case LITERAL_new:
							case NUM_INT:
							case CHAR_LITERAL:
							case STRING_LITERAL:
							case NUM_FLOAT:
								{
									expression();
									break;
								}
							case SEMI:
								{
									break;
								}
							default:
								{
									throw new NoViableAltException(LT(1), getFilename());
								}
						}
					}
					match(SEMI);
					break;
				}
			case LITERAL_switch:
				{
					match(LITERAL_switch);
					match(LPAREN);
					expression();
					match(RPAREN);
					match(LCURLY);
					{
						_loop93:
						do {
							if ((LA(1) == LITERAL_case || LA(1) == LITERAL_default)) {
								{
									int _cnt90 = 0;
									_loop90:
									do {
										if ((LA(1) == LITERAL_case || LA(1) == LITERAL_default) && (_tokenSet_11.member(LA(2)))) {
											{
												switch (LA(1)) {
													case LITERAL_case:
														{
															match(LITERAL_case);
															expression();
															break;
														}
													case LITERAL_default:
														{
															match(LITERAL_default);
															break;
														}
													default:
														{
															throw new NoViableAltException(LT(1), getFilename());
														}
												}
											}
											match(COLON);
										} else {
											if (_cnt90 >= 1) {
												break _loop90;
											} else {
												throw new NoViableAltException(LT(1), getFilename());
											}
										}

										_cnt90++;
									} while (true);
								}
								{
									_loop92:
									do {
										if ((_tokenSet_7.member(LA(1)))) {
											statement();
										} else {
											break _loop92;
										}

									} while (true);
								}
							} else {
								break _loop93;
							}

						} while (true);
					}
					match(RCURLY);
					break;
				}
			case LITERAL_try:
				{
					tryBlock();
					break;
				}
			case LITERAL_throw:
				{
					match(LITERAL_throw);
					expression();
					match(SEMI);
					break;
				}
			case SEMI:
				{
					match(SEMI);
					break;
				}
			default:
				boolean synPredMatched79 = false;
				if (((_tokenSet_12.member(LA(1))) && (_tokenSet_13.member(LA(2))))) {
					int _m79 = mark();
					synPredMatched79 = true;
					inputState.guessing++;
					try {
						{
							declaration();
						}
					} catch (RecognitionException pe) {
						synPredMatched79 = false;
					}
					rewind(_m79);
					inputState.guessing--;
				}
				if (synPredMatched79) {
					declaration();
					match(SEMI);
				} else if ((LA(1) == IDENT) && (LA(2) == COLON)) {
					id = LT(1);
					match(IDENT);
					match(COLON);
					statement();
					if (inputState.guessing == 0) {
						defineLabel((JavaToken) id);
					}
				} else if ((_tokenSet_14.member(LA(1))) && (_tokenSet_15.member(LA(2)))) {
					expression();
					match(SEMI);
				} else if ((LA(1) == LITERAL_synchronized) && (LA(2) == LPAREN)) {
					match(LITERAL_synchronized);
					match(LPAREN);
					expression();
					match(RPAREN);
					statement();
				} else {
					throw new NoViableAltException(LT(1), getFilename());
				}
		}
	}

	public final void forInit() throws RecognitionException, TokenStreamException {

		int count = -1;

		boolean synPredMatched96 = false;
		if (((_tokenSet_12.member(LA(1))) && (_tokenSet_13.member(LA(2))))) {
			int _m96 = mark();
			synPredMatched96 = true;
			inputState.guessing++;
			try {
				{
					declaration();
				}
			} catch (RecognitionException pe) {
				synPredMatched96 = false;
			}
			rewind(_m96);
			inputState.guessing--;
		}
		if (synPredMatched96) {
			declaration();
		} else if ((_tokenSet_14.member(LA(1))) && (_tokenSet_16.member(LA(2)))) {
			count = expressionList();
		} else {
			throw new NoViableAltException(LT(1), getFilename());
		}

	}

	public final int expressionList() throws RecognitionException, TokenStreamException {
		int count;

		count = 1;

		expression();
		{
			_loop105:
			do {
				if ((LA(1) == COMMA)) {
					match(COMMA);
					expression();
					if (inputState.guessing == 0) {
						count++;
					}
				} else {
					break _loop105;
				}

			} while (true);
		}
		return count;
	}

	public final void tryBlock() throws RecognitionException, TokenStreamException {


		match(LITERAL_try);
		compoundStatement(NEW_SCOPE);
		{
			_loop99:
			do {
				if ((LA(1) == LITERAL_catch)) {
					handler();
				} else {
					break _loop99;
				}

			} while (true);
		}
		{
			switch (LA(1)) {
				case LITERAL_finally:
					{
						match(LITERAL_finally);
						compoundStatement(NEW_SCOPE);
						break;
					}
				case SEMI:
				case LITERAL_void:
				case LITERAL_boolean:
				case LITERAL_byte:
				case LITERAL_char:
				case LITERAL_short:
				case LITERAL_int:
				case LITERAL_float:
				case LITERAL_long:
				case LITERAL_double:
				case IDENT:
				case LITERAL_private:
				case LITERAL_public:
				case LITERAL_protected:
				case LITERAL_static:
				case LITERAL_transient:
				case LITERAL_final:
				case LITERAL_abstract:
				case LITERAL_native:
				case LITERAL_threadsafe:
				case LITERAL_synchronized:
				case LITERAL_const:
				case LCURLY:
				case RCURLY:
				case LPAREN:
				case LITERAL_if:
				case LITERAL_else:
				case LITERAL_for:
				case LITERAL_while:
				case LITERAL_do:
				case LITERAL_break:
				case LITERAL_continue:
				case LITERAL_return:
				case LITERAL_switch:
				case LITERAL_case:
				case LITERAL_default:
				case LITERAL_throw:
				case LITERAL_try:
				case MINUS:
				case INC:
				case DEC:
				case BNOT:
				case LNOT:
				case LITERAL_this:
				case LITERAL_super:
				case LITERAL_true:
				case LITERAL_false:
				case LITERAL_null:
				case LITERAL_new:
				case NUM_INT:
				case CHAR_LITERAL:
				case STRING_LITERAL:
				case NUM_FLOAT:
					{
						break;
					}
				default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
			}
		}
	}

	public final void handler() throws RecognitionException, TokenStreamException {


		match(LITERAL_catch);
		match(LPAREN);
		parameterDeclaration();
		match(RPAREN);
		compoundStatement(NEW_SCOPE);
	}

	public final void assignmentExpression() throws RecognitionException, TokenStreamException {


		conditionalExpression();
		{
			switch (LA(1)) {
				case ASSIGN:
				case PLUS_ASSIGN:
				case MINUS_ASSIGN:
				case STAR_ASSIGN:
				case DIV_ASSIGN:
				case MOD_ASSIGN:
				case SR_ASSIGN:
				case BSR_ASSIGN:
				case SL_ASSIGN:
				case BAND_ASSIGN:
				case BXOR_ASSIGN:
				case BOR_ASSIGN:
					{
						{
							switch (LA(1)) {
								case ASSIGN:
									{
										match(ASSIGN);
										break;
									}
								case PLUS_ASSIGN:
									{
										match(PLUS_ASSIGN);
										break;
									}
								case MINUS_ASSIGN:
									{
										match(MINUS_ASSIGN);
										break;
									}
								case STAR_ASSIGN:
									{
										match(STAR_ASSIGN);
										break;
									}
								case DIV_ASSIGN:
									{
										match(DIV_ASSIGN);
										break;
									}
								case MOD_ASSIGN:
									{
										match(MOD_ASSIGN);
										break;
									}
								case SR_ASSIGN:
									{
										match(SR_ASSIGN);
										break;
									}
								case BSR_ASSIGN:
									{
										match(BSR_ASSIGN);
										break;
									}
								case SL_ASSIGN:
									{
										match(SL_ASSIGN);
										break;
									}
								case BAND_ASSIGN:
									{
										match(BAND_ASSIGN);
										break;
									}
								case BXOR_ASSIGN:
									{
										match(BXOR_ASSIGN);
										break;
									}
								case BOR_ASSIGN:
									{
										match(BOR_ASSIGN);
										break;
									}
								default:
									{
										throw new NoViableAltException(LT(1), getFilename());
									}
							}
						}
						assignmentExpression();
						break;
					}
				case SEMI:
				case RBRACK:
				case RCURLY:
				case COMMA:
				case RPAREN:
				case COLON:
					{
						break;
					}
				default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
			}
		}
	}

	public final void conditionalExpression() throws RecognitionException, TokenStreamException {


		logicalOrExpression();
		{
			switch (LA(1)) {
				case QUESTION:
					{
						match(QUESTION);
						conditionalExpression();
						match(COLON);
						conditionalExpression();
						break;
					}
				case SEMI:
				case RBRACK:
				case RCURLY:
				case COMMA:
				case ASSIGN:
				case RPAREN:
				case COLON:
				case PLUS_ASSIGN:
				case MINUS_ASSIGN:
				case STAR_ASSIGN:
				case DIV_ASSIGN:
				case MOD_ASSIGN:
				case SR_ASSIGN:
				case BSR_ASSIGN:
				case SL_ASSIGN:
				case BAND_ASSIGN:
				case BXOR_ASSIGN:
				case BOR_ASSIGN:
					{
						break;
					}
				default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
			}
		}
	}

	public final void logicalOrExpression() throws RecognitionException, TokenStreamException {


		logicalAndExpression();
		{
			_loop113:
			do {
				if ((LA(1) == LOR)) {
					match(LOR);
					logicalAndExpression();
				} else {
					break _loop113;
				}

			} while (true);
		}
	}

	public final void logicalAndExpression() throws RecognitionException, TokenStreamException {


		inclusiveOrExpression();
		{
			_loop116:
			do {
				if ((LA(1) == LAND)) {
					match(LAND);
					inclusiveOrExpression();
				} else {
					break _loop116;
				}

			} while (true);
		}
	}

	public final void inclusiveOrExpression() throws RecognitionException, TokenStreamException {


		exclusiveOrExpression();
		{
			_loop119:
			do {
				if ((LA(1) == BOR)) {
					match(BOR);
					exclusiveOrExpression();
				} else {
					break _loop119;
				}

			} while (true);
		}
	}

	public final void exclusiveOrExpression() throws RecognitionException, TokenStreamException {


		andExpression();
		{
			_loop122:
			do {
				if ((LA(1) == BXOR)) {
					match(BXOR);
					andExpression();
				} else {
					break _loop122;
				}

			} while (true);
		}
	}

	public final void andExpression() throws RecognitionException, TokenStreamException {


		equalityExpression();
		{
			_loop125:
			do {
				if ((LA(1) == BAND)) {
					match(BAND);
					equalityExpression();
				} else {
					break _loop125;
				}

			} while (true);
		}
	}

	public final void equalityExpression() throws RecognitionException, TokenStreamException {


		relationalExpression();
		{
			_loop129:
			do {
				if ((LA(1) == NOT_EQUAL || LA(1) == EQUAL)) {
					{
						switch (LA(1)) {
							case NOT_EQUAL:
								{
									match(NOT_EQUAL);
									break;
								}
							case EQUAL:
								{
									match(EQUAL);
									break;
								}
							default:
								{
									throw new NoViableAltException(LT(1), getFilename());
								}
						}
					}
					relationalExpression();
				} else {
					break _loop129;
				}

			} while (true);
		}
	}

	public final void relationalExpression() throws RecognitionException, TokenStreamException {


		shiftExpression();
		{
			_loop133:
			do {
				if (((LA(1) >= LT && LA(1) <= GE))) {
					{
						switch (LA(1)) {
							case LT:
								{
									match(LT);
									break;
								}
							case GT:
								{
									match(GT);
									break;
								}
							case LE:
								{
									match(LE);
									break;
								}
							case GE:
								{
									match(GE);
									break;
								}
							default:
								{
									throw new NoViableAltException(LT(1), getFilename());
								}
						}
					}
					shiftExpression();
				} else {
					break _loop133;
				}

			} while (true);
		}
	}

	public final void shiftExpression() throws RecognitionException, TokenStreamException {


		additiveExpression();
		{
			_loop137:
			do {
				if (((LA(1) >= SL && LA(1) <= BSR))) {
					{
						switch (LA(1)) {
							case SL:
								{
									match(SL);
									break;
								}
							case SR:
								{
									match(SR);
									break;
								}
							case BSR:
								{
									match(BSR);
									break;
								}
							default:
								{
									throw new NoViableAltException(LT(1), getFilename());
								}
						}
					}
					additiveExpression();
				} else {
					break _loop137;
				}

			} while (true);
		}
	}

	public final void additiveExpression() throws RecognitionException, TokenStreamException {


		multiplicativeExpression();
		{
			_loop141:
			do {
				if ((LA(1) == PLUS || LA(1) == MINUS)) {
					{
						switch (LA(1)) {
							case PLUS:
								{
									match(PLUS);
									break;
								}
							case MINUS:
								{
									match(MINUS);
									break;
								}
							default:
								{
									throw new NoViableAltException(LT(1), getFilename());
								}
						}
					}
					multiplicativeExpression();
				} else {
					break _loop141;
				}

			} while (true);
		}
	}

	public final void multiplicativeExpression() throws RecognitionException, TokenStreamException {


		castExpression();
		{
			_loop145:
			do {
				if ((_tokenSet_17.member(LA(1)))) {
					{
						switch (LA(1)) {
							case STAR:
								{
									match(STAR);
									break;
								}
							case DIV:
								{
									match(DIV);
									break;
								}
							case MOD:
								{
									match(MOD);
									break;
								}
							default:
								{
									throw new NoViableAltException(LT(1), getFilename());
								}
						}
					}
					castExpression();
				} else {
					break _loop145;
				}

			} while (true);
		}
	}

	public final void castExpression() throws RecognitionException, TokenStreamException {

		JavaToken t;

		switch (LA(1)) {
			case INC:
				{
					match(INC);
					castExpression();
					break;
				}
			case DEC:
				{
					match(DEC);
					castExpression();
					break;
				}
			case MINUS:
				{
					match(MINUS);
					castExpression();
					break;
				}
			case BNOT:
				{
					match(BNOT);
					castExpression();
					break;
				}
			case LNOT:
				{
					match(LNOT);
					castExpression();
					break;
				}
			default:
				boolean synPredMatched148 = false;
				if (((LA(1) == LPAREN) && ((LA(2) >= LITERAL_void && LA(2) <= IDENT)))) {
					int _m148 = mark();
					synPredMatched148 = true;
					inputState.guessing++;
					try {
						{
							match(LPAREN);
							t = typeSpec();
							match(RPAREN);
							castExpression();
						}
					} catch (RecognitionException pe) {
						synPredMatched148 = false;
					}
					rewind(_m148);
					inputState.guessing--;
				}
				if (synPredMatched148) {
					match(LPAREN);
					t = typeSpec();
					match(RPAREN);
					castExpression();
					if (inputState.guessing == 0) {
						reference(t);
					}
				} else if ((_tokenSet_18.member(LA(1))) && (_tokenSet_19.member(LA(2)))) {
					postfixExpression();
					{
						switch (LA(1)) {
							case LITERAL_instanceof:
								{
									match(LITERAL_instanceof);
									t = typeSpec();
									if (inputState.guessing == 0) {
										reference(t);
									}
									break;
								}
							case SEMI:
							case RBRACK:
							case STAR:
							case RCURLY:
							case COMMA:
							case ASSIGN:
							case RPAREN:
							case COLON:
							case PLUS_ASSIGN:
							case MINUS_ASSIGN:
							case STAR_ASSIGN:
							case DIV_ASSIGN:
							case MOD_ASSIGN:
							case SR_ASSIGN:
							case BSR_ASSIGN:
							case SL_ASSIGN:
							case BAND_ASSIGN:
							case BXOR_ASSIGN:
							case BOR_ASSIGN:
							case QUESTION:
							case LOR:
							case LAND:
							case BOR:
							case BXOR:
							case BAND:
							case NOT_EQUAL:
							case EQUAL:
							case LT:
							case GT:
							case LE:
							case GE:
							case SL:
							case SR:
							case BSR:
							case PLUS:
							case MINUS:
							case DIV:
							case MOD:
								{
									break;
								}
							default:
								{
									throw new NoViableAltException(LT(1), getFilename());
								}
						}
					}
				} else {
					throw new NoViableAltException(LT(1), getFilename());
				}
		}
	}

	public final void postfixExpression() throws RecognitionException, TokenStreamException {

		Token id = null;
		JavaToken t;
		int count = -1;

		t = primaryExpression();
		{
			_loop154:
			do {
				switch (LA(1)) {
					case DOT:
						{
							match(DOT);
							{
								switch (LA(1)) {
									case IDENT:
										{
											id = LT(1);
											match(IDENT);
											if (inputState.guessing == 0) {
												if (t != null) t.setText(t.getText() + "." + id.getText());
											}
											break;
										}
									case LITERAL_this:
										{
											match(LITERAL_this);
											if (inputState.guessing == 0) {
												if (t != null) t.setText(t.getText() + ".this");
											}
											break;
										}
									case LITERAL_class:
										{
											match(LITERAL_class);
											if (inputState.guessing == 0) {
												if (t != null) t.setText(t.getText() + ".class");
											}
											break;
										}
									default:
										{
											throw new NoViableAltException(LT(1), getFilename());
										}
								}
							}
							break;
						}
					case LBRACK:
						{
							match(LBRACK);
							expression();
							match(RBRACK);
							break;
						}
					case LPAREN:
						{
							match(LPAREN);
							{
								switch (LA(1)) {
									case LITERAL_void:
									case LITERAL_boolean:
									case LITERAL_byte:
									case LITERAL_char:
									case LITERAL_short:
									case LITERAL_int:
									case LITERAL_float:
									case LITERAL_long:
									case LITERAL_double:
									case IDENT:
									case LPAREN:
									case MINUS:
									case INC:
									case DEC:
									case BNOT:
									case LNOT:
									case LITERAL_this:
									case LITERAL_super:
									case LITERAL_true:
									case LITERAL_false:
									case LITERAL_null:
									case LITERAL_new:
									case NUM_INT:
									case CHAR_LITERAL:
									case STRING_LITERAL:
									case NUM_FLOAT:
										{
											count = expressionList();
											break;
										}
									case RPAREN:
										{
											if (inputState.guessing == 0) {
												count = 0;
											}
											break;
										}
									default:
										{
											throw new NoViableAltException(LT(1), getFilename());
										}
								}
							}
							match(RPAREN);
							if (inputState.guessing == 0) {

								if (t != null)
									t.setParamCount(count);

							}
							break;
						}
					default:
						{
							break _loop154;
						}
				}
			} while (true);
		}
		if (inputState.guessing == 0) {
			if (t != null) reference(t);
		}
		{
			switch (LA(1)) {
				case INC:
					{
						match(INC);
						break;
					}
				case DEC:
					{
						match(DEC);
						break;
					}
				case SEMI:
				case RBRACK:
				case STAR:
				case RCURLY:
				case COMMA:
				case ASSIGN:
				case RPAREN:
				case COLON:
				case PLUS_ASSIGN:
				case MINUS_ASSIGN:
				case STAR_ASSIGN:
				case DIV_ASSIGN:
				case MOD_ASSIGN:
				case SR_ASSIGN:
				case BSR_ASSIGN:
				case SL_ASSIGN:
				case BAND_ASSIGN:
				case BXOR_ASSIGN:
				case BOR_ASSIGN:
				case QUESTION:
				case LOR:
				case LAND:
				case BOR:
				case BXOR:
				case BAND:
				case NOT_EQUAL:
				case EQUAL:
				case LT:
				case GT:
				case LE:
				case GE:
				case SL:
				case SR:
				case BSR:
				case PLUS:
				case MINUS:
				case DIV:
				case MOD:
				case LITERAL_instanceof:
					{
						break;
					}
				default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
			}
		}
	}

	public final JavaToken primaryExpression() throws RecognitionException, TokenStreamException {
		JavaToken t;

		Token id = null;
		Token s = null;
		Token th = null;
		t = null;

		switch (LA(1)) {
			case IDENT:
				{
					id = LT(1);
					match(IDENT);
					if (inputState.guessing == 0) {
						t = (JavaToken) id;
					}
					break;
				}
			case LITERAL_void:
			case LITERAL_boolean:
			case LITERAL_byte:
			case LITERAL_char:
			case LITERAL_short:
			case LITERAL_int:
			case LITERAL_float:
			case LITERAL_long:
			case LITERAL_double:
				{
					t = builtInType();
					match(DOT);
					match(LITERAL_class);
					if (inputState.guessing == 0) {
						t.setText(t.getText() + ".class");
					}
					break;
				}
			case LITERAL_new:
				{
					t = newExpression();
					break;
				}
			case NUM_INT:
			case CHAR_LITERAL:
			case STRING_LITERAL:
			case NUM_FLOAT:
				{
					constant();
					break;
				}
			case LITERAL_super:
				{
					s = LT(1);
					match(LITERAL_super);
					if (inputState.guessing == 0) {
						t = (JavaToken) s;
					}
					break;
				}
			case LITERAL_true:
				{
					match(LITERAL_true);
					break;
				}
			case LITERAL_false:
				{
					match(LITERAL_false);
					break;
				}
			case LITERAL_this:
				{
					th = LT(1);
					match(LITERAL_this);
					if (inputState.guessing == 0) {
						t = (JavaToken) th;
						setNearestClassScope();
					}
					break;
				}
			case LITERAL_null:
				{
					match(LITERAL_null);
					break;
				}
			case LPAREN:
				{
					match(LPAREN);
					expression();
					match(RPAREN);
					break;
				}
			default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
		}
		return t;
	}

	public final JavaToken newExpression() throws RecognitionException, TokenStreamException {
		JavaToken t;

		t = null;
		int count = -1;

		match(LITERAL_new);
		t = type();
		{
			switch (LA(1)) {
				case LPAREN:
					{
						match(LPAREN);
						{
							switch (LA(1)) {
								case LITERAL_void:
								case LITERAL_boolean:
								case LITERAL_byte:
								case LITERAL_char:
								case LITERAL_short:
								case LITERAL_int:
								case LITERAL_float:
								case LITERAL_long:
								case LITERAL_double:
								case IDENT:
								case LPAREN:
								case MINUS:
								case INC:
								case DEC:
								case BNOT:
								case LNOT:
								case LITERAL_this:
								case LITERAL_super:
								case LITERAL_true:
								case LITERAL_false:
								case LITERAL_null:
								case LITERAL_new:
								case NUM_INT:
								case CHAR_LITERAL:
								case STRING_LITERAL:
								case NUM_FLOAT:
									{
										count = expressionList();
										break;
									}
								case RPAREN:
									{
										if (inputState.guessing == 0) {
											count = 0;
										}
										break;
									}
								default:
									{
										throw new NoViableAltException(LT(1), getFilename());
									}
							}
						}
						match(RPAREN);
						if (inputState.guessing == 0) {

							t.setText(t.getText() + ".~constructor~");
							t.setParamCount(count);

						}
						{
							switch (LA(1)) {
								case LCURLY:
									{
										classBlock();
										break;
									}
								case SEMI:
								case LBRACK:
								case RBRACK:
								case DOT:
								case STAR:
								case RCURLY:
								case COMMA:
								case ASSIGN:
								case LPAREN:
								case RPAREN:
								case COLON:
								case PLUS_ASSIGN:
								case MINUS_ASSIGN:
								case STAR_ASSIGN:
								case DIV_ASSIGN:
								case MOD_ASSIGN:
								case SR_ASSIGN:
								case BSR_ASSIGN:
								case SL_ASSIGN:
								case BAND_ASSIGN:
								case BXOR_ASSIGN:
								case BOR_ASSIGN:
								case QUESTION:
								case LOR:
								case LAND:
								case BOR:
								case BXOR:
								case BAND:
								case NOT_EQUAL:
								case EQUAL:
								case LT:
								case GT:
								case LE:
								case GE:
								case SL:
								case SR:
								case BSR:
								case PLUS:
								case MINUS:
								case DIV:
								case MOD:
								case INC:
								case DEC:
								case LITERAL_instanceof:
									{
										break;
									}
								default:
									{
										throw new NoViableAltException(LT(1), getFilename());
									}
							}
						}
						break;
					}
				case LBRACK:
					{
						{
							int _cnt163 = 0;
							_loop163:
							do {
								if ((LA(1) == LBRACK) && (_tokenSet_20.member(LA(2)))) {
									match(LBRACK);
									{
										switch (LA(1)) {
											case LITERAL_void:
											case LITERAL_boolean:
											case LITERAL_byte:
											case LITERAL_char:
											case LITERAL_short:
											case LITERAL_int:
											case LITERAL_float:
											case LITERAL_long:
											case LITERAL_double:
											case IDENT:
											case LPAREN:
											case MINUS:
											case INC:
											case DEC:
											case BNOT:
											case LNOT:
											case LITERAL_this:
											case LITERAL_super:
											case LITERAL_true:
											case LITERAL_false:
											case LITERAL_null:
											case LITERAL_new:
											case NUM_INT:
											case CHAR_LITERAL:
											case STRING_LITERAL:
											case NUM_FLOAT:
												{
													expression();
													break;
												}
											case RBRACK:
												{
													break;
												}
											default:
												{
													throw new NoViableAltException(LT(1), getFilename());
												}
										}
									}
									match(RBRACK);
								} else {
									if (_cnt163 >= 1) {
										break _loop163;
									} else {
										throw new NoViableAltException(LT(1), getFilename());
									}
								}

								_cnt163++;
							} while (true);
						}
						{
							switch (LA(1)) {
								case LCURLY:
									{
										arrayInitializer();
										break;
									}
								case SEMI:
								case LBRACK:
								case RBRACK:
								case DOT:
								case STAR:
								case RCURLY:
								case COMMA:
								case ASSIGN:
								case LPAREN:
								case RPAREN:
								case COLON:
								case PLUS_ASSIGN:
								case MINUS_ASSIGN:
								case STAR_ASSIGN:
								case DIV_ASSIGN:
								case MOD_ASSIGN:
								case SR_ASSIGN:
								case BSR_ASSIGN:
								case SL_ASSIGN:
								case BAND_ASSIGN:
								case BXOR_ASSIGN:
								case BOR_ASSIGN:
								case QUESTION:
								case LOR:
								case LAND:
								case BOR:
								case BXOR:
								case BAND:
								case NOT_EQUAL:
								case EQUAL:
								case LT:
								case GT:
								case LE:
								case GE:
								case SL:
								case SR:
								case BSR:
								case PLUS:
								case MINUS:
								case DIV:
								case MOD:
								case INC:
								case DEC:
								case LITERAL_instanceof:
									{
										break;
									}
								default:
									{
										throw new NoViableAltException(LT(1), getFilename());
									}
							}
						}
						break;
					}
				default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
			}
		}
		return t;
	}

	public final void constant() throws RecognitionException, TokenStreamException {


		switch (LA(1)) {
			case NUM_INT:
				{
					match(NUM_INT);
					break;
				}
			case CHAR_LITERAL:
				{
					match(CHAR_LITERAL);
					break;
				}
			case STRING_LITERAL:
				{
					match(STRING_LITERAL);
					break;
				}
			case NUM_FLOAT:
				{
					match(NUM_FLOAT);
					break;
				}
			default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
		}
	}


	public static final String[] _tokenNames = {"<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "\"package\"", "SEMI", "\"import\"", "LBRACK", "RBRACK", "\"void\"", "\"boolean\"", "\"byte\"", "\"char\"", "\"short\"", "\"int\"", "\"float\"", "\"long\"", "\"double\"", "IDENT", "DOT", "STAR", "\"private\"", "\"public\"", "\"protected\"", "\"static\"", "\"transient\"", "\"final\"", "\"abstract\"", "\"native\"", "\"threadsafe\"", "\"synchronized\"", "\"const\"", "\"class\"", "\"extends\"", "\"interface\"", "LCURLY", "RCURLY", "COMMA", "\"implements\"", "ASSIGN", "LPAREN", "RPAREN", "\"throws\"", "COLON", "\"if\"", "\"else\"", "\"for\"", "\"while\"", "\"do\"", "\"break\"", "\"continue\"", "\"return\"", "\"switch\"", "\"case\"", "\"default\"", "\"throw\"", "\"try\"", "\"finally\"", "\"catch\"", "PLUS_ASSIGN", "MINUS_ASSIGN", "STAR_ASSIGN", "DIV_ASSIGN", "MOD_ASSIGN", "SR_ASSIGN", "BSR_ASSIGN", "SL_ASSIGN", "BAND_ASSIGN", "BXOR_ASSIGN", "BOR_ASSIGN", "QUESTION", "LOR", "LAND", "BOR", "BXOR", "BAND", "NOT_EQUAL", "EQUAL", "LT", "GT", "LE", "GE", "SL", "SR", "BSR", "PLUS", "MINUS", "DIV", "MOD", "INC", "DEC", "BNOT", "LNOT", "\"instanceof\"", "\"this\"", "\"super\"", "\"true\"", "\"false\"", "\"null\"", "\"new\"", "NUM_INT", "CHAR_LITERAL", "STRING_LITERAL", "NUM_FLOAT", "WS", "SL_COMMENT", "ML_COMMENT", "ESC", "HEX_DIGIT", "VOCAB", "EXPONENT", "FLOAT_SUFFIX"};

	private static final long _tokenSet_0_data_[] = {25767706656L, 0L};
	public static final BitSet _tokenSet_0 = new BitSet(_tokenSet_0_data_);
	private static final long _tokenSet_1_data_[] = {25767706722L, 0L};
	public static final BitSet _tokenSet_1 = new BitSet(_tokenSet_1_data_);
	private static final long _tokenSet_2_data_[] = {25767706658L, 0L};
	public static final BitSet _tokenSet_2 = new BitSet(_tokenSet_2_data_);
	private static final long _tokenSet_3_data_[] = {25768230400L, 0L};
	public static final BitSet _tokenSet_3 = new BitSet(_tokenSet_3_data_);
	private static final long _tokenSet_4_data_[] = {1125280382592L, 0L};
	public static final BitSet _tokenSet_4 = new BitSet(_tokenSet_4_data_);
	private static final long _tokenSet_5_data_[] = {786560L, 0L};
	public static final BitSet _tokenSet_5 = new BitSet(_tokenSet_5_data_);
	private static final long _tokenSet_6_data_[] = {687194767520L, 0L};
	public static final BitSet _tokenSet_6 = new BitSet(_tokenSet_6_data_);
	private static final long _tokenSet_7_data_[] = {117041951918259744L, 1098945396736L, 0L, 0L};
	public static final BitSet _tokenSet_7 = new BitSet(_tokenSet_7_data_);
	private static final long _tokenSet_8_data_[] = {1133871889920L, 1098945396736L, 0L, 0L};
	public static final BitSet _tokenSet_8 = new BitSet(_tokenSet_8_data_);
	private static final long _tokenSet_9_data_[] = {144098802774048288L, 1098945396736L, 0L, 0L};
	public static final BitSet _tokenSet_9 = new BitSet(_tokenSet_9_data_);
	private static final long _tokenSet_10_data_[] = {-7017976562016L, 1099511627775L, 0L, 0L};
	public static final BitSet _tokenSet_10 = new BitSet(_tokenSet_10_data_);
	private static final long _tokenSet_11_data_[] = {9895605173760L, 1098945396736L, 0L, 0L};
	public static final BitSet _tokenSet_11 = new BitSet(_tokenSet_11_data_);
	private static final long _tokenSet_12_data_[] = {4293393920L, 0L};
	public static final BitSet _tokenSet_12 = new BitSet(_tokenSet_12_data_);
	private static final long _tokenSet_13_data_[] = {4293918336L, 0L};
	public static final BitSet _tokenSet_13 = new BitSet(_tokenSet_13_data_);
	private static final long _tokenSet_14_data_[] = {1099512151552L, 1098945396736L, 0L, 0L};
	public static final BitSet _tokenSet_14 = new BitSet(_tokenSet_14_data_);
	private static final long _tokenSet_15_data_[] = {-576459103033885024L, 1099511627775L, 0L, 0L};
	public static final BitSet _tokenSet_15 = new BitSet(_tokenSet_15_data_);
	private static final long _tokenSet_16_data_[] = {-576458965594931552L, 1099511627775L, 0L, 0L};
	public static final BitSet _tokenSet_16 = new BitSet(_tokenSet_16_data_);
	private static final long _tokenSet_17_data_[] = {1048576L, 25165824L, 0L, 0L};
	public static final BitSet _tokenSet_17 = new BitSet(_tokenSet_17_data_);
	private static final long _tokenSet_18_data_[] = {1099512151552L, 1098437885952L, 0L, 0L};
	public static final BitSet _tokenSet_18 = new BitSet(_tokenSet_18_data_);
	private static final long _tokenSet_19_data_[] = {-576447901759176800L, 1099511627775L, 0L, 0L};
	public static final BitSet _tokenSet_19 = new BitSet(_tokenSet_19_data_);
	private static final long _tokenSet_20_data_[] = {1099512151808L, 1098945396736L, 0L, 0L};
	public static final BitSet _tokenSet_20 = new BitSet(_tokenSet_20_data_);

}
