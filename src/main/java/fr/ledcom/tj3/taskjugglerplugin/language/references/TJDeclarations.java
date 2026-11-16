package fr.ledcom.tj3.taskjugglerplugin.language.references;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import fr.ledcom.tj3.taskjugglerplugin.TJFile;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJBlock;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJIdRef;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatement;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TjFileType;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.PropertyKind;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.TJSemantics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Collects the properties declared in TaskJuggler files.
 * <p>
 * TaskJuggler projects consist of a main {@code .tjp} file and included {@code .tji} files, so declarations are looked
 * up in all TaskJuggler files of the project, preferring the file of the reference.
 */
public final class TJDeclarations {

    /** Macros defined by TaskJuggler itself. */
    public static final Set<String> BUILTIN_MACROS = Set.of("projectstart", "projectend", "now", "today", "markdate");

    private TJDeclarations() {
    }

    /** Declarations of one file, cached until the file changes. */
    public static @NotNull List<TJDeclaration> inFile(@NotNull PsiFile file) {
        if (!(file instanceof TJFile)) return List.of();
        return CachedValuesManager.getCachedValue(file, () -> {
            List<TJDeclaration> result = new ArrayList<>();
            collect(file, List.of(), List.of(), result);
            return CachedValueProvider.Result.create(List.copyOf(result), file);
        });
    }

    /** Declarations of all TaskJuggler files of the project; those of {@code preferred} first. */
    public static @NotNull List<TJDeclaration> inProject(@NotNull Project project, @Nullable PsiFile preferred) {
        List<TJDeclaration> all = allInProject(project);
        PsiFile original = preferred == null ? null : preferred.getOriginalFile();
        if (original == null) return all;
        List<TJDeclaration> result = new ArrayList<>(inFile(original));
        for (TJDeclaration d : all) {
            if (d.element().getContainingFile() != original) result.add(d);
        }
        return result;
    }

    private static List<TJDeclaration> allInProject(Project project) {
        return CachedValuesManager.getManager(project).getCachedValue(project, () -> {
            List<TJDeclaration> result = new ArrayList<>();
            PsiManager manager = PsiManager.getInstance(project);
            Collection<VirtualFile> files =
                    FileTypeIndex.getFiles(TjFileType.INSTANCE, GlobalSearchScope.allScope(project));
            for (VirtualFile virtualFile : files) {
                PsiFile file = manager.findFile(virtualFile);
                if (file != null) result.addAll(inFile(file));
            }
            return CachedValueProvider.Result.create(List.copyOf(result), PsiModificationTracker.MODIFICATION_COUNT);
        });
    }

    public static @NotNull List<TJDeclaration> ofKind(@NotNull PsiElement context, @NotNull PropertyKind kind) {
        List<TJDeclaration> result = new ArrayList<>();
        for (TJDeclaration d : inProject(context.getProject(), context.getContainingFile())) {
            if (d.kind() == kind) result.add(d);
        }
        return result;
    }

    private static void collect(PsiElement parent, List<String> taskPath, List<String> reportPath,
                                List<TJDeclaration> result) {
        for (PsiElement child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof TJStatement statement) {
                collectStatement(statement, taskPath, reportPath, result);
            } else if (child instanceof TJBlock) {
                // blocks attached to list items (columns a { ... }, b)
                collect(child, taskPath, reportPath, result);
            }
        }
    }

    private static void collectStatement(TJStatement statement, List<String> taskPath, List<String> reportPath,
                                         List<TJDeclaration> result) {
        PropertyKind kind = TJSemantics.declarationKind(statement);
        List<String> innerTasks = taskPath;
        List<String> innerReports = reportPath;
        if (kind != null) {
            List<TJIdRef> ids = TJSemantics.declaredIds(statement);
            for (TJIdRef id : ids) {
                String fullId = switch (kind) {
                    case TASK -> join(taskPath, id.getText());
                    case REPORT -> join(reportPath, id.getText());
                    default -> id.getText();
                };
                result.add(new TJDeclaration(kind, fullId, id));
            }
            String segment = ids.isEmpty() ? anonymous(statement) : ids.get(0).getText();
            if (kind == PropertyKind.TASK) innerTasks = append(taskPath, segment);
            if (kind == PropertyKind.REPORT) innerReports = append(reportPath, segment);
        } else if ("supplement".equals(statement.getKeywordName())) {
            TJIdRef target = supplementTarget(statement);
            if (target != null) {
                String type = supplementType(statement);
                if ("task".equals(type)) innerTasks = target.getSegments();
                if ("report".equals(type)) innerReports = target.getSegments();
            }
        }
        // The trailing block and the blocks of list items.
        collect(statement, innerTasks, innerReports, result);
    }

    /** Placeholder path segment for properties without id: their children cannot be referenced by full id. */
    private static String anonymous(TJStatement statement) {
        return "<" + statement.getTextOffset() + ">";
    }

    private static List<String> append(List<String> path, String segment) {
        List<String> result = new ArrayList<>(path);
        result.add(segment);
        return List.copyOf(result);
    }

    private static String join(List<String> path, String id) {
        return path.isEmpty() ? id : String.join(".", path) + "." + id;
    }

    /** {@code task} in {@code supplement task a.b { }}. */
    public static @Nullable String supplementType(TJStatement statement) {
        for (PsiElement arg : TJSemantics.arguments(statement)) {
            return arg instanceof TJIdRef ref && ref.isSimple() ? ref.getText() : null;
        }
        return null;
    }

    /** {@code a.b} in {@code supplement task a.b { }}. */
    public static @Nullable TJIdRef supplementTarget(TJStatement statement) {
        List<PsiElement> args = TJSemantics.arguments(statement);
        return args.size() >= 2 && args.get(1) instanceof TJIdRef ref ? ref : null;
    }

    /**
     * Full id path of the task (or report) enclosing {@code element}, following enclosing declarations and
     * {@code supplement} statements.
     */
    public static @NotNull List<String> enclosingPath(@NotNull PsiElement element, @NotNull PropertyKind kind) {
        List<String> reversed = new ArrayList<>();
        TJStatement statement = element instanceof TJStatement s ? s : TJSemantics.ownerStatement(element);
        for (; statement != null; statement = statement.getParentStatement()) {
            if (TJSemantics.declarationKind(statement) == kind) {
                TJIdRef id = TJSemantics.declaredId(statement);
                reversed.add(id != null ? id.getText() : anonymous(statement));
            } else if ("supplement".equals(statement.getKeywordName())
                       && kind.getDisplayName().equals(supplementType(statement))) {
                TJIdRef target = supplementTarget(statement);
                if (target != null) {
                    List<String> segments = target.getSegments();
                    for (int i = segments.size() - 1; i >= 0; i--) reversed.add(segments.get(i));
                }
                break;
            }
        }
        List<String> path = new ArrayList<>();
        for (int i = reversed.size() - 1; i >= 0; i--) path.add(reversed.get(i));
        return path;
    }
}
