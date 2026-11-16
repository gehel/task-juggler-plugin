package fr.ledcom.tj3.taskjugglerplugin.language.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiReference;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;
import fr.ledcom.tj3.taskjugglerplugin.language.references.TJNameReference;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.PropertyKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** A statement head that is not a keyword is a user defined attribute declared with {@code extend}. */
public abstract class TJStatementHeadMixin extends ASTWrapperPsiElement {

    protected TJStatementHeadMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public @Nullable PsiReference getReference() {
        ASTNode id = getNode().findChildByType(TJTypes.ID);
        if (id == null) return null;
        return new TJNameReference<>(this, TextRange.from(id.getStartOffset() - getNode().getStartOffset(),
                id.getTextLength()), PropertyKind.ATTRIBUTE);
    }
}
