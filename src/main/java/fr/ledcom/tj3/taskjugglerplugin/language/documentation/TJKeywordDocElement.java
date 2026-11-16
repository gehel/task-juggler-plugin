package fr.ledcom.tj3.taskjugglerplugin.language.documentation;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.FakePsiElement;
import fr.ledcom.tj3.taskjugglerplugin.metadata.KeywordInfo;
import org.jetbrains.annotations.NotNull;

/** Stand-in PSI element carrying a keyword's metadata, so the documentation popup can show it. */
public class TJKeywordDocElement extends FakePsiElement {

    private final PsiElement context;
    private final KeywordInfo keyword;

    public TJKeywordDocElement(@NotNull PsiElement context, @NotNull KeywordInfo keyword) {
        this.context = context;
        this.keyword = keyword;
    }

    public @NotNull KeywordInfo getKeyword() {
        return keyword;
    }

    @Override
    public PsiElement getParent() {
        return context;
    }

    @Override
    public String getName() {
        return keyword.name();
    }

    @Override
    public String getPresentableText() {
        return keyword.title();
    }
}
