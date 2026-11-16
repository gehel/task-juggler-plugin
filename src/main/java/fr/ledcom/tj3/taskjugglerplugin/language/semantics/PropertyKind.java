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
    /** Project properties (as opposed to macros, flags, attributes). */
    private final boolean hierarchical;

    PropertyKind(String displayName, boolean hierarchical) {
        this.displayName = displayName;
        this.hierarchical = hierarchical;
    }

    public @NotNull String getDisplayName() {
        return displayName;
    }

    /** Project properties (as opposed to macros, flags, attributes): their keyword is highlighted specially. */
    public boolean isHierarchical() {
        return hierarchical;
    }

    /** Ids are qualified by the ids of the enclosing properties ({@code parent.child}); others are global. */
    public boolean hasQualifiedIds() {
        return this == TASK || this == REPORT;
    }
}
