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

/** * Class to provide InputStream functionality from a portion of a * Document.  This really should be a Reader, but not enough * things use it yet. */
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

	/**	 * Reads the next byte of data from this input stream. The value	 * byte is returned as an <code>int</code> in the range	 * <code>0</code> to <code>255</code>. If no byte is available	 * because the end of the stream has been reached, the value	 * <code>-1</code> is returned. This method blocks until input data	 * is available, the end of the stream is detected, or an exception	 * is thrown.	 * <p>	 * A subclass must provide an implementation of this method.	 *	 * @return     the next byte of data, or <code>-1</code> if the end of the	 *             stream is reached.	 * @exception  IOException  if an I/O error occurs.	 * @since      JDK1.0	 */
	public int read() throws IOException {
		if (index >= segment.offset + segment.count) {
			if (pos >= p1) {
				// no more data				return -1;
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
