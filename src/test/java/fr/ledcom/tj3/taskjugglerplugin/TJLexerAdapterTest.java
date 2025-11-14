package fr.ledcom.tj3.taskjugglerplugin;

import com.intellij.psi.tree.IElementType;
import fr.ledcom.tj3.taskjugglerplugin.language.TJLexerAdapter;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TJLexerAdapterTest {
    @Test
    void test() {
        TJLexerAdapter lexer = new TJLexerAdapter();
        lexer.start("""
                task my_task_id "Some Task" {\
                  depends other.task\
                }""");

        while (true) {
            IElementType tokenType = lexer.getTokenType();
            String tokenText = lexer.getTokenText();
            lexer.advance();
        }

    }
}
