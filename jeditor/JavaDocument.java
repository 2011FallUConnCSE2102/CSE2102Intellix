/*

package jeditor;


import javax.swing.event.DocumentEvent;
import javax.swing.text.*;
import java.io.IOException;

/**
public class JavaDocument extends PlainDocument {

	public JavaDocument() {
		super(new GapContent(1024));
	}

	/**
	public Scanner createScanner() {
		Scanner s;
		try {
			s = new Scanner();
		} catch (IOException e) {
			s = null;
		}
		return s;
	}

	/**
	public int getScannerStart(int p) {
		Element elem = getDefaultRootElement();
		int lineNum = elem.getElementIndex(p);
		Element line = elem.getElement(lineNum);
		AttributeSet a = line.getAttributes();
		while (a.isDefined(CommentAttribute) && lineNum > 0) {
			lineNum -= 1;
			line = elem.getElement(lineNum);
			a = line.getAttributes();
		}
		return line.getStartOffset();
	}
	protected void insertUpdate(DefaultDocumentEvent chng, AttributeSet attr) {
		super.insertUpdate(chng, attr);
		DocumentEvent.ElementChange ec = chng.getChange(root);
		if (ec != null) {
			Element[] added = ec.getChildrenAdded();
			boolean inComment = false;
			for (int i = 0; i < added.length; i++) {
				Element elem = added[i];
				int p0 = elem.getStartOffset();
				int p1 = elem.getEndOffset();
				String s;
				try {
					s = getText(p0, p1 - p0);
				} catch (BadLocationException bl) {
					s = null;
				}
				if (inComment) {
					MutableAttributeSet a = (MutableAttributeSet) elem.getAttributes();
					a.addAttribute(CommentAttribute, CommentAttribute);
					int index = s.indexOf("*/");
					if (index >= 0) {
						// found an end of comment, turn off marks
					}
				} else {
					// scan for multiline comment
					if (index >= 0) {
						// found a start of comment, see if it spans lines
						if (index < 0) {
							// it spans lines
						}
					}
				}
			}
		}
	}

	/**
	protected void removeUpdate(DefaultDocumentEvent chng) {
		super.removeUpdate(chng);
	}
	static final Object CommentAttribute = new AttributeKey();

	static class AttributeKey {

		private AttributeKey() {
		}

		public String toString() {
			return "comment";
		}

	}


	public class Scanner extends sun.tools.java.Scanner {

		Scanner() throws IOException {
			super(new LocalEnvironment(), new DocumentInputStream(JavaDocument.this, 0, getLength()));
			scanComments = true;
		}

		/**
		public void setRange(int p0, int p1) throws IOException {
			useInputStream(new DocumentInputStream(JavaDocument.this, p0, p1));
			this.p0 = p0;
		}

		/**
		public final int getStartOffset() {
			int begOffs = (int) (pos & MAXFILESIZE);
			return p0 + begOffs;
		}

		/**
		public final int getEndOffset() {
			int endOffs = (int) (getEndPos() & MAXFILESIZE);
			return p0 + endOffs;
		}

		int p0;
	}

	static class LocalEnvironment extends sun.tools.java.Environment {

		public void error(Object source, int where, String err, Object arg1, Object arg2, Object arg3) {
			// should do something useful...
			System.err.println("location: " + where);
		}
	}

}