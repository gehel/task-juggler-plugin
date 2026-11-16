package fr.ledcom.tj3.taskjugglerplugin.tj3;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TJ3Test extends BasePlatformTestCase {

    public void testParseOutput() {
        List<TJ3.Message> messages = TJ3.parse("""
                ./sub/inc.tji:3: Error: Unexpected token 'depnds' found.
                  depnds
                /abs/main.tjp:12: Warning: Something odd
                Reading file main.tjp ...
                """, Path.of("/project"));
        assertEquals(2, messages.size());
        assertEquals(Path.of("/project/sub/inc.tji"), messages.get(0).file());
        assertEquals(3, messages.get(0).line());
        assertTrue(messages.get(0).error());
        assertEquals("Unexpected token 'depnds' found.", messages.get(0).text());
        assertEquals(Path.of("/abs/main.tjp"), messages.get(1).file());
        assertFalse(messages.get(1).error());
    }

    /** Runs the real tj3 when it is installed. */
    public void testCheckSyntaxWithInstalledTj3() throws Exception {
        File executable = TJ3.executable();
        if (executable == null) return;
        Path dir = Files.createTempDirectory("tj3test");
        Files.createDirectories(dir.resolve("sub"));
        Path main = Files.writeString(dir.resolve("main.tjp"),
                "project p \"P\" 2024-01-01 +1m\ninclude \"sub/inc.tji\"\n");
        Files.writeString(dir.resolve("sub/inc.tji"), "task b \"B\" {\n  depnds a\n}\n");
        List<TJ3.Message> messages = TJ3.checkSyntax(executable, main);
        assertEquals(1, messages.size());
        assertEquals(dir.resolve("sub/inc.tji").toRealPath(), messages.get(0).file().toRealPath());
        assertEquals(2, messages.get(0).line());
    }
}
