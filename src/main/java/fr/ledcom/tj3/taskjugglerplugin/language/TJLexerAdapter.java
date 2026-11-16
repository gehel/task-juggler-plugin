package fr.ledcom.tj3.taskjugglerplugin.language;

import com.intellij.lexer.FlexAdapter;
import fr.ledcom.tj3.taskjugglerplugin.language.lexer.TaskJugglerLexer;

public class TJLexerAdapter extends FlexAdapter {

    public TJLexerAdapter() {
        super(new TaskJugglerLexer(null));
    }
}
