package fr.ledcom.tj3.taskjugglerplugin.tj3;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.nio.file.Path;
import java.util.Objects;

/** Settings | Languages & Frameworks | TaskJuggler. */
public class TJSettingsConfigurable implements Configurable {

    private TextFieldWithBrowseButton tj3Path;
    private JBCheckBox checkSyntax;
    private JBLabel status;

    @Override
    public @Nls String getDisplayName() {
        return "TaskJuggler";
    }

    @Override
    public @Nullable JComponent createComponent() {
        tj3Path = new TextFieldWithBrowseButton();
        tj3Path.addBrowseFolderListener(null, FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
                .withTitle("Select the tj3 Executable"));
        checkSyntax = new JBCheckBox("Show errors reported by 'tj3 --check-syntax' (when files are saved)");
        status = new JBLabel();
        JPanel panel = FormBuilder.createFormBuilder()
                .addLabeledComponent("tj3 executable:", tj3Path)
                .addComponentToRightColumn(status)
                .addComponent(checkSyntax)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
        reset();
        return panel;
    }

    @Override
    public boolean isModified() {
        TJSettings.State state = TJSettings.getInstance().getState();
        return !Objects.equals(tj3Path.getText().trim(), state.tj3Path) || checkSyntax.isSelected() != state.checkSyntax;
    }

    @Override
    public void apply() {
        TJSettings.State state = TJSettings.getInstance().getState();
        state.tj3Path = tj3Path.getText().trim();
        state.checkSyntax = checkSyntax.isSelected();
        updateStatus();
    }

    @Override
    public void reset() {
        TJSettings.State state = TJSettings.getInstance().getState();
        tj3Path.setText(state.tj3Path);
        checkSyntax.setSelected(state.checkSyntax);
        updateStatus();
    }

    private void updateStatus() {
        Path executable = TJ3.executable();
        status.setText(executable == null ? "tj3 not found: install TaskJuggler (e.g. 'gem install taskjuggler')"
                : "Using " + executable.toAbsolutePath());
    }
}
