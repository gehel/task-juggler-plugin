package fr.ledcom.tj3.taskjugglerplugin.language.references;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.usageView.UsageInfo;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJIdRef;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatement;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.TJSemantics;

import java.util.Collection;

public class TJReferencesTest extends BasePlatformTestCase {

    private static final String PROJECT = """
            project p "P" 2024-01-01 +1m {
              scenario plan "Plan" {
                scenario delayed "Delayed"
              }
              extend task { text Phone "Phone" }
            }
            flags important
            macro devs [ allocate dev1 ]
            resource team "Team" {
              resource dev1 "Alice"
            }
            account cost "Cost" {
              account dev "Development"
            }
            task acso "AcSo" {
              task spec "Spec" {
                depends !software
              }
              task software "Software" {
                task db "DB" { }
                task gui "GUI" {
                  depends !db, !!spec, acso.software.db
                  allocate dev1
                  chargeset dev
                  flags important
                  delayed:effort 2d
                  Phone "x1"
                  ${devs}
                }
              }
            }
            supplement task acso.software {
              task tests "Tests" { depends !db }
            }
            """;

    /** Resolves the reference at the {@code <caret>} marker and returns the declared identifier. */
    private TJIdRef resolveAt(String marked) {
        myFixture.configureByText("main.tjp", marked);
        PsiReference ref = myFixture.getFile().findReferenceAt(myFixture.getCaretOffset());
        assertNotNull("no reference at caret", ref);
        PsiElement target = ref.resolve();
        assertNotNull("unresolved: " + ref.getCanonicalText(), target);
        assertTrue(target instanceof TJIdRef);
        return (TJIdRef) target;
    }

    private void assertResolvesTo(String marker, String declaredId, String keyword) {
        assertTrue("marker not found: " + marker, PROJECT.contains(marker.replace("|", "")));
        TJIdRef target = resolveAt(PROJECT.replace(marker.replace("|", ""), marker.replace("|", "<caret>")));
        assertTrue(TJSemantics.isDeclaration(target));
        assertEquals(keyword, ((TJStatement) target.getParent()).getKeywordName());
        assertEquals(declaredId, target.getText());
    }

    public void testRelativeSibling() {
        assertResolvesTo("depends !|software", "software", "task");
    }

    public void testRelativeTwoLevels() {
        assertResolvesTo("!!|spec", "spec", "task");
    }

    public void testRelativeOneLevel() {
        assertResolvesTo("depends !|db, ", "db", "task");
    }

    public void testAbsoluteSegments() {
        assertResolvesTo("acso.software.|db", "db", "task");
        assertResolvesTo("acso.|software.db", "software", "task");
        assertResolvesTo("|acso.software.db", "acso", "task");
    }

    public void testInsideSupplement() {
        assertResolvesTo("depends !|db }", "db", "task");
    }

    public void testFlatNamespaces() {
        assertResolvesTo("allocate |dev1", "dev1", "resource");
        assertResolvesTo("chargeset |dev", "dev", "account");
        assertResolvesTo("flags |important\n      d", "important", "flags");
    }

    public void testScenarioMacroAndAttribute() {
        assertResolvesTo("|delayed:effort", "delayed", "scenario");
        assertResolvesTo("${|devs}", "devs", "macro");
        assertResolvesTo("|Phone \"x1\"", "Phone", "text");
    }

    public void testUnresolvedRelative() {
        myFixture.configureByText("main.tjp", "task a \"A\" { depends !<caret>missing }");
        PsiReference ref = myFixture.getFile().findReferenceAt(myFixture.getCaretOffset());
        assertNotNull(ref);
        assertNull(ref.resolve());
    }

    public void testAcrossIncludedFiles() {
        myFixture.addFileToProject("resources.tji", "resource bob \"Bob\"\n");
        myFixture.configureByText("main.tjp", "include \"resources.tji\"\ntask t \"T\" { allocate b<caret>ob }");
        PsiElement target = myFixture.getFile().findReferenceAt(myFixture.getCaretOffset()).resolve();
        assertNotNull(target);
        assertEquals("resources.tji", target.getContainingFile().getName());
    }

    public void testIncludeFileReference() {
        PsiFile included = myFixture.addFileToProject("sub/reports.tji", "taskreport r \"\"\n");
        myFixture.configureByText("main.tjp", "include \"sub/rep<caret>orts.tji\"\n");
        PsiReference ref = myFixture.getFile().findReferenceAt(myFixture.getCaretOffset());
        assertNotNull(ref);
        assertEquals(included, ref.resolve());
    }

    public void testFindUsages() {
        myFixture.configureByText("main.tjp", PROJECT.replace("task software \"", "task soft<caret>ware \""));
        Collection<UsageInfo> usages = myFixture.findUsages(myFixture.getElementAtCaret());
        // !software, acso.software.db, supplement task acso.software
        assertEquals(3, usages.size());
    }

    public void testRenameTask() {
        myFixture.configureByText("main.tjp", PROJECT.replace("task software \"", "task soft<caret>ware \""));
        myFixture.renameElementAtCaret("code");
        String text = myFixture.getEditor().getDocument().getText();
        assertTrue(text.contains("task code \"Software\""));
        assertTrue(text.contains("depends !code"));
        assertTrue(text.contains("acso.code.db"));
        assertTrue(text.contains("supplement task acso.code"));
    }

    public void testRenameResource() {
        myFixture.configureByText("main.tjp", "resource de<caret>v1 \"A\"\ntask t \"T\" { allocate dev1 }\n");
        myFixture.renameElementAtCaret("alice");
        myFixture.checkResult("resource alice \"A\"\ntask t \"T\" { allocate alice }\n");
    }

    public void testGotoSymbol() {
        myFixture.configureByText("main.tjp", PROJECT);
        Object[] items = myFixture.getGotoSymbolResults("db", false, null).toArray();
        assertEquals(1, items.length);
    }
}
