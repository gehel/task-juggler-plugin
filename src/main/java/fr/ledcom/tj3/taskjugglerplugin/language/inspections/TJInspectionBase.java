package fr.ledcom.tj3.taskjugglerplugin.language.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import fr.ledcom.tj3.taskjugglerplugin.TJFile;
import org.jetbrains.annotations.NotNull;

/** Visits every PSI element of TaskJuggler files. */
public abstract class TJInspectionBase extends LocalInspectionTool {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        if (!(holder.getFile() instanceof TJFile)) return PsiElementVisitor.EMPTY_VISITOR;
        return new PsiElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                check(element, holder);
            }
        };
    }

    protected abstract void check(@NotNull PsiElement element, @NotNull ProblemsHolder holder);
}
