package fr.ledcom.tj3.taskjugglerplugin.language.inspections;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJIdRef;
import fr.ledcom.tj3.taskjugglerplugin.language.references.TJDeclaration;
import fr.ledcom.tj3.taskjugglerplugin.language.references.TJDeclarations;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.PropertyKind;
import org.jetbrains.annotations.NotNull;

/** The same task, resource, account, shift, scenario, report or macro id declared twice in the project. */
public class TJDuplicateDeclarationInspection extends TJInspectionBase {

    @Override
    protected void check(@NotNull PsiElement element, @NotNull ProblemsHolder holder) {
        if (!(element instanceof TJIdRef ref)) return;
        TJDeclaration self = null;
        for (TJDeclaration d : TJDeclarations.inFile(ref.getContainingFile())) {
            if (d.element() == ref) {
                self = d;
                break;
            }
        }
        if (self == null || self.kind() == PropertyKind.FLAG || self.kind() == PropertyKind.ATTRIBUTE
            || self.kind() == PropertyKind.PROJECT) {
            return;
        }
        for (TJDeclaration other : TJDeclarations.ofKind(ref, self.kind())) {
            if (other.element() != ref && other.fullId().equals(self.fullId())) {
                String where = other.element().getContainingFile() == ref.getContainingFile()
                        ? "" : " (also in " + other.element().getContainingFile().getName() + ")";
                holder.registerProblem(ref, self.kind().getDisplayName() + " '" + self.fullId()
                                            + "' is declared more than once" + where,
                        ProblemHighlightType.GENERIC_ERROR);
                return;
            }
        }
    }
}
