package fr.ledcom.tj3.taskjugglerplugin.tj3.run;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.LazyRunConfigurationProducer;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import fr.ledcom.tj3.taskjugglerplugin.TJFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** "Run 'project.tjp'" from the editor or project view of a main TaskJuggler file. */
public class TJ3RunConfigurationProducer extends LazyRunConfigurationProducer<TJ3RunConfiguration> {

    @Override
    public @NotNull ConfigurationFactory getConfigurationFactory() {
        return TJ3RunConfigurationType.getInstance().getConfigurationFactories()[0];
    }

    private static @Nullable VirtualFile projectFile(ConfigurationContext context) {
        PsiElement location = context.getPsiLocation();
        PsiFile file = location == null ? null : location.getContainingFile();
        if (!(file instanceof TJFile) || file.getVirtualFile() == null) return null;
        VirtualFile virtualFile = file.getVirtualFile();
        return "tjp".equalsIgnoreCase(virtualFile.getExtension()) && virtualFile.isInLocalFileSystem() ? virtualFile : null;
    }

    @Override
    protected boolean setupConfigurationFromContext(@NotNull TJ3RunConfiguration configuration,
                                                    @NotNull ConfigurationContext context,
                                                    @NotNull Ref<PsiElement> sourceElement) {
        VirtualFile file = projectFile(context);
        if (file == null) return false;
        configuration.setProjectFile(file.getPath());
        configuration.setName(file.getName());
        return true;
    }

    @Override
    public boolean isConfigurationFromContext(@NotNull TJ3RunConfiguration configuration,
                                              @NotNull ConfigurationContext context) {
        VirtualFile file = projectFile(context);
        return file != null && file.getPath().equals(configuration.getProjectFile());
    }
}
