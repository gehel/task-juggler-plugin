package fr.ledcom.tj3.taskjugglerplugin.language.documentation;

import com.intellij.codeInsight.TargetElementUtil;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class TJDocumentationTest extends BasePlatformTestCase {

    private String docAtCaret(String text) {
        myFixture.configureByText("a.tjp", text);
        int offset = myFixture.getCaretOffset();
        PsiElement original = myFixture.getFile().findElementAt(offset);
        var provider = new TJDocumentationProvider();
        // Same lookup order as the platform: the provider's custom element first, then the referenced declaration.
        PsiElement target = provider.getCustomDocumentationElement(myFixture.getEditor(), myFixture.getFile(), original, offset);
        if (target == null) {
            target = TargetElementUtil.findTargetElement(myFixture.getEditor(), TargetElementUtil.getInstance().getAllAccepted());
        }
        assertNotNull("no documentation target", target);
        String doc = provider.generateDoc(target, original);
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
