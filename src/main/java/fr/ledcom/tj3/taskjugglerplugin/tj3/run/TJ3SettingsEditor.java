package fr.ledcom.tj3.taskjugglerplugin.tj3.run;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.RawCommandLineEditor;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;

public class TJ3SettingsEditor extends SettingsEditor<TJ3RunConfiguration> {

    private final TextFieldWithBrowseButton projectFile = new TextFieldWithBrowseButton();
    private final TextFieldWithBrowseButton outputDirectory = new TextFieldWithBrowseButton();
    private final RawCommandLineEditor arguments = new RawCommandLineEditor();

    public TJ3SettingsEditor(Project project) {
        projectFile.addBrowseFolderListener(project, FileChooserDescriptorFactory.createSingleFileDescriptor("tjp")
                .withTitle("Select TaskJuggler Project File"));
        outputDirectory.addBrowseFolderListener(project, FileChooserDescriptorFactory.createSingleFolderDescriptor()
                .withTitle("Select Report Output Directory"));
    }

    @Override
    protected void resetEditorFrom(@NotNull TJ3RunConfiguration configuration) {
        projectFile.setText(configuration.getProjectFile());
        outputDirectory.setText(configuration.getOutputDirectory());
        arguments.setText(configuration.getArguments());
    }

    @Override
    protected void applyEditorTo(@NotNull TJ3RunConfiguration configuration) {
        configuration.setProjectFile(projectFile.getText().trim());
        configuration.setOutputDirectory(outputDirectory.getText().trim());
        configuration.setArguments(arguments.getText());
    }

    @Override
    protected @NotNull JComponent createEditor() {
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("Project file:", projectFile)
                .addLabeledComponent("Output directory:", outputDirectory)
                .addLabeledComponent("tj3 arguments:", arguments)
                .addComponentFillVertically(new javax.swing.JPanel(), 0)
                .getPanel();
    }
}
