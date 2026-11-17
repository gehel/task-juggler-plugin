package fr.ledcom.tj3.taskjugglerplugin.language.documentation;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJIdRef;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJLiteral;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatement;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJStatementHead;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;
import fr.ledcom.tj3.taskjugglerplugin.language.references.TJDeclaration;
import fr.ledcom.tj3.taskjugglerplugin.language.references.TJDeclarations;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.PropertyKind;
import fr.ledcom.tj3.taskjugglerplugin.language.semantics.TJSemantics;
import fr.ledcom.tj3.taskjugglerplugin.metadata.KeywordInfo;
import fr.ledcom.tj3.taskjugglerplugin.metadata.KeywordRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Quick documentation: keyword reference (from the TaskJuggler manual) for keywords, and a summary of the declaration
 * for task, resource, ... ids.
 */
public class TJDocumentationProvider extends AbstractDocumentationProvider {

    @Override
    public @Nullable PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file,
                                                              @Nullable PsiElement contextElement, int targetOffset) {
        if (contextElement == null || contextElement.getNode() == null) return null;
        if (contextElement.getNode().getElementType() != TJTypes.KEYWORD) return null;
        PsiElement parent = contextElement.getParent();
        if (parent instanceof TJStatementHead head && head.getParent() instanceof TJStatement statement) {
            KeywordInfo info = keywordOf(statement);
            return info == null ? null : new TJKeywordDocElement(statement, info);
        }
        if (parent instanceof TJIdRef ref && TJSemantics.isFunctionCall(ref)) {
            KeywordInfo info = KeywordRegistry.getInstance().byId(ref.getText());
            return info == null ? null : new TJKeywordDocElement(ref, info);
        }
        return null;
    }

    private static @Nullable KeywordInfo keywordOf(TJStatement statement) {
        KeywordInfo info = statement.getKeywordInfo();
        if (info != null) return info;
        List<KeywordInfo> variants = KeywordRegistry.getInstance().byName(statement.getKeywordName());
        return variants.isEmpty() ? null : variants.getFirst();
    }

    @Override
    public @Nullable PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object,
                                                                     PsiElement element) {
        return object instanceof KeywordInfo info && element != null ? new TJKeywordDocElement(element, info) : null;
    }

    @Override
    public @Nullable List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        return element instanceof TJKeywordDocElement doc ? List.of(doc.getKeyword().manualUrl()) : null;
    }

    @Override
    public @Nullable String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (element instanceof TJIdRef ref && TJSemantics.isDeclaration(ref)
            && ref.getParent() instanceof TJStatement statement) {
            PropertyKind kind = TJSemantics.declarationKind(statement);
            String name = TJSemantics.declaredName(statement);
            return (kind == null ? "" : kind.getDisplayName() + " ") + fullId(ref, kind)
                   + (name == null || name.isEmpty() ? "" : " \"" + name + "\"");
        }
        return null;
    }

    @Override
    public @Nullable String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element instanceof TJKeywordDocElement doc) return keywordDoc(doc.getKeyword());
        if (element instanceof TJIdRef ref && TJSemantics.isDeclaration(ref)
            && ref.getParent() instanceof TJStatement statement) {
            return declarationDoc(ref, statement);
        }
        return null;
    }

    static String keywordDoc(KeywordInfo info) {
        StringBuilder html = new StringBuilder();
        html.append(DocumentationMarkup.DEFINITION_START)
                .append("<b>").append(StringUtil.escapeXmlEntities(info.title())).append("</b>");
        if (info.syntax() != null) {
            html.append("<br/><code>").append(StringUtil.escapeXmlEntities(info.syntax())).append("</code>");
        }
        html.append(DocumentationMarkup.DEFINITION_END);

        html.append(DocumentationMarkup.CONTENT_START);
        if (!"supported".equals(info.supportLevel())) {
            html.append("<p><b>").append(supportLevelText(info)).append("</b></p>");
        }
        if (info.purposeHtml() != null) html.append(info.purposeHtml());
        html.append(DocumentationMarkup.CONTENT_END);

        html.append(DocumentationMarkup.SECTIONS_START);
        if (!info.args().isEmpty()) {
            StringBuilder args = new StringBuilder();
            for (KeywordInfo.Arg arg : info.args()) {
                args.append("<code>").append(StringUtil.escapeXmlEntities(arg.name())).append("</code>");
                if (arg.type() != null) args.append(" ").append(StringUtil.escapeXmlEntities(arg.type()));
                if (arg.doc() != null) args.append(": ").append(StringUtil.escapeXmlEntities(arg.doc()));
                args.append("<br/>");
            }
            section(html, "Arguments:", args.toString());
        }
        section(html, "Context:", info.contexts().isEmpty() ? "global scope" : join(info.contexts()));
        if (!info.attributes().isEmpty()) section(html, "Attributes:", join(info.attributes()));
        StringBuilder flags = new StringBuilder();
        if (info.scenarioSpecific()) flags.append("scenario specific<br/>");
        if (info.inheritedFromProject()) flags.append("inherited from global setting<br/>");
        if (info.inheritedFromParent()) flags.append("inherited from parent property<br/>");
        if (info.listAttribute()) flags.append("list attribute<br/>");
        if (!flags.isEmpty()) section(html, "Properties:", flags.toString());
        if (!info.seeAlso().isEmpty()) section(html, "See also:", join(info.seeAlso()));
        section(html, "Manual:", "<a href=\"" + info.manualUrl() + "\">" + info.manualUrl() + "</a>");
        html.append(DocumentationMarkup.SECTIONS_END);
        return html.toString();
    }

    private static String supportLevelText(KeywordInfo info) {
        return switch (info.supportLevel()) {
            case "deprecated" -> "Deprecated" + (info.seeAlso().isEmpty() ? "" : ", use " + join(info.seeAlso()));
            case "removed" -> "Removed from TaskJuggler";
            case "experimental" -> "Experimental";
            case "beta" -> "Beta";
            default -> info.supportLevel();
        };
    }

    private static String declarationDoc(TJIdRef ref, TJStatement statement) {
        PropertyKind kind = TJSemantics.declarationKind(statement);
        String name = TJSemantics.declaredName(statement);
        StringBuilder html = new StringBuilder();
        html.append(DocumentationMarkup.DEFINITION_START)
                .append(kind == null ? "" : kind.getDisplayName() + " ")
                .append("<b>").append(StringUtil.escapeXmlEntities(fullId(ref, kind))).append("</b>");
        if (name != null && !name.isEmpty()) html.append(" \"").append(StringUtil.escapeXmlEntities(name)).append("\"");
        html.append(DocumentationMarkup.DEFINITION_END);

        String note = attributeString(statement, "note");
        if (note != null) {
            html.append(DocumentationMarkup.CONTENT_START).append(StringUtil.escapeXmlEntities(note).replace("\n", "<br/>"))
                    .append(DocumentationMarkup.CONTENT_END);
        }
        html.append(DocumentationMarkup.SECTIONS_START);
        PsiFile file = statement.getContainingFile();
        if (file != null) section(html, "File:", StringUtil.escapeXmlEntities(file.getName()));
        if (statement.getBody() != null) {
            StringBuilder attrs = new StringBuilder();
            for (TJStatement child : PsiTreeUtil.getChildrenOfTypeAsList(statement.getBody(), TJStatement.class)) {
                if (TJSemantics.declarationKind(child) != null || "note".equals(child.getKeywordName())) continue;
                String text = child.getText().strip();
                if (text.contains("\n") || text.length() > 80) text = text.lines().findFirst().orElse("") + " ...";
                attrs.append("<code>").append(StringUtil.escapeXmlEntities(text)).append("</code><br/>");
            }
            if (!attrs.isEmpty()) section(html, "Attributes:", attrs.toString());
        }
        html.append(DocumentationMarkup.SECTIONS_END);
        return html.toString();
    }

    private static String fullId(TJIdRef ref, @Nullable PropertyKind kind) {
        if (kind != null && kind.hasQualifiedIds()) {
            for (TJDeclaration d : TJDeclarations.inFile(ref.getContainingFile())) {
                if (d.element() == ref) return d.fullId();
            }
        }
        return ref.getText();
    }

    private static @Nullable String attributeString(TJStatement statement, String keyword) {
        if (statement.getBody() == null) return null;
        for (TJStatement child : PsiTreeUtil.getChildrenOfTypeAsList(statement.getBody(), TJStatement.class)) {
            if (keyword.equals(child.getKeywordName())) {
                for (PsiElement arg : TJSemantics.arguments(child)) {
                    if (arg instanceof TJLiteral literal && literal.isString()) return literal.getStringValue();
                }
            }
        }
        return null;
    }

    private static void section(StringBuilder html, String title, String content) {
        html.append(DocumentationMarkup.SECTION_HEADER_START).append(title)
                .append(DocumentationMarkup.SECTION_SEPARATOR).append(content)
                .append(DocumentationMarkup.SECTION_END);
    }

    private static String join(List<String> items) {
        return StringUtil.escapeXmlEntities(String.join(", ", items));
    }
}
