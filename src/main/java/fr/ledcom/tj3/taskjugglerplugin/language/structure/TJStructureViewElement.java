package fr.ledcom.tj3.taskjugglerplugin.language.structure;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJBlock;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatement;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.PropertyKind;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.TJSemantics;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Structure view node: the file or a statement that declares a property (project, task, resource, account, shift,
 * scenario, report, navigator, macro) or supplements one.
 */
public class TJStructureViewElement implements StructureViewTreeElement, SortableTreeElement {

    private final NavigatablePsiElement element;

    public TJStructureViewElement(@NotNull NavigatablePsiElement element) {
        this.element = element;
    }

    @Override
    public Object getValue() {
        return element;
    }

    @Override
    public void navigate(boolean requestFocus) {
        element.navigate(requestFocus);
    }

    @Override
    public boolean canNavigate() {
        return element.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return element.canNavigateToSource();
    }

    @Override
    public @NotNull String getAlphaSortKey() {
        String name = element instanceof PsiFile file ? file.getName() : getPresentation().getPresentableText();
        return name == null ? "" : name;
    }

    @Override
    public @NotNull ItemPresentation getPresentation() {
        ItemPresentation presentation = element.getPresentation();
        return presentation != null ? presentation : new PresentationData();
    }

    @Override
    public TreeElement @NotNull [] getChildren() {
        List<TreeElement> children = new ArrayList<>();
        collect(element instanceof TJStatement statement ? statement.getBody() : element, children);
        return children.toArray(TreeElement.EMPTY_ARRAY);
    }

    /** Collects structural statements below {@code parent}, looking through non-structural ones. */
    private static void collect(PsiElement parent, List<TreeElement> children) {
        if (parent == null) return;
        for (PsiElement child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof TJStatement statement) {
                if (isStructural(statement)) {
                    children.add(new TJStructureViewElement((NavigatablePsiElement) statement));
                } else {
                    collect(statement.getBody(), children);
                }
            } else if (child instanceof TJBlock) {
                collect(child, children);
            }
        }
    }

    static boolean isStructural(TJStatement statement) {
        if ("supplement".equals(statement.getKeywordName())) return true;
        PropertyKind kind = TJSemantics.declarationKind(statement);
        return kind != null && kind != PropertyKind.FLAG && kind != PropertyKind.ATTRIBUTE;
    }
}
