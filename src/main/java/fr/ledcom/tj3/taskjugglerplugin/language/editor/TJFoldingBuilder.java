package fr.ledcom.tj3.taskjugglerplugin.language.editor;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJBlock;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/** Folds multi-line blocks, block comments, -8<- strings and macro bodies. */
public class TJFoldingBuilder extends FoldingBuilderEx implements DumbAware {

    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document,
                                                          boolean quick) {
        List<FoldingDescriptor> result = new ArrayList<>();
        for (TJBlock block : PsiTreeUtil.findChildrenOfType(root, TJBlock.class)) {
            addIfMultiline(result, block.getNode(), document);
        }
        collectTokens(root.getNode(), document, result);
        return result.toArray(FoldingDescriptor.EMPTY_ARRAY);
    }

    private static void collectTokens(ASTNode node, Document document, List<FoldingDescriptor> result) {
        for (ASTNode child = node.getFirstChildNode(); child != null; child = child.getTreeNext()) {
            IElementType type = child.getElementType();
            if (type == TJTypes.BLOCK_COMMENT || type == TJTypes.MULTILINE_STRING || type == TJTypes.MACRO_BODY) {
                addIfMultiline(result, child, document);
            } else if (child.getFirstChildNode() != null) {
                collectTokens(child, document, result);
            }
        }
    }

    private static void addIfMultiline(List<FoldingDescriptor> result, ASTNode node, Document document) {
        TextRange range = node.getTextRange();
        if (range.getLength() < 2) return;
        if (document.getLineNumber(range.getStartOffset()) == document.getLineNumber(range.getEndOffset())) return;
        result.add(new FoldingDescriptor(node, range));
    }

    @Override
    public @Nullable String getPlaceholderText(@NotNull ASTNode node) {
        IElementType type = node.getElementType();
        if (type == TJTypes.BLOCK_COMMENT) return "/*...*/";
        if (type == TJTypes.MULTILINE_STRING) return "-8<- " + firstLine(node.getText().substring(4)) + " ->8-";
        if (type == TJTypes.MACRO_BODY) return "[...]";
        return "{...}";
    }

    private static String firstLine(String text) {
        for (String line : text.split("\n")) {
            String trimmed = line.strip();
            if (!trimmed.isEmpty()) return trimmed.length() > 40 ? trimmed.substring(0, 40) + "..." : trimmed + "...";
        }
        return "...";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}
