package fr.ledcom.tj3.taskjugglerplugin.language.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJIdRefBase;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class TJIdRefMixin extends ASTWrapperPsiElement implements TJIdRefBase {

    protected TJIdRefMixin(@NotNull ASTNode node) {
        super(node);
    }

    /** Number of leading '!' (each one moves the lookup scope one level up). */
    public int getBangCount() {
        int count = 0;
        for (ASTNode child = getNode().getFirstChildNode(); child != null && child.getElementType() == TJTypes.BANG;
             child = child.getTreeNext()) {
            count++;
        }
        return count;
    }

    /** Identifier segments, e.g. [a, b, c] for {@code !a.b.c}. */
    public @NotNull List<String> getSegments() {
        List<String> segments = new ArrayList<>();
        for (ASTNode child = getNode().getFirstChildNode(); child != null; child = child.getTreeNext()) {
            if (child.getElementType() == TJTypes.ID || child.getElementType() == TJTypes.KEYWORD) {
                segments.add(child.getText());
            }
        }
        return segments;
    }

    /** A plain identifier without '!' or '.'. */
    public boolean isSimple() {
        return getBangCount() == 0 && getSegments().size() == 1;
    }
}
