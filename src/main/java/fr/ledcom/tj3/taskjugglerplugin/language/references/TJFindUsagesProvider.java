package fr.ledcom.tj3.taskjugglerplugin.language.references;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import fr.ledcom.tj3.taskjugglerplugin.language.TJLexerAdapter;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJIdRef;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatement;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTokenSets;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.PropertyKind;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.TJSemantics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TJFindUsagesProvider implements FindUsagesProvider {

    @Override
    public @Nullable WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(new TJLexerAdapter(),
                TokenSet.create(TJTypes.ID, TJTypes.KEYWORD, TJTypes.ID_WITH_COLON),
                TJTokenSets.COMMENTS,
                TJTokenSets.STRINGS);
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement element) {
        return element instanceof TJIdRef ref && TJSemantics.isDeclaration(ref);
    }

    @Override
    public @Nullable String getHelpId(@NotNull PsiElement element) {
        return null;
    }

    @Override
    public @NotNull String getType(@NotNull PsiElement element) {
        PropertyKind kind = kindOf(element);
        return kind == null ? "identifier" : kind.getDisplayName();
    }

    @Override
    public @NotNull String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof TJIdRef ref && ref.getParent() instanceof TJStatement statement) {
            String name = TJSemantics.declaredName(statement);
            return name == null || name.isEmpty() ? ref.getText() : ref.getText() + " \"" + name + "\"";
        }
        return element.getText();
    }

    @Override
    public @NotNull String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        return getDescriptiveName(element);
    }

    private static @Nullable PropertyKind kindOf(PsiElement element) {
        return element instanceof TJIdRef ref && ref.getParent() instanceof TJStatement statement
                ? TJSemantics.declarationKind(statement) : null;
    }
}
