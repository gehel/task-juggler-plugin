package fr.ledcom.tj3.taskjugglerplugin.language.psi;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import fr.ledcom.tj3.taskjugglerplugin.TJLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class TjFileType extends LanguageFileType {

    public static final TjFileType INSTANCE = new TjFileType();

    private TjFileType() {
        super(TJLanguage.INSTANCE);
    }

    @Override
    public @NonNls @NotNull String getName() {
        return "TaskJuggler";
    }

    @Override
    public @NlsContexts.Label @NotNull String getDescription() {
        return "TaskJuggler project file";
    }

    @Override
    public @NlsSafe @NotNull String getDefaultExtension() {
        return "tjp";
    }

    @Override
    public Icon getIcon() {
        return null;
    }
}
