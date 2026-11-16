package fr.ledcom.tj3.taskjugglerplugin.language.completion;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import fr.ledcom.tj3.taskjugglerplugin.TJFile;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJLiteral;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/** Live templates in TaskJuggler files, outside of comments and strings. */
public class TJTemplateContextType extends TemplateContextType {

    public TJTemplateContextType() {
        super("TaskJuggler");
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext context) {
        if (!(context.getFile() instanceof TJFile file)) return false;
        PsiElement element = file.findElementAt(Math.max(0, context.getStartOffset() - 1));
        return !(element instanceof PsiComment) && PsiTreeUtil.getParentOfType(element, TJLiteral.class) == null;
    }
}
