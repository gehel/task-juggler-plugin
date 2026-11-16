package fr.ledcom.tj3.taskjugglerplugin.tj3.run;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.CommandLineState;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.LocatableConfigurationBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationError;
import com.intellij.execution.configurations.RuntimeConfigurationException;
import com.intellij.execution.filters.RegexpFilter;
import com.intellij.execution.process.KillableColoredProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.execution.ParametersListUtil;
import fr.ledcom.tj3.taskjugglerplugin.tj3.TJ3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Runs {@code tj3 [--output-dir dir] [arguments] project.tjp}. */
public class TJ3RunConfiguration extends LocatableConfigurationBase<TJ3RunConfigurationOptions> {

    protected TJ3RunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, String name) {
        super(project, factory, name);
    }

    @Override
    protected @NotNull TJ3RunConfigurationOptions getOptions() {
        return (TJ3RunConfigurationOptions) super.getOptions();
    }

    public String getProjectFile() {
        return getOptions().getProjectFile();
    }

    public void setProjectFile(String path) {
        getOptions().setProjectFile(path);
    }

    public String getOutputDirectory() {
        return getOptions().getOutputDirectory();
    }

    public void setOutputDirectory(String path) {
        getOptions().setOutputDirectory(path);
    }

    public String getArguments() {
        return getOptions().getArguments();
    }

    public void setArguments(String arguments) {
        getOptions().setArguments(arguments);
    }

    @Override
    public @NotNull SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new TJ3SettingsEditor(getProject());
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (StringUtil.isEmptyOrSpaces(getProjectFile())) {
            throw new RuntimeConfigurationError("No TaskJuggler project file selected");
        }
        if (!new File(getProjectFile()).isFile()) {
            throw new RuntimeConfigurationError("Project file not found: " + getProjectFile());
        }
        if (TJ3.executable() == null) {
            throw new RuntimeConfigurationError(
                    "tj3 not found: install TaskJuggler or set its path in Settings | Languages & Frameworks | TaskJuggler");
        }
    }

    @Override
    public @Nullable String suggestedName() {
        return StringUtil.isEmpty(getProjectFile()) ? null : new File(getProjectFile()).getName();
    }

    @Override
    public @Nullable RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
        return new CommandLineState(environment) {
            {
                addConsoleFilters(new RegexpFilter(getProject(), RegexpFilter.FILE_PATH_MACROS + ":" + RegexpFilter.LINE_MACROS));
            }

            @Override
            protected @NotNull ProcessHandler startProcess() throws ExecutionException {
                File executable = TJ3.executable();
                if (executable == null) throw new ExecutionException("tj3 executable not found");
                Path file = Path.of(getProjectFile()).toAbsolutePath();
                List<String> args = new ArrayList<>();
                args.add("--no-color");
                if (!StringUtil.isEmptyOrSpaces(getOutputDirectory())) {
                    args.add("--output-dir");
                    args.add(getOutputDirectory());
                }
                args.addAll(ParametersListUtil.parse(StringUtil.notNullize(getArguments())));
                args.add(file.toString());
                GeneralCommandLine command = TJ3.commandLine(executable, file.getParent(), args);
                ProcessHandler handler = new KillableColoredProcessHandler(command);
                ProcessTerminatedListener.attach(handler);
                return handler;
            }
        };
    }
}
