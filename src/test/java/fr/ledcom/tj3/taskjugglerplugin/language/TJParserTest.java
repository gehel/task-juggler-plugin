package fr.ledcom.tj3.taskjugglerplugin.language;

import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.ParsingTestCase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class TJParserTest extends ParsingTestCase {

    public TJParserTest() {
        super("parser", "tjp", new TJParserDefinition());
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    public void testConstructs() {
        doTest(true, true);
    }

    public void testErrors() {
        doTest(true, false);
    }

    /** Parses the examples shipped with a local TaskJuggler installation, when available. */
    public void testTaskJugglerExamples() throws IOException {
        Path examples = Path.of("/usr/share/doc/tj3/examples");
        if (!Files.isDirectory(examples)) return;
        List<Path> files;
        try (Stream<Path> walk = Files.walk(examples)) {
            files = walk.filter(p -> p.toString().matches(".*\\.tj[pi]")).toList();
        }
        for (Path file : files) {
            PsiFile psi = createPsiFile(file.getFileName().toString(), Files.readString(file));
            Collection<PsiErrorElement> errors = PsiTreeUtil.findChildrenOfType(psi, PsiErrorElement.class);
            assertEmpty(file + " " + errors.stream()
                    .map(e -> e.getErrorDescription() + " @" + e.getTextOffset() + ": "
                              + psi.getText().substring(e.getTextOffset(), Math.min(psi.getTextLength(), e.getTextOffset() + 40)))
                    .toList(), errors);
        }
    }
}
