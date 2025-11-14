package fr.ledcom.tj3.taskjugglerplugin.language.psi;

import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

public interface TJTokenSets {

    TokenSet IDENTIFIERS = TokenSet.create(TJTypes.ID);

    TokenSet COMMENTS = TokenSet.create(TJTypes.COMMENT);
    TokenSet STRINGS = TokenSet.create(TJTypes.STRING);
}
