package fr.ledcom.tj3.taskjugglerplugin.language.editor;

import com.intellij.codeInsight.generation.actions.CommentByLineCommentAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class TJEditorTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    public void testFormatter() {
        myFixture.configureByFile("formatter/Before.tjp");
        WriteCommandAction.writeCommandAction(getProject()).run(() ->
                CodeStyleManager.getInstance(getProject()).reformat(myFixture.getFile()));
        assertSameLinesWithFile(getTestDataPath() + "/formatter/After.tjp", myFixture.getEditor().getDocument().getText());
    }

    public void testEnterIndentsInsideBlock() {
        myFixture.configureByText("a.tjp", "task t \"T\" {<caret>\n}");
        myFixture.type("\n");
        myFixture.checkResult("task t \"T\" {\n  <caret>\n}");
    }

    public void testLineComment() {
        myFixture.configureByText("a.tjp", "task t \"T\" {\n  eff<caret>ort 2d\n}");
        new CommentByLineCommentAction().actionPerformedImpl(getProject(), myFixture.getEditor());
        assertEquals("task t \"T\" {\n  # effort 2d\n}", myFixture.getEditor().getDocument().getText());
    }

    public void testFolding() {
        myFixture.testFolding(getTestDataPath() + "/folding/Folding.tjp");
    }

    public void testStructureView() {
        myFixture.configureByText("a.tjp", """
                project p "P" 2024-01-01 +1m
                resource dev "Dev" {
                  resource dev1 "Alice"
                }
                task t "Top" {
                  effort 1d
                  task sub "Sub"
                }
                macro m [ x ]
                """);
        myFixture.testStructureView(component -> {
            PlatformTestUtil.expandAll(component.getTree());
            PlatformTestUtil.assertTreeEqual(component.getTree(), """
                    -a.tjp
                     p (P)
                     -dev (Dev)
                      dev1 (Alice)
                     -t (Top)
                      sub (Sub)
                     m
                    """);
        });
    }
}
