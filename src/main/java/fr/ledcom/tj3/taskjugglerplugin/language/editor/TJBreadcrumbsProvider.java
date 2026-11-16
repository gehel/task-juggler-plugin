package fr.ledcom.tj3.taskjugglerplugin.language.editor;

import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider;
import fr.ledcom.tj3.taskjugglerplugin.TJLanguage;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJIdRef;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatement;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.TJSemantics;
import org.jetbrains.annotations.NotNull;

public class TJBreadcrumbsProvider implements BreadcrumbsProvider {

    private static final Language[] LANGUAGES = {TJLanguage.INSTANCE};

    @Override
    public Language[] getLanguages() {
        return LANGUAGES;
    }

    @Override
    public boolean acceptElement(@NotNull PsiElement element) {
        return element instanceof TJStatement statement && statement.getBody() != null;
    }

    @Override
    public @NotNull String getElementInfo(@NotNull PsiElement element) {
        TJStatement statement = (TJStatement) element;
        TJIdRef id = TJSemantics.declaredId(statement);
        if (id != null) return statement.getKeywordName() + " " + id.getText();
        String name = TJSemantics.declaredName(statement);
        return name != null && !name.isEmpty() ? statement.getKeywordName() + " \"" + name + "\""
                : statement.getKeywordName();
    }
}
