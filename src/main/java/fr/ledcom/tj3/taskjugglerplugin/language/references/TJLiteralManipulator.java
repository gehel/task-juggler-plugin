package fr.ledcom.tj3.taskjugglerplugin.language.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJLiteral;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Content of quoted strings, used by file references (include "file.tji") and language injection. */
public class TJLiteralManipulator extends AbstractElementManipulator<TJLiteral> {

    @Override
    public @Nullable TJLiteral handleContentChange(@NotNull TJLiteral element, @NotNull TextRange range,
                                                   @NotNull String newContent) throws IncorrectOperationException {
        return TJElementManipulators.replaceRange(element, range, newContent);
    }

    @Override
    public @NotNull TextRange getRangeInElement(@NotNull TJLiteral element) {
        int length = element.getTextLength();
        if (element.getNode().findChildByType(TJTypes.STRING) != null && length >= 2) {
            return new TextRange(1, length - 1);
        }
        return new TextRange(0, length);
    }
}
