// This is a generated file. Not intended for manual editing.
package fr.ledcom.tj3.taskjugglerplugin.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface TJProject extends PsiElement {

  @NotNull
  TJInterval2 getInterval2();

  @NotNull
  List<TJCurrency> getCurrencyList();

  @NotNull
  List<TJTimezone> getTimezoneList();

  @Nullable
  PsiElement getId();

}
