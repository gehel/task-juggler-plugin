package fr.ledcom.tj3.taskjugglerplugin.language.psi;

import com.intellij.psi.tree.IElementType;
import fr.ledcom.tj3.taskjugglerplugin.TJLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class TJElementType extends IElementType {

    public TJElementType(@NonNls @NotNull String debugName) {
        super(debugName, TJLanguage.INSTANCE);
    }
}
