package fr.ledcom.tj3.taskjugglerplugin.language.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJLiteral;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJLiteralBase;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;
import fr.ledcom.tj3.taskjugglerplugin.language.references.TJIncludeReferences;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TJLiteralMixin extends ASTWrapperPsiElement implements TJLiteralBase {

    protected TJLiteralMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference @NotNull [] getReferences() {
        return TJIncludeReferences.of((TJLiteral) this);
    }

    public boolean isString() {
        ASTNode first = getNode().getFirstChildNode();
        return first != null && (first.getElementType() == TJTypes.STRING
                                 || first.getElementType() == TJTypes.MULTILINE_STRING);
    }

    /** Content of a quoted string without quotes and escapes, or {@code null} if not a quoted string. */
    public @Nullable String getStringValue() {
        ASTNode first = getNode().getFirstChildNode();
        if (first == null) return null;
        String text = first.getText();
        if (first.getElementType() == TJTypes.STRING && text.length() >= 2) {
            char quote = text.charAt(0);
            String inner = text.substring(1, text.endsWith(String.valueOf(quote)) ? text.length() - 1 : text.length());
            return inner.replace("\\" + quote, String.valueOf(quote));
        }
        if (first.getElementType() == TJTypes.MULTILINE_STRING) {
            String inner = text.startsWith("-8<-") ? text.substring(4) : text;
            if (inner.endsWith("->8-")) inner = inner.substring(0, inner.length() - 4);
            return inner.strip();
        }
        return null;
    }
}
