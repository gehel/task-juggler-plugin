// This is a generated file. Not intended for manual editing.
package fr.ledcom.tj3.taskjugglerplugin.language.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface TJTask extends PsiElement {

  @NotNull
  List<TJTask> getTaskList();

  @NotNull
  List<TJDepends> getDependsList();

  @NotNull
  List<TJDuration> getDurationList();

  @NotNull
  List<TJNote> getNoteList();

  @NotNull
  List<TJResponsible> getResponsibleList();

  @NotNull
  List<TJStart> getStartList();

  @Nullable
  PsiElement getId();

  @NotNull
  PsiElement getString();

}
