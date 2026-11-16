package fr.ledcom.tj3.taskjugglerplugin.language.formatter;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Formatting block: statements inside {@code { }} are indented; arguments continued on following lines are aligned
 * with the first argument (as in TaskJuggler's own examples); expressions get a continuation indent.
 */
public class TJBlock extends AbstractBlock {

    private static final TokenSet EXPRESSIONS = TokenSet.create(
            TJTypes.PAREN_GROUP, TJTypes.DATE_EXPRESSION, TJTypes.MACRO_CALL);

    private final Indent indent;
    private final SpacingBuilder spacingBuilder;

    TJBlock(@NotNull ASTNode node, @Nullable Alignment alignment, @NotNull Indent indent,
            @NotNull SpacingBuilder spacingBuilder) {
        super(node, null, alignment);
        this.indent = indent;
        this.spacingBuilder = spacingBuilder;
    }

    @Override
    protected List<Block> buildChildren() {
        List<Block> blocks = new ArrayList<>();
        IElementType type = myNode.getElementType();
        Alignment argumentAlignment = type == TJTypes.STATEMENT ? Alignment.createAlignment() : null;
        // "{ a 1\n  b 2 }": statements following one written on the line of '{' are aligned with it.
        Alignment statementAlignment = type == TJTypes.BLOCK && contentStartsOnBraceLine() ? Alignment.createAlignment() : null;
        for (ASTNode child = myNode.getFirstChildNode(); child != null; child = child.getTreeNext()) {
            if (child.getElementType() == TokenType.WHITE_SPACE || child.getTextLength() == 0) continue;
            Alignment alignment = statementAlignment != null && child.getElementType() != TJTypes.L_CURLY
                                  && child.getElementType() != TJTypes.R_CURLY
                    ? statementAlignment
                    : childAlignment(type, child, argumentAlignment);
            blocks.add(new TJBlock(child, alignment, childIndent(type, child), spacingBuilder));
        }
        return blocks;
    }

    private boolean contentStartsOnBraceLine() {
        ASTNode first = myNode.getFirstChildNode();
        if (first == null || first.getElementType() != TJTypes.L_CURLY) return false;
        ASTNode next = first.getTreeNext();
        if (next == null) return false;
        if (next.getElementType() == TokenType.WHITE_SPACE) {
            if (next.textContains('\n')) return false;
            next = next.getTreeNext();
        }
        return next != null && next.getElementType() != TJTypes.R_CURLY;
    }

    private static @Nullable Alignment childAlignment(IElementType parent, ASTNode child, @Nullable Alignment argument) {
        if (parent != TJTypes.STATEMENT || argument == null) return null;
        IElementType type = child.getElementType();
        if (type == TJTypes.STATEMENT_HEAD || type == TJTypes.BLOCK || type == TJTypes.COMMA) return null;
        if (TJTypes.COMMENT == type || TJTypes.BLOCK_COMMENT == type) return null;
        return argument;
    }

    private static Indent childIndent(IElementType parent, ASTNode child) {
        IElementType type = child.getElementType();
        if (parent == TJTypes.BLOCK) {
            return type == TJTypes.L_CURLY || type == TJTypes.R_CURLY ? Indent.getNoneIndent() : Indent.getNormalIndent();
        }
        if (parent == TJTypes.STATEMENT) {
            return type == TJTypes.STATEMENT_HEAD ? Indent.getNoneIndent() : Indent.getContinuationWithoutFirstIndent();
        }
        if (EXPRESSIONS.contains(parent)) return Indent.getContinuationWithoutFirstIndent();
        return Indent.getNoneIndent();
    }

    @Override
    public Indent getIndent() {
        return indent;
    }

    @Override
    public @Nullable Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return spacingBuilder.getSpacing(this, child1, child2);
    }

    @Override
    public @NotNull ChildAttributes getChildAttributes(int newChildIndex) {
        if (myNode.getElementType() == TJTypes.BLOCK) return new ChildAttributes(Indent.getNormalIndent(), null);
        return new ChildAttributes(Indent.getNoneIndent(), null);
    }

    @Override
    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }
}
