package fr.ledcom.tj3.taskjugglerplugin.language.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiReference;
import fr.ledcom.tj3.taskjugglerplugin.language.references.TJNameReference;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.PropertyKind;
import org.jetbrains.annotations.NotNull;

/** {@code delayed:} in {@code delayed:start 2024-01-01}: references the scenario. */
public abstract class TJScenarioPrefixMixin extends ASTWrapperPsiElement {

    protected TJScenarioPrefixMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference getReference() {
        return new TJNameReference<>(this, new TextRange(0, getTextLength() - 1), PropertyKind.SCENARIO);
    }
}
