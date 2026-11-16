package fr.ledcom.tj3.taskjugglerplugin.language.inspections;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStrayBrace;
import org.jetbrains.annotations.NotNull;

/** Syntax errors not reported by the tolerant parser. */
public class TJSyntaxAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof TJStrayBrace) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Unmatched '}'").create();
        }
    }
}
