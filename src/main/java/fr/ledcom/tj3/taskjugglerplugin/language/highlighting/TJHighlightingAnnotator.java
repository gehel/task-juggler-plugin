package fr.ledcom.tj3.taskjugglerplugin.language.highlighting;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJIdRef;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJMacroCall;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatement;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.PropertyKind;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.TJSemantics;
import org.jetbrains.annotations.NotNull;

/**
 * Semantic highlighting: property keywords, user defined attributes, declarations, references, literal values,
 * function calls and macro names.
 */
public class TJHighlightingAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof TJStatement statement) {
            annotateStatement(statement, holder);
        } else if (element instanceof TJIdRef ref) {
            annotateIdRef(ref, holder);
        } else if (element instanceof TJMacroCall call) {
            PsiElement name = call.getNode().findChildByType(TJTypes.ID) == null
                    ? null : call.getNode().findChildByType(TJTypes.ID).getPsi();
            if (name != null) highlight(holder, name.getTextRange(), TJSyntaxHighlighter.MACRO_CALL);
        }
    }

    private static void annotateStatement(TJStatement statement, AnnotationHolder holder) {
        PsiElement keyword = statement.getKeywordElement();
        if (keyword == statement) return;
        if (keyword.getNode().getElementType() == TJTypes.ID) {
            highlight(holder, keyword.getTextRange(), TJSyntaxHighlighter.CUSTOM_ATTRIBUTE);
            return;
        }
        PropertyKind kind = TJSemantics.declarationKind(statement);
        if (kind != null && kind.isHierarchical()) {
            highlight(holder, keyword.getTextRange(), TJSyntaxHighlighter.PROPERTY_KEYWORD);
        }
    }

    private static void annotateIdRef(TJIdRef ref, AnnotationHolder holder) {
        TextAttributesKey key;
        if (TJSemantics.isDeclaration(ref)) {
            key = TJSyntaxHighlighter.DECLARATION;
        } else if (TJSemantics.isFunctionCall(ref)) {
            key = TJSyntaxHighlighter.FUNCTION;
        } else if (TJSemantics.referenceKind(ref) != null) {
            key = TJSyntaxHighlighter.REFERENCE;
        } else if (TJSemantics.isValueWord(ref)) {
            key = TJSyntaxHighlighter.VALUE;
        } else {
            return;
        }
        highlight(holder, identifierRange(ref), key);
    }

    /** Range of the identifier part, without leading '!'. */
    private static TextRange identifierRange(TJIdRef ref) {
        TextRange range = ref.getTextRange();
        return new TextRange(range.getStartOffset() + ref.getBangCount(), range.getEndOffset());
    }

    private static void highlight(AnnotationHolder holder, TextRange range, TextAttributesKey key) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(range).textAttributes(key).create();
    }
}
