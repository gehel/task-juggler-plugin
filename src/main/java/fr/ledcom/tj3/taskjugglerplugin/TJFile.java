package fr.ledcom.tj3.taskjugglerplugin;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TjFileType;
import org.jetbrains.annotations.NotNull;

public class TJFile extends PsiFileBase {

    public TJFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, TJLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return TjFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Task Juggler File";
    }

}
