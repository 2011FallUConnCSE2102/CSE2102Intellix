/*

package jeditor;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.undo.UndoableEdit;
import java.io.Serializable;
import java.util.Vector;

/**
public final class GapContent implements AbstractDocument.Content, Serializable {


	/**
	public GapContent() {
		this(10);
	}

	/**
	public GapContent(int initialLength) {
		array = new char[initialLength];
		array[0] = '\n';
		g0 = 1;
		g1 = initialLength;
	}
	public int length() {
		int len = array.length - (g1 - g0);
		return len;
	}

	/**
	public UndoableEdit insertString(int where, String str) throws BadLocationException {
		if (where >= length()) {
			throw new BadLocationException("Invalid insert", length());
		}
		char[] chars = str.toCharArray();
		replace(where, 0, chars);
		return null;
	}

	/**
	public UndoableEdit remove(int where, int nitems) throws BadLocationException {
		if (where + nitems >= length()) {
			throw new BadLocationException("Invalid insert", length() + 1);
		}
		replace(where, nitems, empty);
		return null;

	}

	/**
	public String getString(int where, int len) throws BadLocationException {
		Segment s = new Segment();
		getChars(where, len, s);
		return new String(s.array, s.offset, s.count);
	}

	/**
	public void getChars(int where, int len, Segment chars) throws BadLocationException {
		if (where < 0) {
			throw new BadLocationException("Invalid location", -1);
		}
		if ((where + len) > length()) {
			throw new BadLocationException("Invalid location", length() + 1);
		}
		if ((where + len) <= g0) {
			// below gap
			chars.offset = where;
		} else if (where >= g0) {
			// above gap
			chars.offset = g1 + where - g0;
		} else {
			// spans the gap, must copy
			chars.offset = 0;
			int before = g0 - where;
			System.arraycopy(array, where, chars.array, 0, before);
			System.arraycopy(array, g1, chars.array, before, len - before);
		}
		chars.count = len;
	}

	/**
	public Position createPosition(int offset) throws BadLocationException {
		if (marks == null) {
			marks = new Vector();
			search = new MarkData(0);
		}
		if (unusedMarks > Math.max(5, (marks.size() / 10))) {
			removeUnusedMarks();
		}
		int index = (offset < g0) ? offset : offset + (g1 - g0);
		MarkData m = new MarkData(index);
		int sortIndex = findSortIndex(m);
		marks.insertElementAt(m, sortIndex);
		return new StickyPosition(m);
	}

	/**
	final class MarkData {

		MarkData(int index) {
			this.index = index;
		}

		/**
		public final int getOffset() {
			int offs = (index < g0) ? index : index - (g1 - g0);
			return Math.max(offs, 0);
		}

		public final void dispose() {
			unused = true;
			unusedMarks += 1;
		}

		int index;
		boolean unused;
	}

	/**
	final class StickyPosition implements Position {

		StickyPosition(MarkData mark) {
			this.mark = mark;
		}

		public final int getOffset() {
			return mark.getOffset();
		}

		protected void finalize() throws Throwable {
			// schedule the record to be removed later
		}

		public String toString() {
			return Integer.toString(getOffset());
		}

		MarkData mark;
	}
	private transient Vector marks;

	/**
	private transient MarkData search;

	/**
	private transient int unusedMarks;

	/**
	char[] array;

	/**
	int g0;

	/**
	int g1;
	void replace(int position, int rmSize, char[] addItems) {
		int addSize = addItems.length;
		int addOffset = 0;
		if (addSize == 0) {
			close(position, rmSize);
			return;
		} else if (rmSize > addSize) {
			/* Shrink the end. */
		} else {
			/* Grow the end, do two chunks. */
			int end = open(position + rmSize, endSize);
			System.arraycopy(addItems, rmSize, array, end, endSize);
			addSize = rmSize;
		}
		System.arraycopy(addItems, addOffset, array, position, addSize);
	}

	/**
	void close(int position, int nItems) {
		if (nItems == 0) return;

		int end = position + nItems;
		int new_gs = (g1 - g0) + nItems;
		if (end <= g0) {
			// Move gap to end of block.
				shiftGap(end);
			}
		} else if (position >= g0) {
			// Move gap to beginning of block.
				shiftGap(position);
			}
		} else {
			// The gap is properly inside the target block.
			shiftGapEndUp(g0 + new_gs);
		}
	}

	/**
	int open(int position, int nItems) {
		int gapSize = g1 - g0;
		if (nItems == 0) {
			if (position > g0)
				position += gapSize;
			return position;
		}
		if (nItems >= gapSize) {
			// Pre-shift the gap, to reduce total movement.
			gapSize = g1 - g0;
		}

		g0 = g0 + nItems;
		return position;
	}

	/**
	void resize(int nsize) {
		char[] narray = new char[nsize];
		System.arraycopy(array, 0, narray, 0, Math.min(nsize, array.length));
		array = narray;
	}

	/**
	void shiftEnd(int newSize) {
		int oldSize = array.length;
		int oldGapEnd = g1;
		int upperSize = oldSize - oldGapEnd;
		int newGapEnd;
		long dg;

		if (newSize < oldSize) {
			if (oldSize <= array.length) {
				// No more downsizing.
			}
			if (upperSize > 0) {
				/* When contracting, move vector contents to front. */
				oldGapEnd = oldSize;
				upperSize = 0;
			}
		}

		resize(newSize);
		newGapEnd = array.length - upperSize;
		g1 = newGapEnd;
		dg = newGapEnd - oldGapEnd;
		int n = marks.size();
		for (int i = adjustIndex; i < n; i++) {
			MarkData mark = (MarkData) marks.elementAt(i);
			mark.index += dg;
		}

		if (upperSize != 0) {
			// Copy array items to new end of array.
		}
	}

	/**
	void shiftGap(int newGapStart) {
		if (newGapStart == g0) {
			return;
		}
		int oldGapStart = g0;
		int dg = newGapStart - oldGapStart;
		int oldGapEnd = g1;
		int newGapEnd = oldGapEnd + dg;
		int gapSize = oldGapEnd - oldGapStart;

		g0 = newGapStart;
		g1 = newGapEnd;
		if (dg > 0) {
			// Move gap up, move data and marks down.
			int n = marks.size();
			for (int i = adjustIndex; i < n; i++) {
				MarkData mark = (MarkData) marks.elementAt(i);
				if (mark.index >= newGapEnd) {
					break;
				}
				mark.index -= gapSize;
			}
			System.arraycopy(array, oldGapEnd, array, oldGapStart, dg);
		} else if (dg < 0) {
			// Move gap down, move data and marks up.
			int n = marks.size();
			for (int i = adjustIndex; i < n; i++) {
				MarkData mark = (MarkData) marks.elementAt(i);
				if (mark.index >= oldGapEnd) {
					break;
				}
				mark.index += gapSize;
			}
			System.arraycopy(array, newGapStart, array, newGapEnd, -dg);
		}
	}

	/**
	void shiftGapStartDown(int newGapStart) {
		// Push aside all marks from oldGapStart down to newGapStart.
		int n = marks.size();
		for (int i = adjustIndex; i < n; i++) {
			MarkData mark = (MarkData) marks.elementAt(i);
			if (mark.index > g0) {
				// no more marks to adjust
			}
			mark.index = g1;
		}
		g0 = newGapStart;
	}

	/**
	void shiftGapEndUp(int newGapEnd) {
		int adjustIndex = findMarkAdjustIndex(g1);
		int n = marks.size();
		for (int i = adjustIndex; i < n; i++) {
			MarkData mark = (MarkData) marks.elementAt(i);
			if (mark.index >= newGapEnd) {
				break;
			}
			mark.index = newGapEnd;
		}
		g1 = newGapEnd;
	}

	/**
	final int compare(MarkData o1, MarkData o2) {
		if (o1.index < o2.index) {
			return -1;
		} else if (o1.index > o2.index) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	final int findMarkAdjustIndex(int searchIndex) {
		search.index = Math.max(searchIndex, 1);
		int index = findSortIndex(search);
			MarkData d = (MarkData) marks.elementAt(i);
			if (d.index != search.index) {
				break;
			}
			index -= 1;
		}
		return index;
	}

	/**
	final int findSortIndex(MarkData o) {
		int lower = 0;
		int upper = marks.size() - 1;
		int mid = 0;

		if (upper == -1) {
			return 0;
		}

		int cmp = 0;
		MarkData last = (MarkData) marks.elementAt(upper);
		cmp = compare(o, last);
		if (cmp > 0)
			return upper + 1;

		while (lower <= upper) {
			mid = lower + ((upper - lower) / 2);
			MarkData entry = (MarkData) marks.elementAt(mid);
			cmp = compare(o, entry);

			if (cmp == 0) {
				// found a match
			} else if (cmp < 0) {
				upper = mid - 1;
			} else {
				lower = mid + 1;
			}
		}
	}

	/**
	final void removeUnusedMarks() {
		int n = marks.size();
		Vector cleaned = new Vector(n);
		for (int i = 0; i < n; i++) {
			MarkData mark = (MarkData) marks.elementAt(i);
			if (mark.unused == false) {
				cleaned.addElement(mark);
			}
		}
		marks = cleaned;
		unusedMarks = 0;
	}

}