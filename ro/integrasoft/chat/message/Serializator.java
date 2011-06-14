
package ro.integrasoft.chat.message;

import java.awt.*;
import java.util.Date;
import java.util.Vector;

public class Serializator {
	private String sep = "|";
	private String result;

	public Serializator() {
	}

	public String Serialize(Object o) {
		String s = "";
		if (o == null)
			return "[null]";
		try {
			Point p = (Point) o;
			s = "[Point" + sep + p.x + sep + p.y + "]";
			//System.out.println(s);
			return s;
		} catch (Exception e) {
		}

		try {
			Vector v = (Vector) o;//done
			s = "[Vector" + sep + (v.size() - 1);
			for (int i = 0; i < v.size(); i++) {
				s = s + sep + Serialize(v.elementAt(i));
			}
			s = s + "]";
			return s;
		} catch (Exception e) {
		}

		try {
			String ss = (String) o;//done
			s = "[String" + sep + (ss.length() - 1) + sep + ss + "]";
			return s;
		} catch (Exception e) {
		}

		try {
			Integer ii = (Integer) o;//done
			s = "[Integer" + sep + ii.intValue() + "]";
			return s;
		} catch (Exception e) {
		}

		try {
			Color c = (Color) o;//done
			s = "[Color" + sep + c.getRed() + sep + c.getGreen() + sep + c.getBlue() + "]";
			return s;
		} catch (Exception e) {
		}

		try {
			String[] ss = (String[]) o;//done
			s = "[TString" + sep + (ss.length - 1);
			for (int i = 0; i < ss.length; i++)
				s = s + sep + Serialize(ss[i]);
			s = s + "]";
			return s;
		} catch (Exception e) {
		}
		try {
			GroupObject go = (GroupObject) o;//done
//				System.out.println("Line Width="+go.getWidth());
			s = "[GroupObject" + sep + go.getX1() + sep + go.getX2() + sep + go.getY1() + sep + go.getY2() + sep + go.getWidth();
			if (go.getColor() != null)
				s = s + sep + Serialize((java.awt.Color) go.getColor());
			else
				s = s + sep + "[null]";

			if (go.getText() != null)
				s = s + sep + Serialize((String) go.getText());
			else
				s = s + sep + "[null]";

			if (go.getGroup() != null)
				s = s + sep + Serialize((String) go.getGroup());
			else
				s = s + sep + "[null]";
			if (go.getObject() != null)
				s = s + sep + Serialize(go.getObject()) + "]";
			else
				s = s + sep + "[null]]";
			//System.out.println("GOS="+s);
			return s;

		} catch (Exception e) {
			//System.out.println( "Object "+o.getClass().getName() + " is not groupObject");
		}

		try {
			GRPMessage gpm = (GRPMessage) o;//done... rectime is lost!!!
			s = "[GRPMessage" + sep + Serialize(gpm.getRecTime()) + sep + Serialize((String) gpm.getUser()) + sep + Serialize(gpm.getData()) + sep + Serialize((String) gpm.getType()) + sep + gpm.getMsgId() + "]";
			return s;
		} catch (Exception e) {
		}

		try {
			ADMMessage gpm = (ADMMessage) o;//done
			s = "[ADMMessage" + sep + Serialize(gpm.getRecTime()) + sep + Serialize((String) gpm.getUser()) + sep + Serialize(gpm.getData()) + sep + Serialize((String) gpm.getType()) + sep + gpm.getMsgId() + "]";
			return s;
		} catch (Exception e) {
		}

		try {
			ERRMessage gpm = (ERRMessage) o;//done
			s = "[ERRMessage" + sep + Serialize(gpm.getRecTime()) + sep + Serialize((String) gpm.getUser()) + sep + Serialize(gpm.getData()) + sep + Serialize((String) gpm.getType()) + sep + gpm.getErrId() + "]";
			return s;
		} catch (Exception e) {
		}

		try {
			SYSMessage gpm = (SYSMessage) o;
			s = "[SYSMessage" + sep + Serialize(gpm.getRecTime()) + sep + Serialize((String) gpm.getUser()) + sep + Serialize(gpm.getData()) + sep + Serialize((String) gpm.getType()) + sep + gpm.getMsgId() + "]";
			return s;
		} catch (Exception e) {
		}
		try {
			Date d = (Date) o;
			return "[Date" + sep + d.getTime() + "]";
		} catch (Exception e) {
		}

		try {
			s = "";
			byte[] b = (byte[]) o;
			for (int i = 0; i < b.length; i++) {
				int is = (int) b[i];
				s = s + Integer.toHexString(i);
			}
			//System.out.println("size="+s.length()+"s="+s);
			return "[TByte" + sep + s.length() + sep + s + "]";
		} catch (Exception e) {
		}
		return "Unknown object... can't serialize Object " + o.getClass().getName();
	}

	public String getData(Object o) {
		result = Serialize(o);
		return result;
	}
}