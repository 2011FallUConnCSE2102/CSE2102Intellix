/*
 * User: Administrator
 * Date: Mar 20, 2002
 * Time: 4:53:03 PM
 */

package jeditor;

import javax.swing.text.BadLocationException;
import javax.swing.text.Segment;
import java.io.IOException;
import java.io.InputStream;

/**
class DocumentInputStream extends InputStream {
	private JavaDocument document;

	public DocumentInputStream(JavaDocument document, int p0, int p1) {
		this.document = document;
		this.segment = new Segment();
		this.p0 = p0;
		this.p1 = Math.min(document.getLength(), p1);
		pos = p0;
		try {
			loadSegment();
		} catch (IOException ioe) {
			throw new Error("unexpected: " + ioe);
		}
	}

	/**
	public int read() throws IOException {
		if (index >= segment.offset + segment.count) {
			if (pos >= p1) {
				// no more data
			}
			loadSegment();
		}
		return segment.array[index++];
	}

	void loadSegment() throws IOException {
		try {
			int n = Math.min(1024, p1 - pos);
			document.getText(pos, n, segment);
			pos += n;
			index = segment.offset;
		} catch (BadLocationException e) {
			throw new IOException("Bad location");
		}
	}

	Segment segment;
	int p0;    // start position
	int p1;    // end position
	int pos;   // pos in document
	int index; // index into array of the segment
}