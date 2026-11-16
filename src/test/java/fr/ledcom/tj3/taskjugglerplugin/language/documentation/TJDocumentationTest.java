package fr.ledcom.tj3.taskjugglerplugin.language.documentation;

import com.intellij.codeInsight.documentation.DocumentationManager;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class TJDocumentationTest extends BasePlatformTestCase {

    private String docAtCaret(String text) {
        myFixture.configureByText("a.tjp", text);
        PsiElement original = myFixture.getFile().findElementAt(myFixture.getCaretOffset());
        PsiElement target = DocumentationManager.getInstance(getProject())
                .findTargetElement(myFixture.getEditor(), myFixture.getFile());
        assertNotNull("no documentation target", target);
        String doc = new TJDocumentationProvider().generateDoc(target, original);
        assertNotNull(doc);
        return doc;
    }

    public void testKeywordDoc() {
        String doc = docAtCaret("task t \"T\" {\n  dep<caret>ends a\n}");
        assertTrue(doc, doc.contains("depends"));
        assertTrue(doc, doc.contains("cannot start before the specified tasks"));
        assertTrue(doc, doc.contains("taskjuggler.org/tj3/manual/depends.html"));
    }

    public void testContextSpecificVariant() {
        String doc = docAtCaret("task t \"T\" {\n  acc<caret>ount a\n}");
        assertTrue(doc, doc.contains("account (task)"));
    }

    public void testDeclarationDoc() {
        String doc = docAtCaret("""
                task a "Top" {
                  task b "Build" {
                    note "Compile everything"
                    effort 2d
                  }
                }
                task c "C" { depends a.<caret>b }
                """);
        assertTrue(doc, doc.contains("a.b"));
        assertTrue(doc, doc.contains("Build"));
        assertTrue(doc, doc.contains("Compile everything"));
        assertTrue(doc, doc.contains("effort 2d"));
    }
}
