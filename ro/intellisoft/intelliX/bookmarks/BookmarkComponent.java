/**
 * @Author: qurtach@intellisoft.ro
 * @Date: Jun 6, 2002
 * @Time: 1:07:44 PM
 */

package ro.intellisoft.intelliX.bookmarks;

import ro.intellisoft.intelliX.HermixLink;

public interface BookmarkComponent {

	public abstract void setEditable(boolean flag);

	/**tells if this component accept focus traversing when pressing PGUP/PGDN/UP/DN/LEFT/RIGHT*/
	public abstract boolean acceptFocus();

	/**@returns a string that represint this component*/
	public abstract String getName();

	/**convert this component to a XML format in order to be saved*/
	public abstract String getXMLRepresentation(int flag);
}
