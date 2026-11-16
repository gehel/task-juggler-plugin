package fr.ledcom.tj3.taskjugglerplugin.metadata;

import junit.framework.TestCase;

import java.util.List;

public class KeywordRegistryTest extends TestCase {

    private final KeywordRegistry registry = KeywordRegistry.getInstance();

    public void testLoadsKeywords() {
        assertTrue(registry.all().size() > 200);
        assertTrue(registry.isKeyword("task"));
        assertTrue(registry.isKeyword("depends"));
        assertFalse(registry.isKeyword("asap"));
        assertTrue(registry.isValueLiteral("asap"));
    }

    public void testTopLevel() {
        List<String> names = registry.allowedIn(null).stream().map(KeywordInfo::name).toList();
        assertTrue(names.contains("project"));
        assertTrue(names.contains("task"));
        assertTrue(names.contains("resource"));
        assertFalse(names.contains("depends"));
    }

    public void testResolveVariantByContext() {
        KeywordInfo account = registry.resolve("account", "task");
        assertNotNull(account);
        assertEquals("account.task", account.id());
        assertEquals("account", registry.resolve("account", null).id());
        assertNull(registry.resolve("depends", null));
        assertEquals("depends", registry.resolve("depends", "task").id());
    }

    public void testValues() {
        assertEquals(List.of("alap", "asap"), registry.byId("scheduling").values());
        assertTrue(registry.byId("columnid").values().contains("effort"));
    }

    public void testDocumentation() {
        KeywordInfo task = registry.byId("task");
        assertNotNull(task.purposeHtml());
        assertEquals("task [<id>] <name> [{ <attributes> }]", task.syntax());
        assertEquals("https://taskjuggler.org/tj3/manual/task.html", task.manualUrl());
    }
}
