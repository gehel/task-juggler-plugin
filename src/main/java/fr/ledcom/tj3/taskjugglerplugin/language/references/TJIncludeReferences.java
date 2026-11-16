package fr.ledcom.tj3.taskjugglerplugin.language.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReferenceSet;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJLiteral;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatement;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;
import org.jetbrains.annotations.NotNull;

/** File references in {@code include "path/file.tji"}, relative to the including file. */
public final class TJIncludeReferences {

    private TJIncludeReferences() {
    }

    public static PsiReference @NotNull [] of(@NotNull TJLiteral literal) {
        if (!(literal.getParent() instanceof TJStatement statement)
            || !"include".equals(statement.getKeywordName())
            || literal.getNode().findChildByType(TJTypes.STRING) == null) {
            return PsiReference.EMPTY_ARRAY;
        }
        String path = literal.getStringValue();
        if (path == null || path.contains("$(") || path.contains("${")) return PsiReference.EMPTY_ARRAY;
        TextRange range = new TJLiteralManipulator().getRangeInElement(literal);
        return new FileReferenceSet(path, literal, range.getStartOffset(), null, true).getAllReferences();
    }
}
