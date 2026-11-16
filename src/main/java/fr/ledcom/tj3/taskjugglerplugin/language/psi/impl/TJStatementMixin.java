package fr.ledcom.tj3.taskjugglerplugin.language.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.psi.util.PsiTreeUtil;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatement;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatementBase;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatementHead;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;
import fr.ledcom.tj3.taskjugglerplugin.metadata.KeywordInfo;
import fr.ledcom.tj3.taskjugglerplugin.metadata.KeywordRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TJStatementMixin extends ASTWrapperPsiElement implements TJStatementBase {

    protected TJStatementMixin(@NotNull ASTNode node) {
        super(node);
    }

    private TJStatementHead head() {
        return PsiTreeUtil.getChildOfType(this, TJStatementHead.class);
    }

    @Override
    public @NotNull PsiElement getKeywordElement() {
        TJStatementHead head = head();
        ASTNode token = head == null ? null : head.getNode().findChildByType(
                com.intellij.psi.tree.TokenSet.create(TJTypes.KEYWORD, TJTypes.ID));
        return token == null ? this : token.getPsi();
    }

    @Override
    public @NotNull String getKeywordName() {
        PsiElement keyword = getKeywordElement();
        return keyword == this ? "" : keyword.getText();
    }

    @Override
    public @Nullable TJStatement getParentStatement() {
        return PsiTreeUtil.getParentOfType(this, TJStatement.class, true);
    }

    @Override
    public @Nullable KeywordInfo getKeywordInfo() {
        return CachedValuesManager.getCachedValue(this, () -> CachedValueProvider.Result.create(
                computeKeywordInfo(), PsiModificationTracker.MODIFICATION_COUNT));
    }

    private @Nullable KeywordInfo computeKeywordInfo() {
        TJStatement parent = getParentStatement();
        String context = parent == null ? null : parent.getBlockContextId();
        if (parent != null && context == null) return null;
        return KeywordRegistry.getInstance().resolve(getKeywordName(), context);
    }

    @Override
    public @Nullable String getBlockContextId() {
        KeywordInfo info = getKeywordInfo();
        return info == null ? null : info.id();
    }

    @Override
    public String toString() {
        return "TJStatement(" + getKeywordName() + ")";
    }
}
