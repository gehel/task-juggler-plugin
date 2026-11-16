package fr.ledcom.tj3.taskjugglerplugin.language.references;

import com.intellij.navigation.ChooseByNameContributorEx;
import com.intellij.navigation.NavigationItem;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import com.intellij.util.indexing.FindSymbolParameters;
import com.intellij.util.indexing.IdFilter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Go to Symbol for TaskJuggler declarations, by id. */
public class TJGotoSymbolContributor implements ChooseByNameContributorEx {

    @Override
    public void processNames(@NotNull Processor<? super String> processor, @NotNull GlobalSearchScope scope,
                             @Nullable IdFilter filter) {
        if (scope.getProject() == null) return;
        for (TJDeclaration d : TJDeclarations.inProject(scope.getProject(), null)) {
            if (!processor.process(d.simpleId())) return;
        }
    }

    @Override
    public void processElementsWithName(@NotNull String name, @NotNull Processor<? super NavigationItem> processor,
                                        @NotNull FindSymbolParameters parameters) {
        for (TJDeclaration d : TJDeclarations.inProject(parameters.getProject(), null)) {
            if (d.simpleId().equals(name) && d.element() instanceof NavigationItem item && !processor.process(item)) {
                return;
            }
        }
    }
}
