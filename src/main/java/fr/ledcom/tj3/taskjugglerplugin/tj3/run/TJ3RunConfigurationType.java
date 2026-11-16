package fr.ledcom.tj3.taskjugglerplugin.tj3.run;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.components.BaseState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NotNullLazyValue;
import fr.ledcom.tj3.taskjugglerplugin.TJIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Run configurations that schedule a TaskJuggler project with tj3 and generate its reports. */
public final class TJ3RunConfigurationType extends ConfigurationTypeBase {

    public static final String ID = "TaskJugglerRunConfiguration";

    public TJ3RunConfigurationType() {
        super(ID, "TaskJuggler", "Schedule a TaskJuggler project and generate its reports with tj3",
                NotNullLazyValue.createValue(() -> TJIcons.FILE));
        addFactory(new ConfigurationFactory(this) {
            @Override
            public @NotNull String getId() {
                return ID;
            }

            @Override
            public @NotNull RunConfiguration createTemplateConfiguration(@NotNull Project project) {
                return new TJ3RunConfiguration(project, this, "TaskJuggler");
            }

            @Override
            public @Nullable Class<? extends BaseState> getOptionsClass() {
                return TJ3RunConfigurationOptions.class;
            }
        });
    }

    public static TJ3RunConfigurationType getInstance() {
        return ConfigurationTypeUtil.findConfigurationType(TJ3RunConfigurationType.class);
    }
}
