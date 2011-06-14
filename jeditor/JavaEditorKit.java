/*

package jeditor;


import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.ViewFactory;

/**
public class JavaEditorKit extends DefaultEditorKit {

	public JavaEditorKit() {
		super();
	}

	public JavaContext getStylePreferences() {
		if (preferences == null) {
			preferences = new JavaContext();
		}
		return preferences;
	}

	public void setStylePreferences(JavaContext prefs) {
		preferences = prefs;
	}
	public String getContentType() {
		return "text/java";
	}

	/**
	public Object clone() {
		JavaEditorKit kit = new JavaEditorKit();
		kit.preferences = preferences;
		return kit;
	}

	/**
	public Document createDefaultDocument() {
		return new JavaDocument();
	}

	/**
	public final ViewFactory getViewFactory() {
		return getStylePreferences();
	}

	JavaContext preferences;
}