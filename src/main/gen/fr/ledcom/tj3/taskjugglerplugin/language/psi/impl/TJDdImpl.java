// This is a generated file. Not intended for manual editing.
package fr.ledcom.tj3.taskjugglerplugin.language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.*;

public class TJDdImpl extends ASTWrapperPsiElement implements TJDd {

  public TJDdImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TJVisitor visitor) {
    visitor.visitDd(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TJVisitor) accept((TJVisitor)visitor);
    else super.accept(visitor);
  }

}
