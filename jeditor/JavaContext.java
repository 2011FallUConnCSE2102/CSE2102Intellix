/*

package jeditor;


import javax.swing.text.*;
import java.awt.*;

/**
public class JavaContext extends StyleContext implements ViewFactory {

	/**
	public JavaContext() {
		super();
		Style root = getStyle(DEFAULT_STYLE);
		tokenStyles = new Style[Token.MaximumScanValue + 1];
		Token[] tokens = Token.all;
		int n = tokens.length;
		for (int i = 0; i < n; i++) {
			Token t = tokens[i];
			Style parent = getStyle(t.getCategory());
			if (parent == null) {
				parent = addStyle(t.getCategory(), root);
			}
			Style s = addStyle(null, parent);
			s.addAttribute(Token.TokenAttribute, t);
			tokenStyles[t.getScanValue()] = s;

		}
	}

	/**
	public Color getForeground(int code) {
		if (tokenColors == null) {
			tokenColors = new Color[Token.MaximumScanValue + 1];
		}
		if ((code >= 0) && (code < tokenColors.length)) {
			Color c = tokenColors[code];
			if (c == null) {
				Style s = tokenStyles[code];
				//update by Qurtach in despair :)
				if (s != null) {
					c = StyleConstants.getForeground(s);
				} else {
					c = new Color(0x848484);
				}

			}
			return c;
		}
		return Color.black;
	}

	/**
	public Font getFont(int code) {
		if (tokenFonts == null) {
			tokenFonts = new Font[Token.MaximumScanValue + 1];
		}
		if (code < tokenFonts.length) {
			Font f = tokenFonts[code];
			if (f == null) {
				Style s = tokenStyles[code];
				f = getFont(s);
			}
			return f;
		}
		return null;
	}

	/**
	public Style getStyleForScanValue(int code) {
		if (code < tokenStyles.length) {
			return tokenStyles[code];
		}
		return null;
	}
		return new JavaView(elem);
	}
	Style[] tokenStyles;

	/**
	transient Color[] tokenColors;

	/**
	transient Font[] tokenFonts;

	/**
	class JavaView extends WrappedPlainView {

		/**
		JavaView(Element elem) {
			super(elem);
			JavaDocument doc = (JavaDocument) getDocument();
			lexer = doc.createScanner();
			lexerValid = false;
		}

		/**
		public void paint(Graphics g, Shape a) {
			super.paint(g, a);
			lexerValid = false;
		}

		/**
		protected int drawUnselectedText(Graphics g, int x, int y, int p0, int p1) throws BadLocationException {
			Document doc = getDocument();
			Color last = null;
			int mark = p0;
			for (; p0 < p1;) {
				updateScanner(p0);
				int p = Math.min(lexer.getEndOffset(), p1);
				p = (p <= p0) ? p1 : p;
				Color fg = getForeground(lexer.token);
				if (fg != last && last != null) {
					// color change, flush what we have
					Segment text = getLineBuffer();
					doc.getText(mark, p0 - mark, text);
					x = Utilities.drawTabbedText(text, x, y, g, this, mark);
					mark = p0;
				}
				last = fg;
				p0 = p;
			}
			Segment text = getLineBuffer();
			doc.getText(mark, p1 - mark, text);
			x = Utilities.drawTabbedText(text, x, y, g, this, mark);
			return x;
		}

		/**
		void updateScanner(int p) {
			try {
				if (!lexerValid) {
					JavaDocument doc = (JavaDocument) getDocument();
					lexer.setRange(doc.getScannerStart(p), doc.getLength());
					lexerValid = true;
				}
				while (lexer.getEndOffset() <= p) {
					lexer.scan();
				}
			} catch (Throwable e) {
				// can't adjust scanner... calling logic
				// what the hell, I will let it not printed yet
				System.err.print("*");
			}
		}

		JavaDocument.Scanner lexer;
		boolean lexerValid;
	}

}