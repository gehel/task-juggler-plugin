package fr.ledcom.tj3.taskjugglerplugin.language.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.openapi.util.Key;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import fr.ledcom.tj3.taskjugglerplugin.metadata.KeywordRegistry;

import java.util.ArrayDeque;
import java.util.Deque;

import static fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes.*;

/**
 * External rules of the TaskJuggler grammar.
 * <p>
 * TaskJuggler statements have no terminator: the original parser knows the exact arity of each keyword. This
 * tolerant parser instead ends a statement's argument list when the next token looks like the start of a new
 * statement: a keyword that cannot be an argument of the current statement, or a word at the beginning of a line
 * that does not continue a list or an expression.
 */
public class TJParserUtil extends GeneratedParserUtilBase {

    private static final Key<Deque<Frame>> FRAMES = Key.create("tj.parser.frames");

    private static final TokenSet CONTINUATION = TokenSet.create(
            COMMA, OPERATOR, MINUS, PLUS, TILDE, BANG, AT, L_PAREN, DOT, QUESTION);
    private static final TokenSet BLOCK_FOLLOWERS_IN_LIST = TokenSet.create(COMMA);

    private record Frame(String head, int firstArgOffset) {
    }

    private static Deque<Frame> frames(PsiBuilder b) {
        Deque<Frame> frames = b.getUserData(FRAMES);
        if (frames == null) {
            frames = new ArrayDeque<>();
            b.putUserData(FRAMES, frames);
        }
        return frames;
    }

    /** Called right after a statement head: remembers the keyword for {@link #argumentAllowed}. */
    public static boolean enterStatement(PsiBuilder b, int level) {
        int i = previousVisible(b);
        String head = i == 0 ? "" : tokenText(b, i);
        frames(b).push(new Frame(head, b.getCurrentOffset()));
        return true;
    }

    public static boolean exitStatement(PsiBuilder b, int level) {
        Deque<Frame> frames = frames(b);
        if (!frames.isEmpty()) frames.pop();
        return true;
    }

    /** Whether the current token continues the argument list of the current statement. */
    public static boolean argumentAllowed(PsiBuilder b, int level) {
        IElementType type = b.getTokenType();
        if (type == null || type == R_CURLY || type == ID_WITH_COLON) return false;
        if (type != KEYWORD && type != ID && type != MACRO_CALL_START) return true;

        int prev = previousVisible(b);
        boolean continuation = prev != 0 && CONTINUATION.contains(b.rawLookup(prev));
        if (continuation) return true;
        if (startsLine(b)) return false;
        if (type != KEYWORD) return true;

        Frame frame = frames(b).peek();
        if (frame == null) return false;
        KeywordRegistry registry = KeywordRegistry.getInstance();
        if (b.getCurrentOffset() == frame.firstArgOffset() && registry.requiresArgument(frame.head())) return true;
        String text = b.getTokenText();
        return text != null && registry.isArgumentWord(frame.head(), text);
    }

    /** Whether the '{' at the current position is closed by a '}' that is directly followed by a ','. */
    public static boolean blockFollowedByComma(PsiBuilder b, int level) {
        if (b.getTokenType() != L_CURLY) return false;
        int depth = 0;
        for (int i = 0; ; i++) {
            IElementType t = b.rawLookup(i);
            if (t == null) return false;
            if (t == L_CURLY) depth++;
            else if (t == R_CURLY && --depth == 0) {
                IElementType next = b.rawLookup(nextVisible(b, i + 1));
                return next != null && BLOCK_FOLLOWERS_IN_LIST.contains(next);
            }
        }
    }

    private static boolean isHidden(IElementType t) {
        return t == TokenType.WHITE_SPACE || t == COMMENT || t == BLOCK_COMMENT;
    }

    /** Relative raw index of the previous non-whitespace, non-comment token, or 0 if none. */
    private static int previousVisible(PsiBuilder b) {
        for (int i = -1; ; i--) {
            IElementType t = b.rawLookup(i);
            if (t == null) return 0;
            if (!isHidden(t)) return i;
        }
    }

    private static int nextVisible(PsiBuilder b, int from) {
        int i = from;
        while (true) {
            IElementType t = b.rawLookup(i);
            if (t == null || !isHidden(t)) return i;
            i++;
        }
    }

    /** Whether there is a line break between the previous visible token and the current one. */
    private static boolean startsLine(PsiBuilder b) {
        CharSequence text = b.getOriginalText();
        for (int i = -1; ; i--) {
            IElementType t = b.rawLookup(i);
            if (t == null) return true;
            if (!isHidden(t)) return false;
            int start = b.rawTokenTypeStart(i);
            int end = b.rawTokenTypeStart(i + 1);
            if (t == COMMENT) return true; // line comments run up to the end of the line
            for (int j = start; j < end; j++) {
                if (text.charAt(j) == '\n') return true;
            }
        }
    }

    private static String tokenText(PsiBuilder b, int rawIndex) {
        return b.getOriginalText().subSequence(b.rawTokenTypeStart(rawIndex), b.rawTokenTypeStart(rawIndex + 1))
                .toString();
    }
}
