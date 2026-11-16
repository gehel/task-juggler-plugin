package fr.ledcom.tj3.taskjugglerplugin.language.references;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.util.IncorrectOperationException;
import fr.ledcom.tj3.taskjugglerplugin.TJIcons;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJIdRef;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatement;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.PropertyKind;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.TJSemantics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reference from one segment of an identifier ({@code !a.b.c}) to a declared property.
 * <p>
 * Tasks and reports have hierarchical ids: segment {@code k} refers to the property whose full id is the prefix up to
 * {@code k}. Relative ids ({@code !}) start from the enclosing task, each {@code !} moving one level up. Other kinds
 * live in a flat namespace and are matched by id.
 */
public class TJPropertyReference extends PsiPolyVariantReferenceBase<TJIdRef> {

    private final PropertyKind kind;
    private final int segment;

    public TJPropertyReference(@NotNull TJIdRef element, @NotNull TextRange range, @NotNull PropertyKind kind,
                               int segment) {
        super(element, range, false);
        this.kind = kind;
        this.segment = segment;
    }

    public @NotNull PropertyKind getKind() {
        return kind;
    }

    /** Full id this segment refers to, e.g. {@code a.b} for segment 1 of {@code !a.b.c} at top level. */
    public @Nullable String targetFullId() {
        TJIdRef ref = getElement();
        List<String> segments = ref.getSegments();
        if (segment >= segments.size()) return null;
        if (!kind.hasQualifiedIds()) return segments.get(segment);
        List<String> path = new ArrayList<>(basePath());
        path.addAll(segments.subList(0, segment + 1));
        return String.join(".", path);
    }

    /** Path the id is relative to: the enclosing property path minus one level per '!', or empty if absolute. */
    private List<String> basePath() {
        TJIdRef ref = getElement();
        int bangs = ref.getBangCount();
        if (bangs == 0) return List.of();
        List<String> enclosing = TJDeclarations.enclosingPath(ref, kind);
        int keep = Math.max(0, enclosing.size() - bangs);
        return enclosing.subList(0, keep);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        String target = targetFullId();
        if (target == null) return ResolveResult.EMPTY_ARRAY;
        List<ResolveResult> results = new ArrayList<>();
        for (TJDeclaration d : TJDeclarations.ofKind(getElement(), kind)) {
            if (d.fullId().equals(target)) results.add(new PsiElementResolveResult(d.element()));
        }
        return results.toArray(ResolveResult.EMPTY_ARRAY);
    }

    @Override
    public @Nullable PsiElement resolve() {
        ResolveResult[] results = multiResolve(false);
        // Prefer the first (same file) declaration when ids are declared more than once.
        return results.length > 0 ? results[0].getElement() : null;
    }

    @Override
    public Object @NotNull [] getVariants() {
        TJIdRef ref = getElement();
        List<String> prefixPath = new ArrayList<>(isQualified() ? basePath() : List.of());
        if (isQualified()) prefixPath.addAll(ref.getSegments().subList(0, segment));
        String prefix = prefixPath.isEmpty() ? "" : String.join(".", prefixPath) + ".";

        Map<String, LookupElement> variants = new LinkedHashMap<>();
        for (TJDeclaration d : TJDeclarations.ofKind(ref, kind)) {
            String name;
            if (isQualified()) {
                if (!d.fullId().startsWith(prefix)) continue;
                name = d.fullId().substring(prefix.length());
                if (name.contains(".")) continue;
            } else {
                name = d.fullId();
            }
            if (name.startsWith("<") || d.element() == ref) continue;
            variants.putIfAbsent(name, lookup(name, d));
        }
        return variants.values().toArray();
    }

    private boolean isQualified() {
        return kind.hasQualifiedIds();
    }

    private static LookupElement lookup(String name, TJDeclaration d) {
        TJStatement statement = d.element().getParent() instanceof TJStatement s ? s : null;
        String title = statement == null ? null : TJSemantics.declaredName(statement);
        LookupElementBuilder builder = LookupElementBuilder.create(name)
                .withIcon(TJIcons.forKind(d.kind()))
                .withTypeText(d.kind().getDisplayName());
        if (title != null && !title.isEmpty()) builder = builder.withTailText(" \"" + title + "\"", true);
        if (!name.equals(d.fullId())) builder = builder.withLookupString(d.fullId());
        return builder;
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        return TJElementManipulators.replaceRange(getElement(), getRangeInElement(), newElementName);
    }
}
