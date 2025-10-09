package fr.ledcom.tj3.taskjugglerplugin;

import com.intellij.lang.Language;

public class TJLanguage extends Language {

    public static final TJLanguage INSTANCE = new TJLanguage();

    protected TJLanguage() {
        super("TaskJuggler");
    }
}
