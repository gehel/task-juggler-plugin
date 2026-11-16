package fr.ledcom.tj3.taskjugglerplugin.language.psi;

import com.intellij.psi.tree.TokenSet;

public interface TJTokenSets {

    TokenSet IDENTIFIERS = TokenSet.create(TJTypes.ID);

    TokenSet COMMENTS = TokenSet.create(TJTypes.COMMENT, TJTypes.BLOCK_COMMENT);
    TokenSet STRINGS = TokenSet.create(TJTypes.STRING, TJTypes.MULTILINE_STRING);
    TokenSet NUMBERS = TokenSet.create(TJTypes.INTEGER, TJTypes.FLOAT);
    TokenSet DATES = TokenSet.create(TJTypes.DATE, TJTypes.TIME, TJTypes.DURATION);
    TokenSet OPERATORS = TokenSet.create(TJTypes.OPERATOR, TJTypes.MINUS, TJTypes.PLUS, TJTypes.TILDE,
            TJTypes.AT, TJTypes.QUESTION);
}
