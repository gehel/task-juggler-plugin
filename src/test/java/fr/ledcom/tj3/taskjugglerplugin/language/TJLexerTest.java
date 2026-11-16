package fr.ledcom.tj3.taskjugglerplugin.language;

import com.intellij.lexer.Lexer;
import com.intellij.testFramework.LexerTestCase;

public class TJLexerTest extends LexerTestCase {

    @Override
    protected Lexer createLexer() {
        return new TJLexerAdapter();
    }

    @Override
    protected String getDirPath() {
        return "src/test/testData/lexer";
    }

    public void testSimpleTask() {
        doTest("task my_task \"Some Task\" {\n  depends other.thing\n}",
                """
                        TaskJugglerTokenType.TASK_KEYWORD ('task')
                        WHITE_SPACE (' ')
                        TaskJugglerTokenType.ID ('my_task')
                        WHITE_SPACE (' ')
                        TaskJugglerTokenType.STRING ('"Some Task"')
                        WHITE_SPACE (' ')
                        TaskJugglerTokenType.L_CURLY ('{')
                        WHITE_SPACE ('\\n  ')
                        TaskJugglerTokenType.DEPENDS_KEYWORD ('depends')
                        WHITE_SPACE (' ')
                        TaskJugglerTokenType.ID ('other')
                        TaskJugglerTokenType.DOT ('.')
                        TaskJugglerTokenType.ID ('thing')
                        WHITE_SPACE ('\\n')
                        TaskJugglerTokenType.R_CURLY ('}')""");
    }
}
