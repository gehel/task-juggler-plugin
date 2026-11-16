package fr.ledcom.tj3.taskjugglerplugin.language.formatter;

import com.intellij.formatting.FormattingContext;
import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.formatting.FormattingModelProvider;
import com.intellij.formatting.Indent;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import fr.ledcom.tj3.taskjugglerplugin.TJLanguage;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;
import org.jetbrains.annotations.NotNull;

public class TJFormattingModelBuilder implements FormattingModelBuilder {

    @Override
    public @NotNull FormattingModel createModel(@NotNull FormattingContext context) {
        CodeStyleSettings settings = context.getCodeStyleSettings();
        TJBlock root = new TJBlock(context.getNode(), null, Indent.getNoneIndent(), spacing(settings));
        return FormattingModelProvider.createFormattingModelForPsiFile(context.getContainingFile(), root, settings);
    }

    static SpacingBuilder spacing(CodeStyleSettings settings) {
        return new SpacingBuilder(settings, TJLanguage.INSTANCE)
                .before(TJTypes.COMMA).spaceIf(false)
                .after(TJTypes.COMMA).spaceIf(true)
                .after(TJTypes.STATEMENT_HEAD).spaces(1)
                .before(TJTypes.BLOCK).spaces(1)
                .afterInside(TJTypes.L_CURLY, TJTypes.BLOCK).spaces(1)
                .beforeInside(TJTypes.R_CURLY, TJTypes.BLOCK).spaces(1);
    }
}
