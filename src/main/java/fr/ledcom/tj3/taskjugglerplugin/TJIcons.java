package fr.ledcom.tj3.taskjugglerplugin;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.PropertyKind;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

public final class TJIcons {

    public static final Icon FILE = IconLoader.getIcon("/icons/taskjuggler_icon.svg", TJIcons.class);

    private TJIcons() {
    }

    public static @NotNull Icon forKind(@NotNull PropertyKind kind) {
        return switch (kind) {
            case PROJECT -> AllIcons.Nodes.Project;
            case TASK -> AllIcons.Nodes.Target;
            case RESOURCE -> AllIcons.General.User;
            case ACCOUNT -> AllIcons.Nodes.DataColumn;
            case SHIFT -> AllIcons.General.History;
            case SCENARIO -> AllIcons.Nodes.Related;
            case REPORT -> AllIcons.Nodes.DataTables;
            case NAVIGATOR -> AllIcons.Nodes.Folder;
            case MACRO -> AllIcons.Nodes.Function;
            case FLAG -> AllIcons.Nodes.Bookmark;
            case ATTRIBUTE -> AllIcons.Nodes.Property;
        };
    }
}
