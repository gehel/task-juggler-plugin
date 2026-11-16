package fr.ledcom.tj3.taskjugglerplugin.language.semantics;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJBlock;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJDateExpression;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJMacroCall;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJIdRef;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJLiteral;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJParenGroup;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatement;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;
import fr.ledcom.tj3.taskjugglerplugin.metadata.KeywordInfo;
import fr.ledcom.tj3.taskjugglerplugin.metadata.KeywordRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static fr.ledcom.tj3.taskjugglerplugin.language.semantics.PropertyKind.*;

/**
 * Meaning of statements and identifiers: which statements declare properties, and what an identifier refers to.
 * Keyword variants are identified by their metadata id (see {@link KeywordInfo#id()}).
 */
public final class TJSemantics {

    private static final Map<String, PropertyKind> DECLARATIONS = Map.ofEntries(
            Map.entry("project", PROJECT),
            Map.entry("task", TASK),
            Map.entry("resource", RESOURCE),
            Map.entry("account", ACCOUNT),
            Map.entry("shift", SHIFT),
            Map.entry("scenario", SCENARIO),
            Map.entry("accountreport", REPORT),
            Map.entry("export", REPORT),
            Map.entry("icalreport", REPORT),
            Map.entry("nikureport", REPORT),
            Map.entry("resourcereport", REPORT),
            Map.entry("statussheetreport", REPORT),
            Map.entry("taskreport", REPORT),
            Map.entry("textreport", REPORT),
            Map.entry("timesheetreport", REPORT),
            Map.entry("tracereport", REPORT),
            Map.entry("navigator", NAVIGATOR),
            Map.entry("macro", MACRO),
            Map.entry("flags", FLAG),
            Map.entry("text.extend", ATTRIBUTE),
            Map.entry("number.extend", ATTRIBUTE),
            Map.entry("date.extend", ATTRIBUTE),
            Map.entry("reference.extend", ATTRIBUTE),
            Map.entry("richtext.extend", ATTRIBUTE));

    private static final Map<String, PropertyKind> REFERENCES_BY_NAME = Map.ofEntries(
            Map.entry("depends", TASK),
            Map.entry("precedes", TASK),
            Map.entry("adopt", TASK),
            Map.entry("allocate", RESOURCE),
            Map.entry("responsible", RESOURCE),
            Map.entry("managers", RESOURCE),
            Map.entry("alternative", RESOURCE),
            Map.entry("resources", RESOURCE),
            Map.entry("author", RESOURCE),
            Map.entry("timesheet", RESOURCE),
            Map.entry("statussheet", RESOURCE),
            Map.entry("chargeset", ACCOUNT),
            Map.entry("balance", ACCOUNT),
            Map.entry("shifts", SHIFT),
            Map.entry("scenarios", SCENARIO),
            Map.entry("flags", FLAG));

    private static final Map<String, PropertyKind> REFERENCES_BY_ID = Map.ofEntries(
            Map.entry("task.statussheet", TASK),
            Map.entry("task.timesheet", TASK),
            Map.entry("account.task", ACCOUNT),
            Map.entry("booking.task", RESOURCE),
            Map.entry("scenario.ical", SCENARIO));

    private static final Map<String, PropertyKind> SUPPLEMENT_KINDS = Map.of(
            "task", TASK, "resource", RESOURCE, "account", ACCOUNT, "report", REPORT);

    /** Keywords whose statement only references its first argument (the rest are values). */
    private static final Set<String> FIRST_ARGUMENT_ONLY = Set.of(
            "booking", "timesheet", "statussheet", "author", "task", "account", "scenario");

    private TJSemantics() {
    }

    private static @NotNull String keywordId(@NotNull TJStatement statement) {
        KeywordInfo info = statement.getKeywordInfo();
        return info != null ? info.id() : statement.getKeywordName();
    }

    /** Kind of property declared by the statement, or {@code null} if it does not declare anything. */
    public static @Nullable PropertyKind declarationKind(@NotNull TJStatement statement) {
        KeywordInfo info = statement.getKeywordInfo();
        if (info == null) {
            // Unknown context (e.g. invalid nesting): fall back to the keyword name for the main properties.
            PropertyKind kind = DECLARATIONS.get(statement.getKeywordName());
            return kind != null && kind.isHierarchical() && !isInside(statement, "timesheet", "statussheet") ? kind : null;
        }
        return DECLARATIONS.get(info.id());
    }

    private static boolean isInside(TJStatement statement, String... keywords) {
        for (TJStatement p = statement.getParentStatement(); p != null; p = p.getParentStatement()) {
            for (String k : keywords) if (k.equals(p.getKeywordName())) return true;
        }
        return false;
    }

    /**
     * Value arguments of a statement in source order: id refs, literals, macro calls, expressions. Punctuation and
     * blocks are not included.
     */
    public static @NotNull List<PsiElement> arguments(@NotNull TJStatement statement) {
        List<PsiElement> result = new ArrayList<>();
        for (PsiElement child = statement.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof TJIdRef || child instanceof TJLiteral || child instanceof TJMacroCall
                || child instanceof TJParenGroup || child instanceof TJDateExpression) {
                result.add(child);
            }
        }
        return result;
    }

    /** The identifiers declared by a statement, e.g. {@code t} in {@code task t "T"} or each flag of {@code flags}. */
    public static @NotNull List<TJIdRef> declaredIds(@NotNull TJStatement statement) {
        PropertyKind kind = declarationKind(statement);
        if (kind == null) return List.of();
        List<PsiElement> args = arguments(statement);
        if (kind == FLAG) {
            List<TJIdRef> result = new ArrayList<>();
            for (PsiElement arg : args) {
                if (arg instanceof TJIdRef ref && ref.isSimple()) result.add(ref);
            }
            return result;
        }
        if (!args.isEmpty() && args.get(0) instanceof TJIdRef ref && ref.isSimple()) {
            // "task "Name"" has no id; "task id "Name"" has one.
            return List.of(ref);
        }
        return List.of();
    }

    /** The single id declared by a statement, if any. */
    public static @Nullable TJIdRef declaredId(@NotNull TJStatement statement) {
        if (declarationKind(statement) == FLAG) return null;
        List<TJIdRef> ids = declaredIds(statement);
        return ids.isEmpty() ? null : ids.get(0);
    }

    /** Whether the identifier is the name of a declaration (as opposed to a reference or a value). */
    public static boolean isDeclaration(@NotNull TJIdRef ref) {
        TJStatement statement = ownerStatement(ref);
        return statement != null && ref.getParent() == statement && declaredIds(statement).contains(ref);
    }

    /** The statement the identifier is an argument of (directly or inside an expression). */
    public static @Nullable TJStatement ownerStatement(@NotNull PsiElement element) {
        return PsiTreeUtil.getParentOfType(element, TJStatement.class, true, TJBlock.class);
    }

    public static boolean isInExpression(@NotNull TJIdRef ref) {
        return PsiTreeUtil.getParentOfType(ref, TJParenGroup.class, true, TJStatement.class) != null;
    }

    /** Whether the identifier is a function name in an expression, e.g. {@code isleaf()}. */
    public static boolean isFunctionCall(@NotNull TJIdRef ref) {
        PsiElement next = PsiTreeUtil.skipWhitespacesAndCommentsForward(ref);
        return next instanceof TJParenGroup && next.getTextRange().getStartOffset() == ref.getTextRange().getEndOffset();
    }

    /** Whether the identifier is one of the literal words accepted by its statement's keyword (e.g. asap, html). */
    public static boolean isValueWord(@NotNull TJIdRef ref) {
        if (!ref.isSimple()) return false;
        TJStatement statement = ownerStatement(ref);
        String text = ref.getText();
        KeywordRegistry registry = KeywordRegistry.getInstance();
        if (statement != null && registry.isArgumentWord(statement.getKeywordName(), text)) return true;
        return registry.isValueLiteral(text) && (statement == null || referenceKind(ref) == null);
    }

    /** Kind of property an identifier refers to, or {@code null} if it is not a reference. */
    public static @Nullable PropertyKind referenceKind(@NotNull TJIdRef ref) {
        TJStatement statement = ownerStatement(ref);
        if (statement == null || isFunctionCall(ref)) return null;
        if (isInExpression(ref)) return null;
        if (ref.getParent() != statement) return null;
        if (declaredIds(statement).contains(ref)) return null;

        String name = statement.getKeywordName();
        String id = keywordId(statement);
        List<PsiElement> args = arguments(statement);
        int index = args.indexOf(ref);

        if ("supplement".equals(name)) {
            if (index >= 1 && args.get(index - 1) instanceof TJIdRef type) {
                return SUPPLEMENT_KINDS.get(type.getText());
            }
            // supplement inside a task / resource: supplement.task is "supplement task <id>" too
            return null;
        }
        if (KeywordRegistry.getInstance().isArgumentWord(name, ref.getText())) return null;
        if (FIRST_ARGUMENT_ONLY.contains(name) && firstIdIndex(args) != index) return null;

        PropertyKind kind = REFERENCES_BY_ID.get(id);
        if (kind != null) return kind;
        if (id.startsWith("shift.") || id.startsWith("shifts.")) return SHIFT;
        if (id.startsWith("flags.")) return FLAG;
        if ("flags".equals(id)) return null; // top-level flags declares
        if (statement.getKeywordInfo() == null && "flags".equals(name)) return FLAG;
        return REFERENCES_BY_NAME.get(name);
    }

    private static int firstIdIndex(List<PsiElement> args) {
        for (int i = 0; i < args.size(); i++) {
            if (args.get(i) instanceof TJIdRef) return i;
        }
        return -1;
    }

    /** The first string argument of a declaring statement: the human readable name. */
    public static @Nullable String declaredName(@NotNull TJStatement statement) {
        for (PsiElement arg : arguments(statement)) {
            if (arg instanceof TJLiteral literal && literal.getNode().findChildByType(TJTypes.STRING) != null) {
                return literal.getStringValue();
            }
        }
        return null;
    }
}
