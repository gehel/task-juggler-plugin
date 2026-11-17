package fr.ledcom.tj3.taskjugglerplugin.language.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import fr.ledcom.tj3.taskjugglerplugin.TJIcons;
import fr.ledcom.tj3.taskjugglerplugin.TJLanguage;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJIdRef;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatement;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatementHead;
import fr.ledcom.tj3.taskjugglerplugin.language.references.TJDeclaration;
import fr.ledcom.tj3.taskjugglerplugin.language.references.TJDeclarations;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.PropertyKind;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.TJSemantics;
import fr.ledcom.tj3.taskjugglerplugin.metadata.KeywordInfo;
import fr.ledcom.tj3.taskjugglerplugin.metadata.KeywordRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Context aware completion:
 * <ul>
 *     <li>at the start of a statement: the keywords allowed in the enclosing block, plus user defined attributes;</li>
 *     <li>in argument position: the literal values accepted by the statement's keyword (asap, html, ...).</li>
 * </ul>
 * References (tasks, resources, macros, scenarios...) are completed through their {@code PsiReference} variants.
 */
public class TJCompletionContributor extends CompletionContributor {

    public TJCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().withLanguage(TJLanguage.INSTANCE),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        complete(parameters.getPosition(), result);
                    }
                });
    }

    private static void complete(PsiElement position, CompletionResultSet result) {
        PsiElement parent = position.getParent();
        if (parent instanceof TJStatementHead head && head.getParent() instanceof TJStatement statement) {
            if (head.getScenarioPrefix() == null || head.getFirstChild() != position) {
                completeKeywords(statement, result);
            }
            return;
        }
        if (parent instanceof TJIdRef ref && ref.getBangCount() == 0 && ref.getSegments().size() == 1
            && !TJSemantics.isInExpression(ref)) {
            TJStatement statement = TJSemantics.ownerStatement(ref);
            if (statement != null && ref.getParent() == statement) completeValues(statement, result);
        }
    }

    private static void completeKeywords(TJStatement statement, CompletionResultSet result) {
        TJStatement parent = statement.getParentStatement();
        String context = parent == null ? null : parent.getBlockContextId();
        if (parent != null && context == null) return; // unknown context: nothing sensible to propose
        KeywordRegistry registry = KeywordRegistry.getInstance();
        Set<String> seen = new HashSet<>();
        for (KeywordInfo info : registry.allowedIn(context)) {
            if ("removed".equals(info.supportLevel()) || !seen.add(info.name())) continue;
            result.addElement(keywordLookup(info));
        }
        // User defined attributes (extend task/resource { text Phone "Phone" }) for tasks and resources.
        String target = context == null ? null : context.split("\\.")[0];
        if ("task".equals(target) || "resource".equals(target)) {
            for (TJDeclaration d : TJDeclarations.ofKind(statement, PropertyKind.ATTRIBUTE)) {
                if (seen.add(d.fullId()) && target.equals(extendedKind(d))) {
                    result.addElement(LookupElementBuilder.create(d.fullId())
                            .withIcon(TJIcons.forKind(PropertyKind.ATTRIBUTE))
                            .withTypeText("custom attribute")
                            .withInsertHandler(SPACE));
                }
            }
        }
    }

    /** {@code task} or {@code resource} for an attribute declared in {@code extend task { ... }}. */
    private static @Nullable String extendedKind(TJDeclaration attribute) {
        TJStatement declaration = attribute.element().getParent() instanceof TJStatement s ? s : null;
        TJStatement extend = declaration == null ? null : declaration.getParentStatement();
        if (extend == null || !"extend".equals(extend.getKeywordName())) return null;
        for (PsiElement arg : TJSemantics.arguments(extend)) return arg.getText();
        return null;
    }

    private static LookupElement keywordLookup(KeywordInfo info) {
        LookupElementBuilder builder = LookupElementBuilder.create(info, info.name())
                .withTypeText(typeText(info), true)
                .withStrikeoutness(info.isDeprecated())
                .withBoldness(info.property())
                .withInsertHandler(SPACE);
        if (info.syntax() != null) {
            String tail = info.syntax().startsWith(info.name()) ? info.syntax().substring(info.name().length()) : "";
            if (tail.length() > 40) tail = tail.substring(0, 40) + "...";
            builder = builder.withTailText(tail, true);
        }
        return PrioritizedLookupElement.withPriority(builder, info.isDeprecated() ? -1 : info.property() ? 1 : 0);
    }

    private static String typeText(KeywordInfo info) {
        if (info.property()) return "property";
        return switch (info.supportLevel()) {
            case "deprecated" -> "deprecated";
            case "experimental", "beta" -> info.supportLevel();
            default -> info.scenarioSpecific() ? "scenario specific" : "";
        };
    }

    private static void completeValues(TJStatement statement, CompletionResultSet result) {
        KeywordInfo info = statement.getKeywordInfo();
        Set<String> values = new HashSet<>(info != null ? info.values()
                : KeywordRegistry.getInstance().argumentWords(statement.getKeywordName()));
        if (info != null) {
            for (KeywordInfo.Arg arg : info.args()) {
                KeywordInfo argInfo = KeywordRegistry.getInstance().byId(arg.name());
                if (argInfo != null && arg.type() == null) values.addAll(argInfo.values());
            }
        }
        values.removeAll(Set.of("min", "h", "d", "w", "m", "y")); // duration units are typed with the number
        for (String value : values) {
            result.addElement(LookupElementBuilder.create(value).withTypeText(statement.getKeywordName() + " value"));
        }
    }

    private static final InsertHandler<LookupElement> SPACE = (context, _) -> {
        Document document = context.getDocument();
        int offset = context.getTailOffset();
        CharSequence text = document.getCharsSequence();
        if (offset >= text.length() || text.charAt(offset) != ' ' && text.charAt(offset) != '\t') {
            document.insertString(offset, " ");
        }
        context.getEditor().getCaretModel().moveToOffset(offset + 1);
    };
}
