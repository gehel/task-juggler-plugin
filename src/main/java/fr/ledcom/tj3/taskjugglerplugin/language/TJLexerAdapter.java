package fr.ledcom.tj3.taskjugglerplugin.language;

import com.intellij.lexer.FlexAdapter;

public class TJLexerAdapter extends FlexAdapter {

    public TJLexerAdapter() {
        super(new TaskJugglerLexer(null));
    }
}
