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

public class TJInterval2Impl extends ASTWrapperPsiElement implements TJInterval2 {

  public TJInterval2Impl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TJVisitor visitor) {
    visitor.visitInterval2(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TJVisitor) accept((TJVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<TJDate> getDateList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, TJDate.class);
  }

  @Override
  @Nullable
  public PsiElement getDuration_() {
    return findChildByType(DURATION_);
  }

}
