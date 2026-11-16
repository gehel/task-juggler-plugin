package fr.ledcom.tj3.taskjugglerplugin.language.highlighting;

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
            new AttributesDescriptor("Comments//Line comment", TJSyntaxHighlighter.LINE_COMMENT),
            new AttributesDescriptor("Comments//Block comment", TJSyntaxHighlighter.BLOCK_COMMENT),
            new AttributesDescriptor("Keywords//Keyword", TJSyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("Keywords//Property keyword (task, resource, ...)", TJSyntaxHighlighter.PROPERTY_KEYWORD),
            new AttributesDescriptor("Keywords//User defined attribute", TJSyntaxHighlighter.CUSTOM_ATTRIBUTE),
            new AttributesDescriptor("Keywords//Literal value (asap, html, ...)", TJSyntaxHighlighter.VALUE),
            new AttributesDescriptor("Keywords//Function call", TJSyntaxHighlighter.FUNCTION),
            new AttributesDescriptor("Identifiers//Identifier", TJSyntaxHighlighter.ID),
            new AttributesDescriptor("Identifiers//Declaration", TJSyntaxHighlighter.DECLARATION),
            new AttributesDescriptor("Identifiers//Reference", TJSyntaxHighlighter.REFERENCE),
            new AttributesDescriptor("Identifiers//Scenario prefix", TJSyntaxHighlighter.SCENARIO_PREFIX),
            new AttributesDescriptor("Values//String", TJSyntaxHighlighter.STRING),
            new AttributesDescriptor("Values//Multi-line string (-8<- ->8-)", TJSyntaxHighlighter.MULTILINE_STRING),
            new AttributesDescriptor("Values//Number", TJSyntaxHighlighter.NUMBER),
            new AttributesDescriptor("Values//Date and time", TJSyntaxHighlighter.DATE),
            new AttributesDescriptor("Values//Duration", TJSyntaxHighlighter.DURATION),
            new AttributesDescriptor("Macros//Macro body", TJSyntaxHighlighter.MACRO_BODY),
            new AttributesDescriptor("Macros//Macro call", TJSyntaxHighlighter.MACRO_CALL),
            new AttributesDescriptor("Macros//Environment variable", TJSyntaxHighlighter.ENV_VAR),
            new AttributesDescriptor("Braces and Operators//Operator", TJSyntaxHighlighter.OPERATOR),
            new AttributesDescriptor("Braces and Operators//Comma", TJSyntaxHighlighter.COMMA),
            new AttributesDescriptor("Braces and Operators//Dot", TJSyntaxHighlighter.DOT),
            new AttributesDescriptor("Braces and Operators//Braces", TJSyntaxHighlighter.BRACES),
            new AttributesDescriptor("Braces and Operators//Parentheses", TJSyntaxHighlighter.PARENTHESES),
            new AttributesDescriptor("Bad character", TJSyntaxHighlighter.BAD_CHARACTER),
    };

    @Override
    public @Nullable Icon getIcon() {
        return TJIcons.FILE;
    }

    @Override
    public @NotNull SyntaxHighlighter getHighlighter() {
        return new TJSyntaxHighlighter();
    }

    private static final Map<String, TextAttributesKey> TAGS = Map.of(
            "prop", TJSyntaxHighlighter.PROPERTY_KEYWORD,
            "decl", TJSyntaxHighlighter.DECLARATION,
            "ref", TJSyntaxHighlighter.REFERENCE,
            "value", TJSyntaxHighlighter.VALUE,
            "attr", TJSyntaxHighlighter.CUSTOM_ATTRIBUTE,
            "func", TJSyntaxHighlighter.FUNCTION,
            "macro", TJSyntaxHighlighter.MACRO_CALL);

    @Override
    public @NonNls @NotNull String getDemoText() {
        return """
                /* Project header */
                project <decl>acme</decl> "ACME" 2024-01-01 +6m {
                  timezone "Europe/Paris"
                  <prop>scenario</prop> <decl>plan</decl> "Plan" {
                    <prop>scenario</prop> <decl>delayed</decl> "Delayed"
                  }
                  extend resource { text <decl>Phone</decl> "Phone" }
                }
                flags <decl>team</decl>
                macro <decl>devs</decl> [
                  allocate dev1
                ]
                <prop>resource</prop> <decl>dev1</decl> "Alice" {
                  <attr>Phone</attr> "x100"
                  limits { dailymax 6.4h }
                  workinghours <value>sat</value>, <value>sun</value> <value>off</value>
                }
                <prop>task</prop> <decl>spec</decl> "Specification" {
                  effort 20d
                  ${<macro>devs</macro>}
                  depends !<ref>kickoff</ref>, <ref>other.task</ref>
                  delayed:start 2024-01-20-9:00
                  scheduling <value>asap</value>
                  note -8<-
                    Multi-line text
                  ->8-
                }
                <prop>taskreport</prop> <decl>overview</decl> "" {
                  formats <value>html</value>  # line comment
                  columns <value>name</value>, <value>start</value> { title 'Start' }
                  hidetask ~(<func>isleaf</func>() & plan.end < %{${<macro>now</macro>} - 1w})
                  loadunit $(UNIT)
                }
                """;
    }

    @Override
    public @Nullable Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return TAGS;
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
