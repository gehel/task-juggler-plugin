package fr.ledcom.tj3.taskjugglerplugin.tj3.run;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import fr.ledcom.tj3.taskjugglerplugin.TJIcons;
import org.jetbrains.annotations.NotNull;

/** File | New | TaskJuggler File. */
public class TJCreateFileAction extends CreateFileFromTemplateAction implements DumbAware {

    public TJCreateFileAction() {
        super("TaskJuggler File", "Create a TaskJuggler project or include file", TJIcons.FILE);
    }

    @Override
    protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory directory,
                               CreateFileFromTemplateDialog.@NotNull Builder builder) {
        builder.setTitle("New TaskJuggler File")
                .addKind("Project file (.tjp)", TJIcons.FILE, "TaskJuggler Project")
                .addKind("Include file (.tji)", TJIcons.FILE, "TaskJuggler Include File");
    }

    @Override
    protected @NotNull String getActionName(PsiDirectory directory, @NotNull String newName, String templateName) {
        return "Create TaskJuggler File " + newName;
    }
}
