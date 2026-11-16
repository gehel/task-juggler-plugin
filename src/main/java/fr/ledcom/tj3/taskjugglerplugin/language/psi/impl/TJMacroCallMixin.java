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

/** {@code ${name "arg"}}: references the macro declaration. */
public abstract class TJMacroCallMixin extends ASTWrapperPsiElement {

    protected TJMacroCallMixin(@NotNull ASTNode node) {
        super(node);
    }

    public @Nullable ASTNode getMacroNameNode() {
        return getNode().findChildByType(TJTypes.ID);
    }

    @Override
    public @Nullable PsiReference getReference() {
        ASTNode name = getMacroNameNode();
        if (name == null) return null;
        return new TJNameReference<>(this, TextRange.from(name.getStartOffset() - getNode().getStartOffset(),
                name.getTextLength()), PropertyKind.MACRO);
    }
}
