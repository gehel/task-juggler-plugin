package fr.ledcom.tj3.taskjugglerplugin.metadata;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Keyword metadata of the TaskJuggler language, loaded from {@code /tj3/keywords.json}.
 */
public final class KeywordRegistry {

    /** Pseudo context id used by TaskJuggler for the top level of a project file. */
    public static final String TOP_LEVEL = "properties";
    public static final String PROJECT = "project";

    private static final String RESOURCE = "/tj3/keywords.json";

    private static volatile KeywordRegistry instance;

    private final String tjVersion;
    private final Map<String, KeywordInfo> byId;
    private final Map<String, List<KeywordInfo>> byName;
    private final Set<String> keywordNames;
    private final Set<String> valueLiterals;
    private final Map<String, Set<String>> argumentWords;
    private final Set<String> requiringArgument;

    private KeywordRegistry(String tjVersion, Map<String, KeywordInfo> byId, Set<String> keywordNames,
                            Set<String> valueLiterals) {
        this.tjVersion = tjVersion;
        this.byId = Collections.unmodifiableMap(byId);
        Map<String, List<KeywordInfo>> names = new LinkedHashMap<>();
        for (KeywordInfo info : byId.values()) {
            names.computeIfAbsent(info.name(), n -> new ArrayList<>()).add(info);
        }
        this.byName = Collections.unmodifiableMap(names);
        this.keywordNames = Collections.unmodifiableSet(keywordNames);
        this.valueLiterals = Collections.unmodifiableSet(valueLiterals);

        Map<String, Set<String>> words = new LinkedHashMap<>();
        Set<String> requiring = new LinkedHashSet<>();
        for (KeywordInfo info : byId.values()) {
            Set<String> w = words.computeIfAbsent(info.name(), n -> new LinkedHashSet<>());
            w.addAll(info.values());
            for (KeywordInfo.Arg arg : info.args()) {
                // Arguments documented by another keyword (e.g. columns <columnid>) inherit its literal values.
                KeywordInfo argInfo = byId.get(arg.name());
                if (argInfo != null && arg.type() == null) w.addAll(argInfo.values());
            }
            String syntax = info.syntax();
            if (syntax != null && syntax.startsWith(info.name())) {
                String rest = syntax.substring(info.name().length()).trim();
                if (!rest.isEmpty() && !rest.startsWith("[{")) requiring.add(info.name());
            }
        }
        this.argumentWords = Collections.unmodifiableMap(words);
        this.requiringArgument = Collections.unmodifiableSet(requiring);
    }

    public static @NotNull KeywordRegistry getInstance() {
        KeywordRegistry result = instance;
        if (result == null) {
            synchronized (KeywordRegistry.class) {
                result = instance;
                if (result == null) {
                    instance = result = load();
                }
            }
        }
        return result;
    }

    private static KeywordRegistry load() {
        try (InputStream in = KeywordRegistry.class.getResourceAsStream(RESOURCE)) {
            if (in == null) throw new IllegalStateException("Missing resource " + RESOURCE);
            JsonObject root = new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), JsonObject.class);
            Map<String, KeywordInfo> byId = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> e : root.getAsJsonObject("keywords").entrySet()) {
                byId.put(e.getKey(), parse(e.getKey(), e.getValue().getAsJsonObject()));
            }
            return new KeywordRegistry(
                    root.get("tjVersion").getAsString(),
                    byId,
                    new LinkedHashSet<>(strings(root.getAsJsonArray("keywordNames"))),
                    new LinkedHashSet<>(strings(root.getAsJsonArray("valueLiterals"))));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static KeywordInfo parse(String id, JsonObject o) {
        List<KeywordInfo.Arg> args = new ArrayList<>();
        for (JsonElement a : o.getAsJsonArray("args")) {
            JsonObject ao = a.getAsJsonObject();
            args.add(new KeywordInfo.Arg(str(ao, "name"), str(ao, "type"), str(ao, "doc")));
        }
        return new KeywordInfo(
                id,
                str(o, "name"),
                str(o, "title"),
                str(o, "supportLevel"),
                str(o, "purpose"),
                str(o, "purposeHtml"),
                str(o, "syntax"),
                List.copyOf(args),
                strings(o.getAsJsonArray("contexts")),
                o.get("globalScope").getAsBoolean(),
                strings(o.getAsJsonArray("attributes")),
                o.get("property").getAsBoolean(),
                o.get("listAttribute").getAsBoolean(),
                o.get("scenarioSpecific").getAsBoolean(),
                o.get("inheritedFromProject").getAsBoolean(),
                o.get("inheritedFromParent").getAsBoolean(),
                strings(o.getAsJsonArray("seeAlso")),
                strings(o.getAsJsonArray("values")));
    }

    private static @Nullable String str(JsonObject o, String key) {
        JsonElement e = o.get(key);
        return e == null || e.isJsonNull() ? null : e.getAsString();
    }

    private static List<String> strings(JsonArray array) {
        List<String> result = new ArrayList<>(array.size());
        array.forEach(e -> result.add(e.getAsString()));
        return List.copyOf(result);
    }

    /** Version of TaskJuggler the metadata was extracted from. */
    public @NotNull String getTjVersion() {
        return tjVersion;
    }

    public @Nullable KeywordInfo byId(@NotNull String id) {
        return byId.get(id);
    }

    public @NotNull Collection<KeywordInfo> all() {
        return byId.values();
    }

    /** All variants sharing the given keyword name, e.g. {@code account} and {@code account.task}. */
    public @NotNull List<KeywordInfo> byName(@NotNull String name) {
        return byName.getOrDefault(name, List.of());
    }

    public boolean isKeyword(@NotNull String word) {
        return keywordNames.contains(word);
    }

    /** Words that only appear as literal values in the grammar (e.g. {@code asap}, {@code html}). */
    public boolean isValueLiteral(@NotNull String word) {
        return valueLiterals.contains(word);
    }

    /**
     * Whether {@code word} is a literal value accepted as argument of keyword {@code keyword} (in any context),
     * e.g. {@code headline} for {@code journalattributes} or {@code start} for {@code columns}.
     */
    public boolean isArgumentWord(@NotNull String keyword, @NotNull String word) {
        Set<String> words = argumentWords.get(keyword);
        return words != null && words.contains(word);
    }

    /** Literal values accepted by any variant of the given keyword. */
    public @NotNull Set<String> argumentWords(@NotNull String keyword) {
        return argumentWords.getOrDefault(keyword, Set.of());
    }

    /** Whether some variant of the keyword takes at least one argument right after the keyword. */
    public boolean requiresArgument(@NotNull String keyword) {
        return requiringArgument.contains(keyword);
    }

    public @NotNull Set<String> getKeywordNames() {
        return keywordNames;
    }

    /**
     * Keywords that may appear directly inside the given context.
     *
     * @param context keyword id of the enclosing block ({@code task}, {@code supplement.task}, ...), or {@code null}
     *                for the top level of a file
     */
    public @NotNull List<KeywordInfo> allowedIn(@Nullable String context) {
        List<KeywordInfo> result = new ArrayList<>();
        if (context == null) {
            // The project header is the only top-level keyword that is not one of the 'properties'.
            result.add(byId.get(PROJECT));
            context = TOP_LEVEL;
        }
        KeywordInfo ctx = byId.get(context);
        if (ctx != null) {
            for (String attr : ctx.attributes()) {
                KeywordInfo info = byId.get(attr);
                if (info != null) result.add(info);
            }
        }
        return result;
    }

    /**
     * Resolves a keyword name to its variant valid in the given context.
     *
     * @param name    keyword as written in the file
     * @param context keyword id of the enclosing block, or {@code null} for the top level
     * @return the matching variant, or {@code null} if the keyword is not allowed in that context
     */
    public @Nullable KeywordInfo resolve(@NotNull String name, @Nullable String context) {
        for (KeywordInfo info : allowedIn(context)) {
            if (info.name().equals(name)) return info;
        }
        return null;
    }
}
