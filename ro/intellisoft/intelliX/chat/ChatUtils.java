
package ro.intellisoft.intelliX.chat;

import java.awt.*;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class ChatUtils {

	private static final String[] smileysArray = {":-)", ":)", ":(", ":P", ":O", ":))", ":D", ";)", ":p", ":o"};
	private static final String[] smileysImgArray = {"smile", "smile", "sad", "tongue", "ooo", "laught", "teeth", "hehe", "tongue", "ooo"};

	public static URL getURL(String filename) {
		URL url = null;
		try {
			url = new URL("file", "", System.getProperty("user.dir") + "\\" + filename);
		} catch (java.net.MalformedURLException e) {
			System.out.println("Couldn't create image: badly specified image file" + e);
			return null;
		}
		return url;
	}

	public static URL getURL(String filename, String codebase) {
		URL url = null;
		try {
			url = new URL(codebase + filename);
		} catch (Exception e) {
			System.out.println("Couldn't create image: badly specified URL" + e);
			return null;
		}
		return url;
	}//end  getURL

	public static Color decodeColor(String color2decode) {
		if (color2decode.equals("black"))
			return Color.black;
		else if (color2decode.equals("blue"))
			return Color.blue;
		else if (color2decode.equals("green"))
			return Color.green;
		else if (color2decode.equals("red"))
			return Color.red;
		else if (color2decode.equals("yellow"))
			return Color.yellow;
		else if (color2decode.equals("cyan"))
			return Color.cyan;
		else if (color2decode.equals("white"))
			return Color.white;
		else if (color2decode.equals("silver"))
			return Color.lightGray;
		else if (color2decode.equals("gray"))
			return Color.darkGray;
		else if (color2decode.equals("navy"))
			return new Color(0, 0, 128);
		else if (color2decode.equals("teal"))
			return new Color(0, 128, 128);
		else if (color2decode.equals("lime"))
			return new Color(0, 255, 0);
		else if (color2decode.equals("aqua"))
			return new Color(0, 255, 255);
		else if (color2decode.equals("maroon"))
			return new Color(128, 0, 0);
		else if (color2decode.equals("purple"))
			return new Color(128, 0, 128);
		else if (color2decode.equals("olive"))
			return new Color(128, 128, 0);
		else if (color2decode.equals("fuchista"))
			return new Color(255, 0, 255);
		else if (color2decode.startsWith("#")) {
			return new Color(Integer.parseInt(color2decode.substring(1), 16));
		} else
			return Color.black;
	}//end decodeColor

	//verifica daca avem in textForCheck un emoticon si inlocuieste in string
	// cu linkul spre imaginea corespunzatoare
	public static String putEmotIcons(String textForCheck, String codeBase, String username) {
		StringTokenizer analizer = new StringTokenizer(textForCheck);
		String textForReturn = new String();                // aici se construieste String-ul de return
		String temporar = new String();                     // token-ul curent
		while (analizer.hasMoreTokens()) {
			temporar = analizer.nextToken();
			//analizez smileys-urile si le inlocuiesc cu imaginile resp
			for (int i = 0; i < smileysArray.length; i++)
				if (temporar.equals(smileysArray[i])) {
					temporar = "<img src = " + codeBase + "/images/e_" + smileysImgArray[i] + ".gif>";
					break;
				}
			if (temporar.equals("/me"))
				temporar = username;                        // /me
			textForReturn = textForReturn + " " + temporar;
		}
		return textForReturn;
	}//end putEmotIcons

	/**
	 * Verifica daca un nume de user/group este permis.
	 * @return null daca este valid sau un mesaj de eroare in caz contrar
	 * @author Qurtach
	 */
	public static String isValidName(String aName) {
		if (aName == null){
			return "null name";
		}
		//nu permit decat litere, cifre si underscore si nici guest:
		if (aName.equals("guest"))
			return "Guest is not a valid name!";
		if (aName.length() > 12) {
			return "The name must be at most 12 characters long!";
		}
		for (int i = 0; i < aName.length(); i++) {
			char c = aName.charAt(i);
			if (c == '_')
				continue;
			if (c > 'z' || c < '0')
				return "You can't use " + c + " character!";
			if (c > '9' && c < 'A')
				return "You can't use " + c + " character!";
			if (c > 'Z' && c < 'a')
				return "You can't use " + c + " character!";
		}
		return null;
	}
}