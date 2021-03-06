// $ANTLR 2.7.1: "java.g" -> "JavaLexer.java"$

package JavaParser;

import antlr.*;
import antlr.collections.impl.BitSet;

import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;

public class JavaLexer extends antlr.CharScanner implements JavaXrefTokenTypes, TokenStream {
	public JavaLexer(InputStream in) {
		this(new ByteBuffer(in));
	}

	public JavaLexer(Reader in) {
		this(new CharBuffer(in));
	}

	public JavaLexer(InputBuffer ib) {
		this(new LexerSharedInputState(ib));
	}

	public JavaLexer(LexerSharedInputState state) {
		super(state);
		literals = new Hashtable();
		literals.put(new ANTLRHashString("package", this), new Integer(4));

		literals.put(new ANTLRHashString("import", this), new Integer(6));

		literals.put(new ANTLRHashString("void", this), new Integer(9));
		literals.put(new ANTLRHashString("boolean", this), new Integer(10));
		literals.put(new ANTLRHashString("byte", this), new Integer(11));
		literals.put(new ANTLRHashString("char", this), new Integer(12));
		literals.put(new ANTLRHashString("short", this), new Integer(13));
		literals.put(new ANTLRHashString("int", this), new Integer(14));
		literals.put(new ANTLRHashString("float", this), new Integer(15));
		literals.put(new ANTLRHashString("long", this), new Integer(16));
		literals.put(new ANTLRHashString("double", this), new Integer(17));

		literals.put(new ANTLRHashString("private", this), new Integer(21));
		literals.put(new ANTLRHashString("public", this), new Integer(22));
		literals.put(new ANTLRHashString("protected", this), new Integer(23));
		literals.put(new ANTLRHashString("static", this), new Integer(24));
		literals.put(new ANTLRHashString("transient", this), new Integer(25));
		literals.put(new ANTLRHashString("final", this), new Integer(26));
		literals.put(new ANTLRHashString("abstract", this), new Integer(27));
		literals.put(new ANTLRHashString("native", this), new Integer(28));
		literals.put(new ANTLRHashString("threadsafe", this), new Integer(29)); //??what is this?
		literals.put(new ANTLRHashString("synchronized", this), new Integer(30));
		literals.put(new ANTLRHashString("const", this), new Integer(31));
		literals.put(new ANTLRHashString("class", this), new Integer(32));
		literals.put(new ANTLRHashString("extends", this), new Integer(33));
		literals.put(new ANTLRHashString("interface", this), new Integer(34));

		literals.put(new ANTLRHashString("implements", this), new Integer(38));

		literals.put(new ANTLRHashString("throws", this), new Integer(42));

		literals.put(new ANTLRHashString("if", this), new Integer(44));
		literals.put(new ANTLRHashString("else", this), new Integer(45));
		literals.put(new ANTLRHashString("for", this), new Integer(46));
		literals.put(new ANTLRHashString("while", this), new Integer(47));
		literals.put(new ANTLRHashString("do", this), new Integer(48));
		literals.put(new ANTLRHashString("break", this), new Integer(49));
		literals.put(new ANTLRHashString("continue", this), new Integer(50));
		literals.put(new ANTLRHashString("return", this), new Integer(51));
		literals.put(new ANTLRHashString("switch", this), new Integer(52));
		literals.put(new ANTLRHashString("case", this), new Integer(53));
		literals.put(new ANTLRHashString("default", this), new Integer(54));
		literals.put(new ANTLRHashString("throw", this), new Integer(55));
		literals.put(new ANTLRHashString("try", this), new Integer(56));
		literals.put(new ANTLRHashString("finally", this), new Integer(57));
		literals.put(new ANTLRHashString("catch", this), new Integer(58));

		literals.put(new ANTLRHashString("instanceof", this), new Integer(93));
		literals.put(new ANTLRHashString("this", this), new Integer(94));
		literals.put(new ANTLRHashString("super", this), new Integer(95));
		literals.put(new ANTLRHashString("true", this), new Integer(96));
		literals.put(new ANTLRHashString("false", this), new Integer(97));
		literals.put(new ANTLRHashString("null", this), new Integer(98));
		literals.put(new ANTLRHashString("new", this), new Integer(99));

		//added by Qurtach:
		//		literals.put(new ANTLRHashString("strictfp", this), new Integer(35));
		//		literals.put(new ANTLRHashString("volatile", this), new Integer(36));
		//		literals.put(new ANTLRHashString("goto", this), new Integer(59));
		//		literals.put(new ANTLRHashString("assert", this), new Integer(92));

		caseSensitiveLiterals = true;
		setCaseSensitive(true);
	}

	public Token nextToken() throws TokenStreamException {
		Token theRetToken = null;
		tryAgain:
			for (; ;) {
				Token _token = null;
				int _ttype = Token.INVALID_TYPE;
				resetText();
				try {   // for char stream error handling
					try {   // for lexical error handling
						switch (LA(1)) {
							case '?':
								{
									mQUESTION(true);
									theRetToken = _returnToken;
									break;
								}
							case '(':
								{
									mLPAREN(true);
									theRetToken = _returnToken;
									break;
								}
							case ')':
								{
									mRPAREN(true);
									theRetToken = _returnToken;
									break;
								}
							case '[':
								{
									mLBRACK(true);
									theRetToken = _returnToken;
									break;
								}
							case ']':
								{
									mRBRACK(true);
									theRetToken = _returnToken;
									break;
								}
							case '{':
								{
									mLCURLY(true);
									theRetToken = _returnToken;
									break;
								}
							case '}':
								{
									mRCURLY(true);
									theRetToken = _returnToken;
									break;
								}
							case ':':
								{
									mCOLON(true);
									theRetToken = _returnToken;
									break;
								}
							case ',':
								{
									mCOMMA(true);
									theRetToken = _returnToken;
									break;
								}
							case '~':
								{
									mBNOT(true);
									theRetToken = _returnToken;
									break;
								}
							case ';':
								{
									mSEMI(true);
									theRetToken = _returnToken;
									break;
								}
							case '\t':
							case '\n':
							case '\u000c':
							case '\r':
							case ' ':
								{
									mWS(true);
									theRetToken = _returnToken;
									break;
								}
							case '\'':
								{
									mCHAR_LITERAL(true);
									theRetToken = _returnToken;
									break;
								}
							case '"':
								{
									mSTRING_LITERAL(true);
									theRetToken = _returnToken;
									break;
								}
							case '$':
							case 'A':
							case 'B':
							case 'C':
							case 'D':
							case 'E':
							case 'F':
							case 'G':
							case 'H':
							case 'I':
							case 'J':
							case 'K':
							case 'L':
							case 'M':
							case 'N':
							case 'O':
							case 'P':
							case 'Q':
							case 'R':
							case 'S':
							case 'T':
							case 'U':
							case 'V':
							case 'W':
							case 'X':
							case 'Y':
							case 'Z':
							case '_':
							case 'a':
							case 'b':
							case 'c':
							case 'd':
							case 'e':
							case 'f':
							case 'g':
							case 'h':
							case 'i':
							case 'j':
							case 'k':
							case 'l':
							case 'm':
							case 'n':
							case 'o':
							case 'p':
							case 'q':
							case 'r':
							case 's':
							case 't':
							case 'u':
							case 'v':
							case 'w':
							case 'x':
							case 'y':
							case 'z':
								{
									mIDENT(true);
									theRetToken = _returnToken;
									break;
								}
							case '.':
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								{
									mNUM_INT(true);
									theRetToken = _returnToken;
									break;
								}
							default:
								if ((LA(1) == '>') && (LA(2) == '>') && (LA(3) == '>') && (LA(4) == '=')) {
									mBSR_ASSIGN(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '>') && (LA(2) == '>') && (LA(3) == '=')) {
									mSR_ASSIGN(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '>') && (LA(2) == '>') && (LA(3) == '>') && (true)) {
									mBSR(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '<') && (LA(2) == '<') && (LA(3) == '=')) {
									mSL_ASSIGN(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '=') && (LA(2) == '=')) {
									mEQUAL(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '!') && (LA(2) == '=')) {
									mNOT_EQUAL(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '/') && (LA(2) == '=')) {
									mDIV_ASSIGN(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '+') && (LA(2) == '=')) {
									mPLUS_ASSIGN(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '+') && (LA(2) == '+')) {
									mINC(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '-') && (LA(2) == '=')) {
									mMINUS_ASSIGN(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '-') && (LA(2) == '-')) {
									mDEC(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '*') && (LA(2) == '=')) {
									mSTAR_ASSIGN(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '%') && (LA(2) == '=')) {
									mMOD_ASSIGN(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '>') && (LA(2) == '>') && (true)) {
									mSR(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '>') && (LA(2) == '=')) {
									mGE(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '<') && (LA(2) == '<') && (true)) {
									mSL(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '<') && (LA(2) == '=')) {
									mLE(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '^') && (LA(2) == '=')) {
									mBXOR_ASSIGN(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '|') && (LA(2) == '=')) {
									mBOR_ASSIGN(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '|') && (LA(2) == '|')) {
									mLOR(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '&') && (LA(2) == '=')) {
									mBAND_ASSIGN(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '&') && (LA(2) == '&')) {
									mLAND(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '/') && (LA(2) == '/')) {
									mSL_COMMENT(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '/') && (LA(2) == '*')) {
									mML_COMMENT(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '=') && (true)) {
									mASSIGN(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '!') && (true)) {
									mLNOT(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '/') && (true)) {
									mDIV(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '+') && (true)) {
									mPLUS(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '-') && (true)) {
									mMINUS(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '*') && (true)) {
									mSTAR(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '%') && (true)) {
									mMOD(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '>') && (true)) {
									mGT(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '<') && (true)) {
									mLT(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '^') && (true)) {
									mBXOR(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '|') && (true)) {
									mBOR(true);
									theRetToken = _returnToken;
								} else if ((LA(1) == '&') && (true)) {
									mBAND(true);
									theRetToken = _returnToken;
								} else {
									if (LA(1) == EOF_CHAR) {
										uponEOF();
										_returnToken = makeToken(Token.EOF_TYPE);
									} else {
										throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
									}
								}
						}
						if (_returnToken == null) continue tryAgain; // found SKIP token
						_ttype = _returnToken.getType();
						_returnToken.setType(_ttype);
						return _returnToken;
					} catch (RecognitionException e) {
						throw new TokenStreamRecognitionException(e);
					}
				} catch (CharStreamException cse) {
					if (cse instanceof CharStreamIOException) {
						throw new TokenStreamIOException(((CharStreamIOException) cse).io);
					} else {
						throw new TokenStreamException(cse.getMessage());
					}
				}
			}
	}

	public final void mQUESTION(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = QUESTION;
		int _saveIndex;

		match('?');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mLPAREN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = LPAREN;
		int _saveIndex;

		match('(');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mRPAREN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = RPAREN;
		int _saveIndex;

		match(')');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mLBRACK(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = LBRACK;
		int _saveIndex;

		match('[');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mRBRACK(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = RBRACK;
		int _saveIndex;

		match(']');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mLCURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = LCURLY;
		int _saveIndex;

		match('{');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mRCURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = RCURLY;
		int _saveIndex;

		match('}');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mCOLON(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = COLON;
		int _saveIndex;

		match(':');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mCOMMA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = COMMA;
		int _saveIndex;

		match(',');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = ASSIGN;
		int _saveIndex;

		match('=');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mEQUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = EQUAL;
		int _saveIndex;

		match("==");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mLNOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = LNOT;
		int _saveIndex;

		match('!');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mBNOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = BNOT;
		int _saveIndex;

		match('~');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mNOT_EQUAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = NOT_EQUAL;
		int _saveIndex;

		match("!=");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mDIV(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = DIV;
		int _saveIndex;

		match('/');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mDIV_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = DIV_ASSIGN;
		int _saveIndex;

		match("/=");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mPLUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = PLUS;
		int _saveIndex;

		match('+');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mPLUS_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = PLUS_ASSIGN;
		int _saveIndex;

		match("+=");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mINC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = INC;
		int _saveIndex;

		match("++");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mMINUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = MINUS;
		int _saveIndex;

		match('-');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mMINUS_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = MINUS_ASSIGN;
		int _saveIndex;

		match("-=");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mDEC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = DEC;
		int _saveIndex;

		match("--");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mSTAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = STAR;
		int _saveIndex;

		match('*');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mSTAR_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = STAR_ASSIGN;
		int _saveIndex;

		match("*=");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mMOD(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = MOD;
		int _saveIndex;

		match('%');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mMOD_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = MOD_ASSIGN;
		int _saveIndex;

		match("%=");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mSR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = SR;
		int _saveIndex;

		match(">>");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mSR_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = SR_ASSIGN;
		int _saveIndex;

		match(">>=");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mBSR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = BSR;
		int _saveIndex;

		match(">>>");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mBSR_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = BSR_ASSIGN;
		int _saveIndex;

		match(">>>=");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mGE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = GE;
		int _saveIndex;

		match(">=");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mGT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = GT;
		int _saveIndex;

		match(">");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mSL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = SL;
		int _saveIndex;

		match("<<");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mSL_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = SL_ASSIGN;
		int _saveIndex;

		match("<<=");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mLE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = LE;
		int _saveIndex;

		match("<=");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mLT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = LT;
		int _saveIndex;

		match('<');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mBXOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = BXOR;
		int _saveIndex;

		match('^');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mBXOR_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = BXOR_ASSIGN;
		int _saveIndex;

		match("^=");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mBOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = BOR;
		int _saveIndex;

		match('|');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mBOR_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = BOR_ASSIGN;
		int _saveIndex;

		match("|=");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mLOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = LOR;
		int _saveIndex;

		match("||");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mBAND(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = BAND;
		int _saveIndex;

		match('&');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mBAND_ASSIGN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = BAND_ASSIGN;
		int _saveIndex;

		match("&=");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mLAND(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = LAND;
		int _saveIndex;

		match("&&");
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mSEMI(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = SEMI;
		int _saveIndex;

		match(';');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mWS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = WS;
		int _saveIndex;

		{
			switch (LA(1)) {
				case ' ':
					{
						match(' ');
						break;
					}
				case '\t':
					{
						match('\t');
						break;
					}
				case '\u000c':
					{
						match('\f');
						break;
					}
				case '\n':
				case '\r':
					{
						{
							if ((LA(1) == '\r') && (LA(2) == '\n')) {
								match("\r\n");
							} else if ((LA(1) == '\r') && (true)) {
								match('\r');
							} else if ((LA(1) == '\n')) {
								match('\n');
							} else {
								throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
							}

						}
						newline();
						break;
					}
				default:
					{
						throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
					}
			}
		}
		_ttype = Token.SKIP;
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mSL_COMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = SL_COMMENT;
		int _saveIndex;

		match("//");
		{
			_loop216:
			do {
				if ((_tokenSet_0.member(LA(1)))) {
					matchNot('\n');
				} else {
					break _loop216;
				}

			} while (true);
		}
		match('\n');
		_ttype = Token.SKIP;
		newline();
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mML_COMMENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = ML_COMMENT;
		int _saveIndex;

		match("/*");
		{
			_loop220:
			do {
				if (((LA(1) == '*') && ((LA(2) >= '\u0003' && LA(2) <= '\u00ff')) && ((LA(3) >= '\u0003' && LA(3) <= '\u00ff'))) && (LA(2) != '/')) {
					match('*');
				} else if ((LA(1) == '\n')) {
					match('\n');
					newline();
				} else if ((_tokenSet_1.member(LA(1)))) {
					{
						match(_tokenSet_1);
					}
				} else {
					break _loop220;
				}

			} while (true);
		}
		match("*/");
		_ttype = Token.SKIP;
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mCHAR_LITERAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = CHAR_LITERAL;
		int _saveIndex;

		match('\'');
		{
			if ((LA(1) == '\\')) {
				mESC(false);
			} else if ((_tokenSet_2.member(LA(1)))) {
				matchNot('\'');
			} else {
				throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
			}

		}
		match('\'');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	protected final void mESC(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = ESC;
		int _saveIndex;

		match('\\');
		{
			switch (LA(1)) {
				case 'n':
					{
						match('n');
						break;
					}
				case 'r':
					{
						match('r');
						break;
					}
				case 't':
					{
						match('t');
						break;
					}
				case 'b':
					{
						match('b');
						break;
					}
				case 'f':
					{
						match('f');
						break;
					}
				case '"':
					{
						match('"');
						break;
					}
				case '\'':
					{
						match('\'');
						break;
					}
				case '\\':
					{
						match('\\');
						break;
					}
				case 'u':
					{
						{
							int _cnt229 = 0;
							_loop229:
							do {
								if ((LA(1) == 'u')) {
									match('u');
								} else {
									if (_cnt229 >= 1) {
										break _loop229;
									} else {
										throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
									}
								}

								_cnt229++;
							} while (true);
						}
						mHEX_DIGIT(false);
						mHEX_DIGIT(false);
						mHEX_DIGIT(false);
						mHEX_DIGIT(false);
						break;
					}
				case '0':
				case '1':
				case '2':
				case '3':
					{
						{
							matchRange('0', '3');
						}
						{
							if (((LA(1) >= '0' && LA(1) <= '9')) && ((LA(2) >= '\u0003' && LA(2) <= '\u00ff')) && (true) && (true)) {
								{
									matchRange('0', '9');
								}
								{
									if (((LA(1) >= '0' && LA(1) <= '9')) && ((LA(2) >= '\u0003' && LA(2) <= '\u00ff')) && (true) && (true)) {
										matchRange('0', '9');
									} else if (((LA(1) >= '\u0003' && LA(1) <= '\u00ff')) && (true) && (true) && (true)) {
									} else {
										throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
									}

								}
							} else if (((LA(1) >= '\u0003' && LA(1) <= '\u00ff')) && (true) && (true) && (true)) {
							} else {
								throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
							}

						}
						break;
					}
				case '4':
				case '5':
				case '6':
				case '7':
					{
						{
							matchRange('4', '7');
						}
						{
							if (((LA(1) >= '0' && LA(1) <= '9')) && ((LA(2) >= '\u0003' && LA(2) <= '\u00ff')) && (true) && (true)) {
								matchRange('0', '9');
							} else if (((LA(1) >= '\u0003' && LA(1) <= '\u00ff')) && (true) && (true) && (true)) {
							} else {
								throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
							}

						}
						break;
					}
				default:
					{
						throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
					}
			}
		}
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mSTRING_LITERAL(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = STRING_LITERAL;
		int _saveIndex;

		match('"');
		{
			_loop225:
			do {
				if ((LA(1) == '\\')) {
					mESC(false);
				} else if ((_tokenSet_3.member(LA(1)))) {
					matchNot('"');
				} else {
					break _loop225;
				}

			} while (true);
		}
		match('"');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	protected final void mHEX_DIGIT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = HEX_DIGIT;
		int _saveIndex;

		{
			switch (LA(1)) {
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					{
						matchRange('0', '9');
						break;
					}
				case 'A':
				case 'B':
				case 'C':
				case 'D':
				case 'E':
				case 'F':
					{
						matchRange('A', 'F');
						break;
					}
				case 'a':
				case 'b':
				case 'c':
				case 'd':
				case 'e':
				case 'f':
					{
						matchRange('a', 'f');
						break;
					}
				default:
					{
						throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
					}
			}
		}
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	protected final void mVOCAB(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = VOCAB;
		int _saveIndex;

		matchRange('\3', '\377');
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mIDENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = IDENT;
		int _saveIndex;

		{
			switch (LA(1)) {
				case 'a':
				case 'b':
				case 'c':
				case 'd':
				case 'e':
				case 'f':
				case 'g':
				case 'h':
				case 'i':
				case 'j':
				case 'k':
				case 'l':
				case 'm':
				case 'n':
				case 'o':
				case 'p':
				case 'q':
				case 'r':
				case 's':
				case 't':
				case 'u':
				case 'v':
				case 'w':
				case 'x':
				case 'y':
				case 'z':
					{
						matchRange('a', 'z');
						break;
					}
				case 'A':
				case 'B':
				case 'C':
				case 'D':
				case 'E':
				case 'F':
				case 'G':
				case 'H':
				case 'I':
				case 'J':
				case 'K':
				case 'L':
				case 'M':
				case 'N':
				case 'O':
				case 'P':
				case 'Q':
				case 'R':
				case 'S':
				case 'T':
				case 'U':
				case 'V':
				case 'W':
				case 'X':
				case 'Y':
				case 'Z':
					{
						matchRange('A', 'Z');
						break;
					}
				case '_':
					{
						match('_');
						break;
					}
				case '$':
					{
						match('$');
						break;
					}
				default:
					{
						throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
					}
			}
		}
		{
			_loop242:
			do {
				switch (LA(1)) {
					case 'a':
					case 'b':
					case 'c':
					case 'd':
					case 'e':
					case 'f':
					case 'g':
					case 'h':
					case 'i':
					case 'j':
					case 'k':
					case 'l':
					case 'm':
					case 'n':
					case 'o':
					case 'p':
					case 'q':
					case 'r':
					case 's':
					case 't':
					case 'u':
					case 'v':
					case 'w':
					case 'x':
					case 'y':
					case 'z':
						{
							matchRange('a', 'z');
							break;
						}
					case 'A':
					case 'B':
					case 'C':
					case 'D':
					case 'E':
					case 'F':
					case 'G':
					case 'H':
					case 'I':
					case 'J':
					case 'K':
					case 'L':
					case 'M':
					case 'N':
					case 'O':
					case 'P':
					case 'Q':
					case 'R':
					case 'S':
					case 'T':
					case 'U':
					case 'V':
					case 'W':
					case 'X':
					case 'Y':
					case 'Z':
						{
							matchRange('A', 'Z');
							break;
						}
					case '_':
						{
							match('_');
							break;
						}
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9':
						{
							matchRange('0', '9');
							break;
						}
					case '$':
						{
							match('$');
							break;
						}
					default:
						{
							break _loop242;
						}
				}
			} while (true);
		}
		_ttype = testLiteralsTable(_ttype);
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mNUM_INT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = NUM_INT;
		int _saveIndex;
		boolean isDecimal = false;

		switch (LA(1)) {
			case '.':
				{
					match('.');
					_ttype = DOT;
					{
						if (((LA(1) >= '0' && LA(1) <= '9'))) {
							{
								int _cnt246 = 0;
								_loop246:
								do {
									if (((LA(1) >= '0' && LA(1) <= '9'))) {
										matchRange('0', '9');
									} else {
										if (_cnt246 >= 1) {
											break _loop246;
										} else {
											throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
										}
									}

									_cnt246++;
								} while (true);
							}
							{
								if ((LA(1) == 'E' || LA(1) == 'e')) {
									mEXPONENT(false);
								} else {
								}

							}
							{
								if ((_tokenSet_4.member(LA(1)))) {
									mFLOAT_SUFFIX(false);
								} else {
								}

							}
							_ttype = NUM_FLOAT;
						} else {
						}

					}
					break;
				}
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				{
					{
						switch (LA(1)) {
							case '0':
								{
									match('0');
									isDecimal = true;
									{
										switch (LA(1)) {
											case 'X':
											case 'x':
												{
													{
														switch (LA(1)) {
															case 'x':
																{
																	match('x');
																	break;
																}
															case 'X':
																{
																	match('X');
																	break;
																}
															default:
																{
																	throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
																}
														}
													}
													{
														int _cnt253 = 0;
														_loop253:
														do {
															if ((_tokenSet_5.member(LA(1))) && (true) && (true) && (true)) {
																mHEX_DIGIT(false);
															} else {
																if (_cnt253 >= 1) {
																	break _loop253;
																} else {
																	throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
																}
															}

															_cnt253++;
														} while (true);
													}
													break;
												}
											case '0':
											case '1':
											case '2':
											case '3':
											case '4':
											case '5':
											case '6':
											case '7':
											case '8':
												{
													{
														int _cnt255 = 0;
														_loop255:
														do {
															if (((LA(1) >= '0' && LA(1) <= '8'))) {
																matchRange('0', '8');
															} else {
																if (_cnt255 >= 1) {
																	break _loop255;
																} else {
																	throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
																}
															}

															_cnt255++;
														} while (true);
													}
													break;
												}
											default:
												{
												}
										}
									}
									break;
								}
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								{
									{
										matchRange('1', '9');
									}
									{
										_loop258:
										do {
											if (((LA(1) >= '0' && LA(1) <= '9'))) {
												matchRange('0', '9');
											} else {
												break _loop258;
											}

										} while (true);
									}
									isDecimal = true;
									break;
								}
							default:
								{
									throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
								}
						}
					}
					{
						if ((LA(1) == 'L' || LA(1) == 'l')) {
							{
								switch (LA(1)) {
									case 'l':
										{
											match('l');
											break;
										}
									case 'L':
										{
											match('L');
											break;
										}
									default:
										{
											throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
										}
								}
							}
						} else if (((_tokenSet_6.member(LA(1)))) && (isDecimal)) {
							{
								switch (LA(1)) {
									case '.':
										{
											match('.');
											{
												_loop263:
												do {
													if (((LA(1) >= '0' && LA(1) <= '9'))) {
														matchRange('0', '9');
													} else {
														break _loop263;
													}

												} while (true);
											}
											{
												if ((LA(1) == 'E' || LA(1) == 'e')) {
													mEXPONENT(false);
												} else {
												}

											}
											{
												if ((_tokenSet_4.member(LA(1)))) {
													mFLOAT_SUFFIX(false);
												} else {
												}

											}
											break;
										}
									case 'E':
									case 'e':
										{
											mEXPONENT(false);
											{
												if ((_tokenSet_4.member(LA(1)))) {
													mFLOAT_SUFFIX(false);
												} else {
												}

											}
											break;
										}
									case 'D':
									case 'F':
									case 'd':
									case 'f':
										{
											mFLOAT_SUFFIX(false);
											break;
										}
									default:
										{
											throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
										}
								}
							}
							_ttype = NUM_FLOAT;
						} else {
						}

					}
					break;
				}
			default:
				{
					throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
				}
		}
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	protected final void mEXPONENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = EXPONENT;
		int _saveIndex;

		{
			switch (LA(1)) {
				case 'e':
					{
						match('e');
						break;
					}
				case 'E':
					{
						match('E');
						break;
					}
				default:
					{
						throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
					}
			}
		}
		{
			switch (LA(1)) {
				case '+':
					{
						match('+');
						break;
					}
				case '-':
					{
						match('-');
						break;
					}
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					{
						break;
					}
				default:
					{
						throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
					}
			}
		}
		{
			int _cnt271 = 0;
			_loop271:
			do {
				if (((LA(1) >= '0' && LA(1) <= '9'))) {
					matchRange('0', '9');
				} else {
					if (_cnt271 >= 1) {
						break _loop271;
					} else {
						throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
					}
				}

				_cnt271++;
			} while (true);
		}
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	protected final void mFLOAT_SUFFIX(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		int _begin = text.length();
		_ttype = FLOAT_SUFFIX;
		int _saveIndex;

		switch (LA(1)) {
			case 'f':
				{
					match('f');
					break;
				}
			case 'F':
				{
					match('F');
					break;
				}
			case 'd':
				{
					match('d');
					break;
				}
			case 'D':
				{
					match('D');
					break;
				}
			default:
				{
					throw new NoViableAltForCharException((char) LA(1), getFilename(), getLine());
				}
		}
		if (_createToken && _token == null && _ttype != Token.SKIP) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}


	private static final long _tokenSet_0_data_[] = {-1032L, -1L, -1L, -1L, 0L, 0L, 0L, 0L};
	public static final BitSet _tokenSet_0 = new BitSet(_tokenSet_0_data_);
	private static final long _tokenSet_1_data_[] = {-4398046512136L, -1L, -1L, -1L, 0L, 0L, 0L, 0L};
	public static final BitSet _tokenSet_1 = new BitSet(_tokenSet_1_data_);
	private static final long _tokenSet_2_data_[] = {-549755813896L, -268435457L, -1L, -1L, 0L, 0L, 0L, 0L};
	public static final BitSet _tokenSet_2 = new BitSet(_tokenSet_2_data_);
	private static final long _tokenSet_3_data_[] = {-17179869192L, -268435457L, -1L, -1L, 0L, 0L, 0L, 0L};
	public static final BitSet _tokenSet_3 = new BitSet(_tokenSet_3_data_);
	private static final long _tokenSet_4_data_[] = {0L, 343597383760L, 0L, 0L, 0L};
	public static final BitSet _tokenSet_4 = new BitSet(_tokenSet_4_data_);
	private static final long _tokenSet_5_data_[] = {287948901175001088L, 541165879422L, 0L, 0L, 0L};
	public static final BitSet _tokenSet_5 = new BitSet(_tokenSet_5_data_);
	private static final long _tokenSet_6_data_[] = {70368744177664L, 481036337264L, 0L, 0L, 0L};
	public static final BitSet _tokenSet_6 = new BitSet(_tokenSet_6_data_);

}
