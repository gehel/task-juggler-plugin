package fr.ledcom.tj3.taskjugglerplugin.language.psi;

import com.intellij.psi.PsiElement;
import fr.ledcom.tj3.taskjugglerplugin.metadata.KeywordInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Hand-written API of {@link TJStatement}: a statement is {@code [scenario:]keyword arguments* [{ statements }]}.
 */
public interface TJStatementBase extends PsiElement {

    /** The keyword (or user defined attribute) token starting this statement. */
    @NotNull PsiElement getKeywordElement();

    /** Text of the keyword, e.g. {@code task}. */
    @NotNull String getKeywordName();

    /** The block of attributes of this statement (the trailing {@code { ... }}), if any. */
    @Nullable TJBlock getBody();

    /** Statement whose block contains this statement, or {@code null} at the top level of a file. */
    @Nullable TJStatement getParentStatement();

    /**
     * Metadata of the keyword variant valid at this position (e.g. {@code account.task} for {@code account} inside a
     * task), or {@code null} for unknown keywords or keywords not allowed in the enclosing context.
     */
    @Nullable KeywordInfo getKeywordInfo();

    /**
     * Context id to look up keywords allowed inside this statement's block: the keyword id, or the parent context
     * for statements that are transparent (e.g. macros, includes).
     */
    @Nullable String getBlockContextId();
}
