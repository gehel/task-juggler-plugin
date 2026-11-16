package fr.ledcom.tj3.taskjugglerplugin.language;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTokenSets;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class TJSyntaxHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey LINE_COMMENT =
            createTextAttributesKey("TJ_LINE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey BLOCK_COMMENT =
            createTextAttributesKey("TJ_BLOCK_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("TJ_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey ID =
            createTextAttributesKey("TJ_ID", DefaultLanguageHighlighterColors.IDENTIFIER);
    public static final TextAttributesKey STRING =
            createTextAttributesKey("TJ_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey MULTILINE_STRING =
            createTextAttributesKey("TJ_MULTILINE_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NUMBER =
            createTextAttributesKey("TJ_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey DATE =
            createTextAttributesKey("TJ_DATE", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey DURATION =
            createTextAttributesKey("TJ_DURATION", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey MACRO_BODY =
            createTextAttributesKey("TJ_MACRO_BODY", DefaultLanguageHighlighterColors.TEMPLATE_LANGUAGE_COLOR);
    public static final TextAttributesKey MACRO_CALL =
            createTextAttributesKey("TJ_MACRO_CALL", DefaultLanguageHighlighterColors.METADATA);
    public static final TextAttributesKey ENV_VAR =
            createTextAttributesKey("TJ_ENV_VAR", DefaultLanguageHighlighterColors.CONSTANT);
    public static final TextAttributesKey SCENARIO_PREFIX =
            createTextAttributesKey("TJ_SCENARIO_PREFIX", DefaultLanguageHighlighterColors.LABEL);
    public static final TextAttributesKey OPERATOR =
            createTextAttributesKey("TJ_OPERATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey COMMA =
            createTextAttributesKey("TJ_COMMA", DefaultLanguageHighlighterColors.COMMA);
    public static final TextAttributesKey DOT =
            createTextAttributesKey("TJ_DOT", DefaultLanguageHighlighterColors.DOT);
    public static final TextAttributesKey BRACES =
            createTextAttributesKey("TJ_BRACES", DefaultLanguageHighlighterColors.BRACES);
    public static final TextAttributesKey PARENTHESES =
            createTextAttributesKey("TJ_PARENTHESES", DefaultLanguageHighlighterColors.PARENTHESES);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("TJ_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    // Semantic highlighting, applied by TJHighlightingAnnotator.
    public static final TextAttributesKey DECLARATION =
            createTextAttributesKey("TJ_DECLARATION", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
    public static final TextAttributesKey REFERENCE =
            createTextAttributesKey("TJ_REFERENCE", DefaultLanguageHighlighterColors.INSTANCE_FIELD);
    public static final TextAttributesKey VALUE =
            createTextAttributesKey("TJ_VALUE", DefaultLanguageHighlighterColors.CONSTANT);
    public static final TextAttributesKey CUSTOM_ATTRIBUTE =
            createTextAttributesKey("TJ_CUSTOM_ATTRIBUTE", DefaultLanguageHighlighterColors.INSTANCE_METHOD);
    public static final TextAttributesKey PROPERTY_KEYWORD =
            createTextAttributesKey("TJ_PROPERTY_KEYWORD", KEYWORD);
    public static final TextAttributesKey FUNCTION =
            createTextAttributesKey("TJ_FUNCTION", DefaultLanguageHighlighterColors.FUNCTION_CALL);

    private static final Map<IElementType, TextAttributesKey> KEYS = new HashMap<>();

    static {
        KEYS.put(TJTypes.COMMENT, LINE_COMMENT);
        KEYS.put(TJTypes.BLOCK_COMMENT, BLOCK_COMMENT);
        KEYS.put(TJTypes.KEYWORD, KEYWORD);
        KEYS.put(TJTypes.ID, ID);
        KEYS.put(TJTypes.STRING, STRING);
        KEYS.put(TJTypes.MULTILINE_STRING, MULTILINE_STRING);
        KEYS.put(TJTypes.INTEGER, NUMBER);
        KEYS.put(TJTypes.FLOAT, NUMBER);
        KEYS.put(TJTypes.DATE, DATE);
        KEYS.put(TJTypes.TIME, DATE);
        KEYS.put(TJTypes.DURATION, DURATION);
        KEYS.put(TJTypes.MACRO_BODY, MACRO_BODY);
        KEYS.put(TJTypes.MACRO_CALL_START, MACRO_CALL);
        KEYS.put(TJTypes.MACRO_CALL_END, MACRO_CALL);
        KEYS.put(TJTypes.DATE_EXPR_START, DATE);
        KEYS.put(TJTypes.ENV_VAR, ENV_VAR);
        KEYS.put(TJTypes.ID_WITH_COLON, SCENARIO_PREFIX);
        for (IElementType t : TJTokenSets.OPERATORS.getTypes()) KEYS.put(t, OPERATOR);
        KEYS.put(TJTypes.BANG, OPERATOR);
        KEYS.put(TJTypes.COMMA, COMMA);
        KEYS.put(TJTypes.DOT, DOT);
        KEYS.put(TJTypes.L_CURLY, BRACES);
        KEYS.put(TJTypes.R_CURLY, BRACES);
        KEYS.put(TJTypes.L_PAREN, PARENTHESES);
        KEYS.put(TJTypes.R_PAREN, PARENTHESES);
        KEYS.put(TokenType.BAD_CHARACTER, BAD_CHARACTER);
    }

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new TJLexerAdapter();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        return pack(KEYS.get(tokenType));
    }
}
