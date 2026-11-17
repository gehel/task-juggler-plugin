package fr.ledcom.tj3.taskjugglerplugin.language.editor;

import com.intellij.codeInsight.generation.actions.CommentByLineCommentAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.openapi.util.Disposer;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import fr.ledcom.tj3.taskjugglerplugin.language.structure.TJStructureViewFactory;

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
        // The structure view tool window lives in a separate bundled plugin since 2026.x: test the model directly.
        var model = new TJStructureViewFactory().getStructureViewBuilder(myFixture.getFile()) instanceof
                TreeBasedStructureViewBuilder builder ? builder.createStructureViewModel(myFixture.getEditor()) : null;
        assertNotNull(model);
        try {
            StringBuilder tree = new StringBuilder();
            render(model.getRoot(), 0, tree);
            assertEquals("""
                    a.tjp
                     p (P)
                     dev (Dev)
                      dev1 (Alice)
                     t (Top)
                      sub (Sub)
                     m
                    """, tree.toString());
        } finally {
            Disposer.dispose(model);
        }
    }

    private static void render(TreeElement element, int depth, StringBuilder out) {
        out.append(" ".repeat(depth)).append(element.getPresentation().getPresentableText()).append('\n');
        for (TreeElement child : element.getChildren()) render(child, depth + 1, out);
    }
}
