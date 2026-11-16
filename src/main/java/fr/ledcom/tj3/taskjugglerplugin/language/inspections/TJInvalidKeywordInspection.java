package fr.ledcom.tj3.taskjugglerplugin.language.inspections;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.openapi.util.TextRange;
import fr.ledcom.tj3.taskjugglerplugin.language.references.TJElementManipulators;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJScenarioPrefix;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatement;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatementHead;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;
import fr.ledcom.tj3.taskjugglerplugin.metadata.KeywordInfo;
import fr.ledcom.tj3.taskjugglerplugin.metadata.KeywordRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Keywords used where TaskJuggler does not accept them, unknown keywords (that are not user defined attributes),
 * scenario prefixes on attributes that are not scenario specific.
 */
public class TJInvalidKeywordInspection extends TJInspectionBase {

    @Override
    protected void check(@NotNull PsiElement element, @NotNull ProblemsHolder holder) {
        if (!(element instanceof TJStatement statement)) return;
        PsiElement keyword = statement.getKeywordElement();
        if (keyword == statement) return;
        TJStatement parent = statement.getParentStatement();
        String context = parent == null ? null : parent.getBlockContextId();
        if (parent != null && context == null) return; // the parent is already reported or unknown
        String name = statement.getKeywordName();
        String where = parent == null ? "at the top level" : "inside '" + parent.getKeywordName() + "'";

        if (keyword.getNode().getElementType() == TJTypes.ID) {
            TJStatementHead head = (TJStatementHead) keyword.getParent();
            if (head.getReference() != null && head.getReference().resolve() != null) return; // custom attribute
            holder.registerProblem(keyword, "Unknown keyword or attribute '" + name + "'",
                    ProblemHighlightType.LIKE_UNKNOWN_SYMBOL, suggestions(name, context));
            return;
        }

        KeywordInfo info = statement.getKeywordInfo();
        if (info == null) {
            holder.registerProblem(keyword, "'" + name + "' is not allowed " + where,
                    ProblemHighlightType.GENERIC_ERROR, suggestions(name, context));
            return;
        }
        TJScenarioPrefix prefix = PsiTreeUtilCompat.scenarioPrefix(statement);
        if (prefix != null && !info.scenarioSpecific()) {
            holder.registerProblem(prefix, "'" + name + "' is not scenario specific", ProblemHighlightType.GENERIC_ERROR);
        }
    }

    private static LocalQuickFix[] suggestions(String name, String context) {
        List<KeywordInfo> allowed = KeywordRegistry.getInstance().allowedIn(context);
        Set<String> names = new LinkedHashSet<>();
        allowed.stream()
                .filter(i -> !i.isDeprecated())
                .map(KeywordInfo::name)
                .filter(n -> distance(n, name) <= Math.max(1, name.length() / 3))
                .sorted(Comparator.comparingInt(n -> distance(n, name)))
                .limit(3)
                .forEach(names::add);
        List<LocalQuickFix> fixes = new ArrayList<>();
        for (String n : names) fixes.add(new ReplaceKeywordFix(n));
        return fixes.toArray(LocalQuickFix.EMPTY_ARRAY);
    }

    private static int distance(String a, String b) {
        int[] prev = new int[b.length() + 1];
        int[] cur = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) prev[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            cur[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                cur[j] = Math.min(Math.min(cur[j - 1] + 1, prev[j] + 1), prev[j - 1] + cost);
            }
            int[] t = prev;
            prev = cur;
            cur = t;
        }
        return prev[b.length()];
    }

    /** Replaces the statement keyword. */
    static class ReplaceKeywordFix implements LocalQuickFix {
        private final String keyword;

        ReplaceKeywordFix(String keyword) {
            this.keyword = keyword;
        }

        @Override
        public @NotNull String getName() {
            return "Change to '" + keyword + "'";
        }

        @Override
        public @NotNull String getFamilyName() {
            return "Change keyword";
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement element = descriptor.getPsiElement();
            if (!(element.getParent() instanceof TJStatementHead head)) return;
            TextRange range = element.getTextRange().shiftLeft(head.getTextRange().getStartOffset());
            TJElementManipulators.replaceRange(head, range, keyword);
        }
    }

    /** Small helper to avoid a dependency on generated accessor names. */
    static final class PsiTreeUtilCompat {
        static TJScenarioPrefix scenarioPrefix(TJStatement statement) {
            TJStatementHead head = statement.getStatementHead();
            return head == null ? null : head.getScenarioPrefix();
        }
    }
}
