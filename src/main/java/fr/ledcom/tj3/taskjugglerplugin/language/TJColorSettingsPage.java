package fr.ledcom.tj3.taskjugglerplugin.language;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.openapi.util.NlsContexts;
import fr.ledcom.tj3.taskjugglerplugin.TJIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class TJColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Comment", TJSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Keyword", TJSyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("ID", TJSyntaxHighlighter.ID),
            new AttributesDescriptor("String", TJSyntaxHighlighter.STRING),
            new AttributesDescriptor("Coma", TJSyntaxHighlighter.COMA),
            new AttributesDescriptor("Brace", TJSyntaxHighlighter.BRACE),
            new AttributesDescriptor("Dot", TJSyntaxHighlighter.DOT),
            new AttributesDescriptor("Bad value", TJSyntaxHighlighter.BAD_CHARACTER),
    };

    @Override
    public @Nullable Icon getIcon() {
        return TJIcons.FILE;
    }

    @Override
    public @NotNull SyntaxHighlighter getHighlighter() {
        return new TJSyntaxHighlighter();
    }

    @Override
    public @NonNls @NotNull String getDemoText() {
        return """
                task my_task "some_task" {
                  note "some note"
                  depends !!some.other.task
                  duration 2w
                }
                """;
    }

    @Override
    public @Nullable Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @Override
    public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @Override
    public @NotNull @NlsContexts.ConfigurableName String getDisplayName() {
        return "TaskJuggler";
    }
}
