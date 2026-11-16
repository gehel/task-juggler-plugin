package fr.ledcom.tj3.taskjugglerplugin.language;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import static fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes.TASK_KEYWORD;

public class TJSyntaxHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey COMMENT =
            createTextAttributesKey("TJ_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("TJ_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey ID =
            createTextAttributesKey("TJ_ID", DefaultLanguageHighlighterColors.IDENTIFIER);
    public static final TextAttributesKey STRING =
            createTextAttributesKey("TJ_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("TJ_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey COMA =
            createTextAttributesKey("TJ_COMA", DefaultLanguageHighlighterColors.COMMA);
    public static final TextAttributesKey BRACE =
            createTextAttributesKey("TJ_BRACE", DefaultLanguageHighlighterColors.BRACES);
    public static final TextAttributesKey DOT =
            createTextAttributesKey("TJ_DOT", DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("SIMPLE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] ID_KEYS = new TextAttributesKey[]{ID};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING};
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] COMA_KEYS = new TextAttributesKey[]{COMA};
    private static final TextAttributesKey[] BRACE_KEYS = new TextAttributesKey[]{BRACE};
    private static final TextAttributesKey[] DOT_KEYS = new TextAttributesKey[]{DOT};

    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};

    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new TJLexerAdapter();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(TJTypes.COMMENT))
            return COMMENT_KEYS;
        if (List.of(
                TJTypes.DEPENDS_KEYWORD,
                TJTypes.TASK_KEYWORD,
                TJTypes.START_KEYWORD,
                TJTypes.END_KEYWORD,
                TJTypes.DURATION_KEYWORD,
                TJTypes.NOTE_KEYWORD,
                TJTypes.RESPONSIBLE_KEYWORD).contains(tokenType))
            return KEYWORD_KEYS;
        if (List.of(TJTypes.ID).contains(tokenType))
            return ID_KEYS;
        if (tokenType.equals(TJTypes.STRING))
            return STRING_KEYS;
        if (tokenType.equals(TJTypes.INTEGER))
            return NUMBER_KEYS;
        if (tokenType.equals(TJTypes.COMA))
            return COMA_KEYS;
        if (List.of(TJTypes.L_CURLY, TJTypes.R_CURLY).contains(tokenType))
            return BRACE_KEYS;
        if (tokenType.equals(TJTypes.DOT))
            return DOT_KEYS;

        if (tokenType.equals(TokenType.BAD_CHARACTER))
            return BAD_CHAR_KEYS;

        return EMPTY_KEYS;
    }
}
