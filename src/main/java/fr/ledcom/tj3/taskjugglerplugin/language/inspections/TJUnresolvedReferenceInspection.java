package fr.ledcom.tj3.taskjugglerplugin.language.inspections;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJIdRef;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJLiteral;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJMacroCall;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJScenarioPrefix;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatement;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.TJSemantics;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;
import fr.ledcom.tj3.taskjugglerplugin.language.references.TJNameReference;
import fr.ledcom.tj3.taskjugglerplugin.language.references.TJPropertyReference;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.PropertyKind;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * References to tasks, resources, accounts, shifts, scenarios, flags and macros that are not declared in any
 * TaskJuggler file of the project. User defined attributes are reported by {@link TJInvalidKeywordInspection}.
 */
public class TJUnresolvedReferenceInspection extends TJInspectionBase {

    @Override
    protected void check(@NotNull PsiElement element, @NotNull ProblemsHolder holder) {
        if (!(element instanceof TJIdRef || element instanceof TJMacroCall || element instanceof TJScenarioPrefix)) {
            return;
        }
        TJStatement owner = TJSemantics.ownerStatement(element);
        if (owner != null && owner.getKeywordInfo() == null && owner.getParentStatement() != null) {
            return; // the keyword itself is invalid here and reported by TJInvalidKeywordInspection
        }
        if (owner != null && owner.getKeywordInfo() == null && owner.getParentStatement() == null
            && !(element instanceof TJMacroCall)) {
            return;
        }
        for (PsiReference reference : element.getReferences()) {
            PropertyKind kind;
            if (reference instanceof TJPropertyReference p) kind = p.getKind();
            else if (reference instanceof TJNameReference<?> n && !n.isBuiltin()) kind = n.getKind();
            else continue;
            if (kind == PropertyKind.ATTRIBUTE) continue;
            if (((PsiPolyVariantReference) reference).multiResolve(false).length > 0) continue;
            String name = reference.getCanonicalText();
            if (definedInMacro(element, name)) continue;
            holder.registerProblem(reference, "Unknown " + kind.getDisplayName() + " '" + name + "'",
                    ProblemHighlightType.LIKE_UNKNOWN_SYMBOL);
            return; // one problem per identifier: later segments cannot resolve either
        }
    }

    /**
     * Declarations inside macro bodies are not parsed; don't report names that appear in one, nor references that
     * are themselves built from macro parameters.
     */
    private static boolean definedInMacro(PsiElement element, String name) {
        Pattern word = Pattern.compile("(?<![\\w.])" + Pattern.quote(name) + "(?!\\w)");
        for (TJLiteral literal : PsiTreeUtil.findChildrenOfType(element.getContainingFile(), TJLiteral.class)) {
            if (literal.getNode().findChildByType(TJTypes.MACRO_BODY) != null && word.matcher(literal.getText()).find()) {
                return true;
            }
        }
        return false;
    }
}
