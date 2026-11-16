package fr.ledcom.tj3.taskjugglerplugin.language.inspections;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatement;
import fr.ledcom.tj3.taskjugglerplugin.metadata.KeywordInfo;
import org.jetbrains.annotations.NotNull;

/** Deprecated and removed keywords. */
public class TJDeprecatedKeywordInspection extends TJInspectionBase {

    @Override
    protected void check(@NotNull PsiElement element, @NotNull ProblemsHolder holder) {
        if (!(element instanceof TJStatement statement)) return;
        KeywordInfo info = statement.getKeywordInfo();
        if (info == null || !info.isDeprecated()) return;
        String instead = info.seeAlso().isEmpty() ? "" : ", use " + String.join(" or ", info.seeAlso()) + " instead";
        if ("removed".equals(info.supportLevel())) {
            holder.registerProblem(statement.getKeywordElement(),
                    "'" + info.name() + "' is no longer supported by TaskJuggler" + instead,
                    ProblemHighlightType.GENERIC_ERROR);
        } else {
            holder.registerProblem(statement.getKeywordElement(), "'" + info.name() + "' is deprecated" + instead,
                    ProblemHighlightType.LIKE_DEPRECATED);
        }
    }
}
