package fr.ledcom.tj3.taskjugglerplugin.language.references;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJIdRef;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.TJSemantics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TJRefactoringSupportProvider extends RefactoringSupportProvider {

    @Override
    public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, @Nullable PsiElement context) {
        return element instanceof TJIdRef ref && TJSemantics.isDeclaration(ref);
    }
}
