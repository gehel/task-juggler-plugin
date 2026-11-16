package fr.ledcom.tj3.taskjugglerplugin.language.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

/** Hand-written API of {@link TJLiteral}. */
public interface TJLiteralBase extends PsiElement {

    /** Whether this is a quoted or multi-line string. */
    boolean isString();

    /** Content of a quoted string without quotes and escapes, or {@code null} if not a string. */
    @Nullable String getStringValue();
}
