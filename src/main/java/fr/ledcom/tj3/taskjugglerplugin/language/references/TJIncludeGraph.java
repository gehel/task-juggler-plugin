package fr.ledcom.tj3.taskjugglerplugin.language.references;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.psi.util.PsiTreeUtil;
import fr.ledcom.tj3.taskjugglerplugin.TJFile;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJLiteral;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatement;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TjFileType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Which TaskJuggler files form one project: a main file and everything it (transitively) includes. Several
 * independent projects may live in the same IDE project.
 */
public final class TJIncludeGraph {

    private TJIncludeGraph() {
    }

    /** Files directly included by {@code file}. */
    public static @NotNull List<VirtualFile> includes(@NotNull PsiFile file) {
        return CachedValuesManager.getCachedValue(file, () -> {
            List<VirtualFile> result = new ArrayList<>();
            for (TJStatement statement : PsiTreeUtil.findChildrenOfType(file, TJStatement.class)) {
                if (!"include".equals(statement.getKeywordName())) continue;
                for (TJLiteral literal : PsiTreeUtil.getChildrenOfTypeAsList(statement, TJLiteral.class)) {
                    PsiReference[] references = literal.getReferences();
                    if (references.length == 0) continue;
                    PsiElement target = references[references.length - 1].resolve();
                    if (target instanceof PsiFile included && included.getVirtualFile() != null) {
                        result.add(included.getVirtualFile());
                    }
                }
            }
            return CachedValueProvider.Result.create(List.copyOf(result), PsiModificationTracker.MODIFICATION_COUNT);
        });
    }

    /**
     * The files whose declarations are visible from {@code file}: the include closures of all files that include it
     * (directly or not), or its own include closure if nothing includes it.
     */
    public static @NotNull Set<VirtualFile> scope(@NotNull PsiFile file) {
        VirtualFile virtualFile = file.getOriginalFile().getVirtualFile();
        if (virtualFile == null) return Set.of();
        Map<VirtualFile, Set<VirtualFile>> scopes = scopes(file.getProject());
        Set<VirtualFile> scope = scopes.get(virtualFile);
        return scope != null ? scope : Set.of(virtualFile);
    }

    private static Map<VirtualFile, Set<VirtualFile>> scopes(Project project) {
        return CachedValuesManager.getManager(project).getCachedValue(project, () -> CachedValueProvider.Result.create(
                computeScopes(project), PsiModificationTracker.MODIFICATION_COUNT));
    }

    private static Map<VirtualFile, Set<VirtualFile>> computeScopes(Project project) {
        PsiManager manager = PsiManager.getInstance(project);
        Collection<VirtualFile> files = FileTypeIndex.getFiles(TjFileType.INSTANCE, GlobalSearchScope.allScope(project));
        Map<VirtualFile, List<VirtualFile>> edges = new HashMap<>();
        Set<VirtualFile> included = new LinkedHashSet<>();
        for (VirtualFile f : files) {
            PsiFile psi = manager.findFile(f);
            List<VirtualFile> targets = psi instanceof TJFile ? includes(psi) : List.of();
            edges.put(f, targets);
            included.addAll(targets);
        }
        // Roots: files not included by any other file (cycles: every file of the cycle is treated as a root).
        Map<VirtualFile, Set<VirtualFile>> result = new HashMap<>();
        for (VirtualFile f : files) {
            if (included.contains(f) && !reaches(edges, f, f)) continue;
            Set<VirtualFile> closure = closure(edges, f);
            for (VirtualFile member : closure) {
                result.computeIfAbsent(member, k -> new LinkedHashSet<>()).addAll(closure);
            }
        }
        return result;
    }

    private static Set<VirtualFile> closure(Map<VirtualFile, List<VirtualFile>> edges, VirtualFile root) {
        Set<VirtualFile> seen = new LinkedHashSet<>();
        Deque<VirtualFile> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            VirtualFile f = queue.poll();
            if (seen.add(f)) queue.addAll(edges.getOrDefault(f, List.of()));
        }
        return seen;
    }

    private static boolean reaches(Map<VirtualFile, List<VirtualFile>> edges, VirtualFile from, VirtualFile to) {
        Set<VirtualFile> seen = new LinkedHashSet<>();
        Deque<VirtualFile> queue = new ArrayDeque<>(edges.getOrDefault(from, List.of()));
        while (!queue.isEmpty()) {
            VirtualFile f = queue.poll();
            if (f.equals(to)) return true;
            if (seen.add(f)) queue.addAll(edges.getOrDefault(f, List.of()));
        }
        return false;
    }
}
