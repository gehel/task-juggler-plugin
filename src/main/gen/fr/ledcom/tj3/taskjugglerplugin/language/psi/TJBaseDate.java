// This is a generated file. Not intended for manual editing.
package fr.ledcom.tj3.taskjugglerplugin.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface TJBaseDate extends PsiElement {

  @NotNull
  TJDd getDd();

  @NotNull
  TJMm getMm();

  @Nullable
  TJTimezone getTimezone();

  @NotNull
  TJYyyy getYyyy();

  @Nullable
  TJHh getHh();

  @Nullable
  TJM getM();

  @Nullable
  TJSs getSs();

}
