package fr.ledcom.tj3.taskjugglerplugin.language.editor;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;

public class TJQuoteHandler extends SimpleTokenSetQuoteHandler {

    public TJQuoteHandler() {
        super(TJTypes.STRING);
    }
}
