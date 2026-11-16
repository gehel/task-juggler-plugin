package fr.ledcom.tj3.taskjugglerplugin.language.references;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.util.IncorrectOperationException;
import fr.ledcom.tj3.taskjugglerplugin.TJIcons;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.PropertyKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Reference by plain name to a macro, scenario or user defined attribute. */
public class TJNameReference<T extends PsiElement> extends PsiPolyVariantReferenceBase<T> {

    private final PropertyKind kind;

    public TJNameReference(@NotNull T element, @NotNull TextRange range, @NotNull PropertyKind kind) {
        super(element, range, false);
        this.kind = kind;
    }

    public @NotNull PropertyKind getKind() {
        return kind;
    }

    /** Built-in names that are valid although not declared in any file. */
    public boolean isBuiltin() {
        return kind == PropertyKind.MACRO && TJDeclarations.BUILTIN_MACROS.contains(getValue());
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        String name = getValue();
        List<ResolveResult> results = new ArrayList<>();
        for (TJDeclaration d : TJDeclarations.ofKind(getElement(), kind)) {
            if (d.fullId().equals(name)) results.add(new PsiElementResolveResult(d.element()));
        }
        return results.toArray(ResolveResult.EMPTY_ARRAY);
    }

    @Override
    public @Nullable PsiElement resolve() {
        ResolveResult[] results = multiResolve(false);
        return results.length > 0 ? results[0].getElement() : null;
    }

    @Override
    public Object @NotNull [] getVariants() {
        Map<String, Object> variants = new LinkedHashMap<>();
        for (TJDeclaration d : TJDeclarations.ofKind(getElement(), kind)) {
            variants.putIfAbsent(d.fullId(), LookupElementBuilder.create(d.fullId())
                    .withIcon(TJIcons.forKind(kind)).withTypeText(kind.getDisplayName()));
        }
        if (kind == PropertyKind.MACRO) {
            for (String builtin : TJDeclarations.BUILTIN_MACROS) {
                variants.putIfAbsent(builtin, LookupElementBuilder.create(builtin)
                        .withIcon(TJIcons.forKind(kind)).withTypeText("built-in macro"));
            }
        }
        return variants.values().toArray();
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        return TJElementManipulators.replaceRange(getElement(), getRangeInElement(), newElementName);
    }
}
