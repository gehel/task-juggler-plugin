package fr.ledcom.tj3.taskjugglerplugin.language.psi;

import com.intellij.psi.tree.IElementType;
import fr.ledcom.tj3.taskjugglerplugin.TJLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class TJTokenType extends IElementType {

    public TJTokenType(@NotNull @NonNls String debugName) {
        super(debugName, TJLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "TaskJugglerTokenType." + super.toString();
    }

}