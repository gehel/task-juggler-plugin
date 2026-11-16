package fr.ledcom.tj3.taskjugglerplugin.language.inspections;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class TJInspectionsTest extends BasePlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        myFixture.enableInspections(new TJInvalidKeywordInspection(), new TJUnresolvedReferenceInspection(),
                new TJDuplicateDeclarationInspection(), new TJDeprecatedKeywordInspection());
    }

    public void testInvalidKeywords() {
        myFixture.configureByText("a.tjp", """
                project p "P" 2024-01-01 +1m {
                  scenario plan "Plan"
                  <error descr="'depends' is not allowed inside 'project'">depends</error> x
                }
                task t "T" {
                  <error descr="Unknown keyword or attribute 'efort'">efort</error> 2d
                  <error descr="'note' is not scenario specific">plan:</error>note "x"
                  plan:effort 2d
                }
                <error descr="'effort' is not allowed at the top level">effort</error> 2d
                """);
        myFixture.checkHighlighting(true, false, false);
    }

    public void testTypoQuickFix() {
        myFixture.configureByText("a.tjp", "task t \"T\" {\n  dependz<caret> x\n}\ntask x \"X\"\n");
        IntentionAction fix = myFixture.findSingleIntention("Change to 'depends'");
        myFixture.launchAction(fix);
        myFixture.checkResult("task t \"T\" {\n  depends x\n}\ntask x \"X\"\n");
    }

    public void testUnresolvedReferences() {
        myFixture.configureByText("a.tjp", """
                resource dev "Dev"
                macro m [ x ]
                task t "T" {
                  task a "A"
                  task b "B" {
                    depends !a, !<error descr="Unknown task 'missing'">missing</error>
                    allocate dev, <error descr="Unknown resource 'nobody'">nobody</error>
                    start ${projectstart}
                    effort ${m}
                    effort ${<error descr="Unknown macro 'nomacro'">nomacro</error>}
                  }
                }
                """);
        myFixture.checkHighlighting(true, false, false);
    }

    public void testScopedToIncludeTree() {
        myFixture.addFileToProject("one/main.tjp", "project p \"P\" 2024-01-01 +1m\ninclude \"res.tji\"\n");
        myFixture.addFileToProject("one/res.tji", "resource dev \"Dev\"\n");
        myFixture.addFileToProject("two/res.tji", "resource dev \"Dev\"\n");
        myFixture.configureByText("two.tjp", "project q \"Q\" 2024-01-01 +1m\ninclude \"two/res.tji\"\n"
                                             + "task t \"T\" { allocate dev }\n");
        myFixture.checkHighlighting(true, false, false);
        myFixture.configureFromTempProjectFile("one/res.tji");
        myFixture.checkHighlighting(true, false, false);
    }

    public void testDuplicates() {
        myFixture.configureByText("a.tjp", """
                resource <error descr="resource 'dev' is declared more than once">dev</error> "Dev"
                resource <error descr="resource 'dev' is declared more than once">dev</error> "Dev 2"
                task t "T" { task a "A" }
                task u "U" { task a "A" }
                """);
        myFixture.checkHighlighting(true, false, false);
    }

    public void testStrayBrace() {
        myFixture.configureByText("a.tjp", "task t \"T\" { }\n<error descr=\"Unmatched '}'\">}</error>\n");
        myFixture.checkHighlighting(true, false, false);
    }

    /** The examples shipped with TaskJuggler are valid: no problem may be reported. */
    public void testNoFalsePositivesOnTaskJugglerExamples() throws IOException {
        Path examples = Path.of("/usr/share/doc/tj3/examples");
        if (!Files.isDirectory(examples)) return;
        List<Path> dirs;
        try (Stream<Path> list = Files.list(examples)) {
            dirs = list.filter(Files::isDirectory).toList();
        }
        StringBuilder problems = new StringBuilder();
        for (Path dir : dirs) {
            List<Path> files;
            try (Stream<Path> list = Files.list(dir)) {
                files = list.filter(p -> p.toString().matches(".*\\.tj[pi]")).sorted().toList();
            }
            for (Path file : files) {
                myFixture.addFileToProject(dir.getFileName() + "/" + file.getFileName(), Files.readString(file));
            }
            for (Path file : files) {
                myFixture.configureFromTempProjectFile(dir.getFileName() + "/" + file.getFileName());
                for (HighlightInfo info : myFixture.doHighlighting()) {
                    if (info.getSeverity().compareTo(HighlightSeverity.WEAK_WARNING) < 0) continue;
                    String text = myFixture.getEditor().getDocument().getText();
                    int line = myFixture.getEditor().getDocument().getLineNumber(info.getStartOffset()) + 1;
                    problems.append(file.getFileName()).append(':').append(line).append(' ')
                            .append(info.getDescription()).append(" [")
                            .append(text, info.getStartOffset(), info.getEndOffset()).append("]\n");
                }
            }
        }
        assertEquals("", problems.toString());
    }
}
