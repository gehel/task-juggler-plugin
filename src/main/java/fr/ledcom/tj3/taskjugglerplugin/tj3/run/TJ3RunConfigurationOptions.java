package fr.ledcom.tj3.taskjugglerplugin.tj3.run;

import com.intellij.execution.configurations.LocatableRunConfigurationOptions;
import com.intellij.openapi.components.StoredProperty;

public class TJ3RunConfigurationOptions extends LocatableRunConfigurationOptions {

    private final StoredProperty<String> projectFile = string("").provideDelegate(this, "projectFile");
    private final StoredProperty<String> outputDirectory = string("").provideDelegate(this, "outputDirectory");
    private final StoredProperty<String> arguments = string("").provideDelegate(this, "arguments");

    public String getProjectFile() {
        return projectFile.getValue(this);
    }

    public void setProjectFile(String value) {
        projectFile.setValue(this, value);
    }

    public String getOutputDirectory() {
        return outputDirectory.getValue(this);
    }

    public void setOutputDirectory(String value) {
        outputDirectory.setValue(this, value);
    }

    public String getArguments() {
        return arguments.getValue(this);
    }

    public void setArguments(String value) {
        arguments.setValue(this, value);
    }
}
