package fr.ledcom.tj3.taskjugglerplugin;

import com.intellij.psi.tree.IElementType;
import fr.ledcom.tj3.taskjugglerplugin.language.TJLexerAdapter;
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

        // this is what the lexer currently generates, but probably isn't what we want
        assertThat(lexer.getTokenType().toString()).isEqualTo("TASK");
        assertThat(lexer.getTokenText().toString()).isEqualTo("task");
        lexer.advance();
        assertThat(lexer.getTokenType().toString()).isEqualTo("WHITE_SPACE");
        lexer.advance();
        assertThat(lexer.getTokenType().toString()).isEqualTo("TaskJugglerTokenType.ID");
    }
}
