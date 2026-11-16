package fr.ledcom.tj3.taskjugglerplugin.language.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public abstract class TJScenarioPrefixMixin extends ASTWrapperPsiElement {

    protected TJScenarioPrefixMixin(@NotNull ASTNode node) {
        super(node);
    }
}
