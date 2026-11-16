package fr.ledcom.tj3.taskjugglerplugin.language.completion;

import com.intellij.codeInsight.template.impl.TemplateManagerImpl;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.List;

public class TJCompletionTest extends BasePlatformTestCase {

    private List<String> complete(String text) {
        myFixture.configureByText("a.tjp", text);
        myFixture.completeBasic();
        List<String> strings = myFixture.getLookupElementStrings();
        assertNotNull("single variant was inserted", strings);
        return strings;
    }

    public void testTopLevelKeywords() {
        List<String> items = complete("<caret>");
        assertContainsElements(items, "project", "task", "resource", "macro", "taskreport");
        assertDoesntContain(items, "depends", "effort");
    }

    public void testTaskAttributes() {
        List<String> items = complete("task t \"T\" {\n  <caret>\n}");
        assertContainsElements(items, "depends", "effort", "allocate", "task", "milestone", "note");
        assertDoesntContain(items, "project", "formats", "resource");
    }

    public void testReportAttributes() {
        List<String> items = complete("taskreport r \"\" {\n  <caret>\n}");
        assertContainsElements(items, "formats", "columns", "hidetask", "timeformat");
        assertDoesntContain(items, "depends");
    }

    public void testKeywordPrefixInsertsSpace() {
        myFixture.configureByText("a.tjp", "task t \"T\" {\n  depe<caret>\n}");
        var items = myFixture.completeBasic();
        if (items != null) {
            myFixture.getLookup().setCurrentItem(java.util.Arrays.stream(items)
                    .filter(i -> i.getLookupString().equals("depends")).findFirst().orElseThrow());
            myFixture.finishLookup(com.intellij.codeInsight.lookup.Lookup.NORMAL_SELECT_CHAR);
        }
        assertEquals("task t \"T\" {\n  depends \n}", myFixture.getEditor().getDocument().getText());
        assertEquals("task t \"T\" {\n  depends ".length(), myFixture.getCaretOffset());
    }

    public void testValues() {
        List<String> items = complete("task t \"T\" {\n  scheduling <caret>\n}");
        assertSameElements(items, "alap", "asap");
    }

    public void testColumnValues() {
        List<String> items = complete("taskreport r \"\" {\n  columns name, <caret>\n}");
        assertContainsElements(items, "start", "end", "effort", "chart");
    }

    public void testTaskReferences() {
        List<String> items = complete("""
                task a "A" {
                  task b "B"
                  task c "C"
                  task d "D" { depends !<caret> }
                }
                """);
        assertSameElements(items, "b", "c");
    }

    public void testQualifiedTaskReferences() {
        List<String> items = complete("""
                task a "A" {
                  task b "B" { task x "X" }
                }
                task t "T" { depends a.<caret> }
                """);
        assertSameElements(items, "b");
    }

    public void testResourceReferences() {
        List<String> items = complete("""
                resource team "Team" {
                  resource alice "Alice"
                  resource bob "Bob"
                }
                task t "T" { allocate <caret> }
                """);
        assertContainsElements(items, "team", "alice", "bob");
    }

    public void testMacroReferences() {
        List<String> items = complete("macro mine [ x ]\ntask t \"T\" { start ${<caret>} }");
        assertContainsElements(items, "mine", "projectstart", "now");
    }

    public void testCustomAttributes() {
        List<String> items = complete("""
                project p "P" 2024-01-01 +1m {
                  extend task { text Phone "Phone" }
                }
                task t "T" {
                  <caret>
                }
                """);
        assertContainsElements(items, "Phone", "effort");
    }

    public void testLiveTemplate() {
        TemplateManagerImpl.setTemplateTesting(getTestRootDisposable());
        myFixture.configureByText("a.tjp", "task<caret>");
        myFixture.type("\t");
        String text = myFixture.getEditor().getDocument().getText();
        assertTrue(text, text.startsWith("task  \"\" {"));
    }
}
