package fr.ledcom.tj3.taskjugglerplugin.language.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Hand-written API of {@link TJIdRef}: {@code !!a.b.c}. */
public interface TJIdRefBase extends PsiElement {

    /** Number of leading '!' (each one moves the lookup scope one level up). */
    int getBangCount();

    /** Identifier segments, e.g. [a, b, c] for {@code !a.b.c}. */
    @NotNull List<String> getSegments();

    /** A plain identifier without '!' or '.'. */
    boolean isSimple();
}
