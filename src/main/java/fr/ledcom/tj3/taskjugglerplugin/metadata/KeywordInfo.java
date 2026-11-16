package fr.ledcom.tj3.taskjugglerplugin.metadata;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Documentation and structural metadata of one TaskJuggler keyword variant, as extracted from the TaskJuggler
 * syntax rules by {@code tools/extract-keywords.rb}.
 * <p>
 * The {@link #id} is unique ({@code "account.task"}), while several variants can share the same {@link #name}
 * ({@code "account"}) in different contexts.
 */
public record KeywordInfo(
        @NotNull String id,
        @NotNull String name,
        @NotNull String title,
        @NotNull String supportLevel,
        @Nullable String purpose,
        @Nullable String purposeHtml,
        @Nullable String syntax,
        @NotNull List<Arg> args,
        @NotNull List<String> contexts,
        boolean globalScope,
        @NotNull List<String> attributes,
        boolean property,
        boolean listAttribute,
        boolean scenarioSpecific,
        boolean inheritedFromProject,
        boolean inheritedFromParent,
        @NotNull List<String> seeAlso,
        @NotNull List<String> values) {

    public record Arg(@NotNull String name, @Nullable String type, @Nullable String doc) {
    }

    public boolean isDeprecated() {
        return "deprecated".equals(supportLevel) || "removed".equals(supportLevel);
    }

    public @NotNull String manualUrl() {
        return "https://taskjuggler.org/tj3/manual/" + id + ".html";
    }
}
