package fr.ledcom.tj3.taskjugglerplugin.language.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.util.IncorrectOperationException;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJIdRef;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJIdRefBase;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatement;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;
import fr.ledcom.tj3.taskjugglerplugin.language.references.TJElementManipulators;
import fr.ledcom.tj3.taskjugglerplugin.language.references.TJPropertyReference;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.PropertyKind;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.TJSemantics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Identifier, possibly qualified or relative. In declaration position ({@code task <id> "Name"}) it is the named
 * element that references resolve to; elsewhere it may carry one reference per segment.
 */
public abstract class TJIdRefMixin extends ASTWrapperPsiElement implements TJIdRefBase, PsiNameIdentifierOwner {

    protected TJIdRefMixin(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public int getBangCount() {
        int count = 0;
        for (ASTNode child = getNode().getFirstChildNode(); child != null && child.getElementType() == TJTypes.BANG;
             child = child.getTreeNext()) {
            count++;
        }
        return count;
    }

    private List<ASTNode> segmentNodes() {
        List<ASTNode> segments = new ArrayList<>();
        for (ASTNode child = getNode().getFirstChildNode(); child != null; child = child.getTreeNext()) {
            if (child.getElementType() == TJTypes.ID || child.getElementType() == TJTypes.KEYWORD) {
                segments.add(child);
            }
        }
        return segments;
    }

    @Override
    public @NotNull List<String> getSegments() {
        List<String> segments = new ArrayList<>();
        for (ASTNode node : segmentNodes()) segments.add(node.getText());
        return segments;
    }

    @Override
    public boolean isSimple() {
        return getBangCount() == 0 && segmentNodes().size() == 1;
    }

    private boolean isDeclaration() {
        return TJSemantics.isDeclaration((TJIdRef) this);
    }

    @Override
    public @Nullable PsiElement getNameIdentifier() {
        if (!isDeclaration()) return null;
        List<ASTNode> segments = segmentNodes();
        return segments.isEmpty() ? null : segments.getLast().getPsi();
    }

    @Override
    public @Nullable String getName() {
        PsiElement identifier = getNameIdentifier();
        return identifier == null ? null : identifier.getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return TJElementManipulators.replaceRange(this, new TextRange(0, getTextLength()), name);
    }

    @Override
    public int getTextOffset() {
        List<ASTNode> segments = segmentNodes();
        return segments.isEmpty() ? super.getTextOffset() : segments.getLast().getStartOffset();
    }

    @Override
    public PsiReference @NotNull [] getReferences() {
        return CachedValuesManager.getCachedValue(this, () -> CachedValueProvider.Result.create(
                computeReferences(), PsiModificationTracker.MODIFICATION_COUNT));
    }

    private PsiReference[] computeReferences() {
        TJIdRef self = (TJIdRef) this;
        PropertyKind kind = TJSemantics.referenceKind(self);
        if (kind == null) return PsiReference.EMPTY_ARRAY;
        List<ASTNode> segments = segmentNodes();
        int start = getNode().getStartOffset();
        List<PsiReference> references = new ArrayList<>();
        if (kind.hasQualifiedIds()) {
            for (int i = 0; i < segments.size(); i++) {
                ASTNode node = segments.get(i);
                references.add(new TJPropertyReference(self, TextRange.from(node.getStartOffset() - start,
                        node.getTextLength()), kind, i));
            }
        } else if (!segments.isEmpty()) {
            ASTNode last = segments.getLast();
            references.add(new TJPropertyReference(self, TextRange.from(last.getStartOffset() - start,
                    last.getTextLength()), kind, segments.size() - 1));
        }
        return references.toArray(PsiReference.EMPTY_ARRAY);
    }

    @Override
    public @Nullable PsiReference getReference() {
        PsiReference[] references = getReferences();
        return references.length == 0 ? null : references[references.length - 1];
    }

    @Override
    public ItemPresentation getPresentation() {
        if (isDeclaration() && getParent() instanceof TJStatement statement
            && TJSemantics.declarationKind(statement) != PropertyKind.FLAG) {
            return statement.getPresentation();
        }
        return super.getPresentation();
    }
}
