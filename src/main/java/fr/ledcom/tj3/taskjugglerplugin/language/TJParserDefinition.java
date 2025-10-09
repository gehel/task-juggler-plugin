package fr.ledcom.tj3.taskjugglerplugin.language;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import fr.ledcom.tj3.taskjugglerplugin.TJFile;
import fr.ledcom.tj3.taskjugglerplugin.TJLanguage;
import fr.ledcom.tj3.taskjugglerplugin.TJParser;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTokenSets;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;
import org.jetbrains.annotations.NotNull;

public class TJParserDefinition implements ParserDefinition {

    public static final IFileElementType FILE = new IFileElementType(TJLanguage.INSTANCE);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new TJLexerAdapter();
    }

    @Override
    public @NotNull PsiParser createParser(Project project) {
        return new TJParser();
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return FILE;
    }

    @Override
    public @NotNull TokenSet getCommentTokens() {
        return TokenSet.EMPTY;
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return TJTokenSets.STRINGS;
    }

    @Override
    public @NotNull PsiElement createElement(ASTNode node) {
        return TJTypes.Factory.createElement(node);
    }

    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new TJFile(viewProvider);
    }
}
