package fr.ledcom.tj3.taskjugglerplugin.language;

import org.jetbrains.annotations.NotNull;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;

import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;

public final class TJCompletionContributor extends CompletionContributor {

    public TJCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(TJTypes.TASK_BODY_ELEMENT),
                new CompletionProvider<>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               @NotNull ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        resultSet.addElement(LookupElementBuilder.create("task"));
                    }
                }
        );
    }

}