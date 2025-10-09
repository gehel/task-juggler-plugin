package fr.ledcom.tj3.taskjugglerplugin.language;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TJLexerAdapterTest {

    @Test
    void test() {
        TJLexerAdapter lexerAdapter = new TJLexerAdapter();
        lexerAdapter.start("task task_id \"a task\" {\n" +
                "  depends id\n" +
                "  note \"my note\"\n" +
                "} ");
        
    }

}