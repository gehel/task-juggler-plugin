package fr.ledcom.tj3.taskjugglerplugin.language.semantics;

import org.jetbrains.annotations.NotNull;

/** Kinds of named things that can be declared in a TaskJuggler file and referenced elsewhere. */
public enum PropertyKind {
    PROJECT("project", true),
    TASK("task", true),
    RESOURCE("resource", true),
    ACCOUNT("account", true),
    SHIFT("shift", true),
    SCENARIO("scenario", true),
    REPORT("report", true),
    NAVIGATOR("navigator", false),
    MACRO("macro", false),
    FLAG("flag", false),
    ATTRIBUTE("attribute", false);

    private final String displayName;
    /** Properties form a hierarchy whose ids are qualified by their parents' ids (a.b.c). */
    private final boolean hierarchical;

    PropertyKind(String displayName, boolean hierarchical) {
        this.displayName = displayName;
        this.hierarchical = hierarchical;
    }

    public @NotNull String getDisplayName() {
        return displayName;
    }

    public boolean isHierarchical() {
        return hierarchical;
    }
}
