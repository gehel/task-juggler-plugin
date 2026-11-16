package fr.ledcom.tj3.taskjugglerplugin.language.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import fr.ledcom.tj3.taskjugglerplugin.TJLanguage;
import org.jetbrains.annotations.NotNull;

/** Text replacement inside TaskJuggler PSI elements by reparsing a dummy file. */
public final class TJElementManipulators {

    private TJElementManipulators() {
    }

    /**
     * Replaces {@code range} (relative to the element) by {@code text} and returns the new element of the same class.
     */
    @SuppressWarnings("unchecked")
    public static <T extends PsiElement> T replaceRange(@NotNull T element, @NotNull TextRange range,
                                                        @NotNull String text) throws IncorrectOperationException {
        String oldText = element.getText();
        String newText = range.replace(oldText, text);
        T replacement = (T) createElement(element, newText);
        if (replacement == null) throw new IncorrectOperationException("Cannot create element from '" + newText + "'");
        return (T) element.replace(replacement);
    }

    /** Parses {@code text} in a context similar to {@code template} and returns the element of the same class. */
    private static PsiElement createElement(PsiElement template, String text) {
        String prefix = switch (template.getClass().getSimpleName()) {
            case "TJIdRefImpl" -> "depends ";
            case "TJLiteralImpl" -> "note ";
            case "TJMacroCallImpl" -> "start ";
            case "TJScenarioPrefixImpl", "TJStatementHeadImpl" -> "";
            default -> null;
        };
        if (prefix == null) return null;
        String suffix = template.getClass().getSimpleName().equals("TJScenarioPrefixImpl") ? "start 2024-01-01" : "";
        var file = PsiFileFactory.getInstance(template.getProject())
                .createFileFromText("dummy.tjp", TJLanguage.INSTANCE, prefix + text + suffix);
        PsiElement leaf = file.findElementAt(prefix.length());
        Class<? extends PsiElement> type = template.getClass();
        PsiElement candidate = PsiTreeUtil.getParentOfType(leaf, type, false);
        return candidate != null && candidate.getText().equals(text) ? candidate : null;
    }
}
