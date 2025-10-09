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

public class TJBaseDateImpl extends ASTWrapperPsiElement implements TJBaseDate {

  public TJBaseDateImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TJVisitor visitor) {
    visitor.visitBaseDate(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TJVisitor) accept((TJVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public TJDd getDd() {
    return findNotNullChildByClass(TJDd.class);
  }

  @Override
  @NotNull
  public TJMm getMm() {
    return findNotNullChildByClass(TJMm.class);
  }

  @Override
  @Nullable
  public TJTimezone getTimezone() {
    return findChildByClass(TJTimezone.class);
  }

  @Override
  @NotNull
  public TJYyyy getYyyy() {
    return findNotNullChildByClass(TJYyyy.class);
  }

  @Override
  @Nullable
  public TJHh getHh() {
    return findChildByClass(TJHh.class);
  }

  @Override
  @Nullable
  public TJM getM() {
    return findChildByClass(TJM.class);
  }

  @Override
  @Nullable
  public TJSs getSs() {
    return findChildByClass(TJSs.class);
  }

}
