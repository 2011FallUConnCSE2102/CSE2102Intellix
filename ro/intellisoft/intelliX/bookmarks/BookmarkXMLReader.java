/**
 * User: Administrator
 * @Author: qurtach@intellisoft.ro
 * @Date: May 20, 2002
 * @Time: 12:27:50 PM
 */

package ro.intellisoft.intelliX.bookmarks;

import ro.intellisoft.XML.XMLAttributes;
import ro.intellisoft.intelliX.UI.GeneralEditorPane;
import ro.intellisoft.whiteboard.SVGHandler;

public class BookmarkXMLReader extends ro.intellisoft.XML.XMLHandler {
	private GeneralEditorPane parent;
	private BookmarkWindow crtBookmark = null;
	private TextBookmark tb = null;
	private SVGHandler svgHandler = null;

	public BookmarkXMLReader(GeneralEditorPane parent) {
		this.parent = parent;
	}

	public void startElement(String name, XMLAttributes attributes) {
		if (svgHandler != null){
			svgHandler.startElement(name, attributes);
			return;
		}
		//return to the bookamrk
		if (name.equals("bookmarks")) {
			//starts parsing the XML tree
		} else if (name.equals("bookmark")) {
			crtBookmark = parent.getBookmarkForLine(Integer.parseInt(attributes.getValue("line")));
			if (crtBookmark == null){
				boolean RW = parent.isWriteLockAquired();
				crtBookmark = new BookmarkWindow(parent, parent.getIDE(), attributes.getValue("description"), Integer.parseInt(attributes.getValue("line")), RW);
				parent.addBookmark(crtBookmark);
			} else {
				crtBookmark.clear();
			}
		} else if (name.equals("link")) {
			if (attributes.getValue("type").equals("image/jpg")) {
                WhiteboardBookmark wb = new WhiteboardBookmark( null, attributes.getValue("src"));
				crtBookmark.addComponent(wb, false);
			} else if (attributes.getValue("type").equals("image/svg")) {
				WhiteboardBookmark wb = null;
				if (attributes.getValue("src").equals("inline")){
					wb = new WhiteboardBookmark(null, true);
					svgHandler = new SVGHandler(wb.getWhiteboard(), ".", "");
				} else {
					wb = new WhiteboardBookmark(null, false, attributes.getValue("src"));
				}
				crtBookmark.addComponent(wb, false);
			} else if (attributes.getValue("type").equals("audio")) {
				AudioBookmark ab = new AudioBookmark(null);
				crtBookmark.addComponent(ab, false);
			} else if (attributes.getValue("type").equals("annotation")) {
				tb = new TextBookmark( null, parent.getIDE());
				crtBookmark.addComponent(tb, false);
				//then add chat lines!
			}
		} else if (name.equals("line")) {
			if (tb != null){
				tb.addLine(attributes.getValue("remark"));
			} //else throw new NullPointerException();
		}
	}

	public void endElement(String name) {
		if (name.equals("bookmark")) {
			crtBookmark = null;
		} else if (name.equals("link")) {
			//probably an annotation ends here
			tb = null;
		} else if (name.equals("bookmarks")) {
			//ends parsing the XML tree here
		} else if (name.equals("svg")) {
			if (svgHandler!= null){
				svgHandler = null;
			}
		} else if (svgHandler!= null){
			svgHandler.endElement(name);
			//treate it to the whiteboard!
		}
	}

	public void characters(String data) {
		if (svgHandler != null){
			svgHandler.characters(data);
		}
	}

}