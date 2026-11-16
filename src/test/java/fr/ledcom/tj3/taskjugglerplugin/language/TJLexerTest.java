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

    @Override
    protected String getPathToTestDataFile(String extension) {
        return getDirPath() + "/" + getTestName(true) + extension;
    }

    private void doFileTest() {
        doFileTest("tjp");
    }

    public void testKeywordsAndIds() {
        doFileTest();
    }

    public void testNumbersAndDates() {
        doFileTest();
    }

    public void testStringsAndComments() {
        doFileTest();
    }

    public void testMacros() {
        doFileTest();
    }

    public void testRestartableAfterEachToken() {
        checkCorrectRestart("task a \"A\" {\n  summary -8<-\n  text\n  ->8-\n  depends !b.task\n}\n");
    }
}
