package fr.ledcom.tj3.taskjugglerplugin.language.psi;

import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

public interface TJTokenSets {

    TokenSet IDENTIFIERS = TokenSet.create(TJTypes.ID, TJTypes.ABSOLUTE_ID, TJTypes.RELATIVE_ID);

    TokenSet STRINGS = TokenSet.create(TJTypes.STRING);

    TokenSet COMMENTS = TokenSet.create(TJTypes.LINE_COMMENT);

    TokenSet COMPLEX_KEYWORDS = TokenSet.create(
            TJTypes.TASK, TJTypes.PROJECT
    );

    TokenSet SIMPLE_ATTRIBUTE_KEYWORDS = TokenSet.create(
            TJTypes.NOTE, TJTypes.DEPENDS, TJTypes.CURRENCY, TJTypes.DURATION
    );
}
