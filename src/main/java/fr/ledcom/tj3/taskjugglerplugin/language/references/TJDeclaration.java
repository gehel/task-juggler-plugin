package fr.ledcom.tj3.taskjugglerplugin.language.references;

import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJIdRef;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.PropertyKind;
import org.jetbrains.annotations.NotNull;

/**
 * A declared property.
 *
 * @param kind    what is declared
 * @param fullId  id as used in references: qualified ({@code parent.child}) for tasks and reports, plain otherwise
 * @param element the identifier in the declaring statement
 */
public record TJDeclaration(@NotNull PropertyKind kind, @NotNull String fullId, @NotNull TJIdRef element) {

    public @NotNull String simpleId() {
        int dot = fullId.lastIndexOf('.');
        return dot < 0 ? fullId : fullId.substring(dot + 1);
    }
}
