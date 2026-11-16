package fr.ledcom.tj3.taskjugglerplugin.language.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import fr.ledcom.tj3.taskjugglerplugin.metadata.KeywordRegistry;

import static fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes.*;

/*
 * Lexer for TaskJuggler project files, modelled after TaskJuggler's own
 * ProjectFileScanner.rb. Keywords are recognised by name (KeywordRegistry);
 * a word directly after '.' or '!' is always an identifier (e.g. "!deliveries.start").
 */
%%

%public
%class TaskJugglerLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType

%{
  /** Start offset of a multi-token construct (block comment, strings, macro definition). */
  private int tokenStart;

  private IElementType word() {
    return KeywordRegistry.getInstance().isKeyword(yytext().toString()) ? KEYWORD : ID;
  }

  /** Current line starts with the scissors end marker: keep it, push back the rest of the line. */
  private boolean endsScissors() {
    String line = yytext().toString();
    String trimmed = line.stripLeading();
    if (!trimmed.startsWith("->8-")) return false;
    int end = line.length() - trimmed.length() + 4;
    yypushback(line.length() - end);
    return true;
  }

  /** Current line (without line break and trailing blanks) ends with ']': it ends the macro body. */
  private boolean endsMacro() {
    String line = yytext().toString();
    String content = line.stripTrailing();
    if (!content.endsWith("]")) return false;
    yypushback(line.length() - content.length());
    return true;
  }

  private void startComposite(int state) {
    tokenStart = getTokenStart();
    yybegin(state);
  }

  private IElementType endComposite(IElementType type) {
    zzStartRead = tokenStart;
    yybegin(YYINITIAL);
    return type;
  }
%}

%eof{
  return;
%eof}

%xstate AFTER_DOT
%xstate BLOCK_COMMENT_STATE
%xstate DQ_STRING
%xstate SQ_STRING
%xstate SCISSORS_STRING
%xstate MACRO_DEF
%xstate MACRO_CALL

EOL=\R
WHITE_SPACE=[\ \t\f\r\n]+

WORD=[a-zA-Z_][a-zA-Z0-9_]*
DIGIT=[0-9]
INTEGER={DIGIT}+
FLOAT={DIGIT}*\.{DIGIT}+
DATE={DIGIT}{4}-{DIGIT}{1,2}-{DIGIT}{1,2}(-{DIGIT}{1,2}:{DIGIT}{1,2}(:{DIGIT}{1,2})?(-[-+]?{DIGIT}{4})?)?
TIME={DIGIT}{1,2}:{DIGIT}{2}
DURATION=({INTEGER}|{FLOAT})(min|h|d|w|m|y)

LINE_COMMENT=("#"|"//")[^\r\n]*

%%

<BLOCK_COMMENT_STATE> {
  "*/"                      { return endComposite(BLOCK_COMMENT); }
  [^*]+                     { }
  "*"                       { }
  <<EOF>>                   { return endComposite(BLOCK_COMMENT); }
}

<DQ_STRING> {
  \"                        { return endComposite(STRING); }
  \\\"                      { }
  [^\"\\]+                  { }
  \\                        { }
  <<EOF>>                   { return endComposite(STRING); }
}

<SQ_STRING> {
  \'                        { return endComposite(STRING); }
  \\\'                      { }
  [^\'\\]+                  { }
  \\                        { }
  <<EOF>>                   { return endComposite(STRING); }
}

// -8<- ... ->8- : the end marker must be the first non-blank text of a line.
// Content is consumed line by line (each rule match starts at a line start).
<SCISSORS_STRING> {
  [^\r\n]*{EOL}?            { if (endsScissors()) return endComposite(MULTILINE_STRING); }
  <<EOF>>                   { return endComposite(MULTILINE_STRING); }
}

// Macro body: from '[' up to the first line that ends with ']' (as TaskJuggler does).
<MACRO_DEF> {
  [^\r\n]*{EOL}?            { if (endsMacro()) return endComposite(MACRO_BODY); }
  <<EOF>>                   { return endComposite(MACRO_BODY); }
}

<MACRO_CALL> {
  "}"                       { yybegin(YYINITIAL); return MACRO_CALL_END; }
  "?"                       { return QUESTION; }
  {WORD}                    { return ID; }
  \"([^\"\\]|\\.)*\"        { return STRING; }
  [\ \t]+                   { return TokenType.WHITE_SPACE; }
  [^]                       { yypushback(1); yybegin(YYINITIAL); }
}

<AFTER_DOT> {
  {WORD}                    { yybegin(YYINITIAL); return ID; }
  [^]                       { yypushback(1); yybegin(YYINITIAL); }
}

<YYINITIAL> {
  {WHITE_SPACE}             { return TokenType.WHITE_SPACE; }

  {LINE_COMMENT}            { return COMMENT; }
  "/*"                      { startComposite(BLOCK_COMMENT_STATE); }

  "-8<-"[^\r\n]*            { startComposite(SCISSORS_STRING); }
  \"                        { startComposite(DQ_STRING); }
  \'                        { startComposite(SQ_STRING); }
  "["                       { startComposite(MACRO_DEF); }

  "${"                      { yybegin(MACRO_CALL); return MACRO_CALL_START; }
  "%{"                      { return DATE_EXPR_START; }
  "$("[A-Z_][A-Z0-9_]*")"   { return ENV_VAR; }

  {WORD}":"                 { return ID_WITH_COLON; }
  {WORD}/"."[a-zA-Z_]       { return ID; }
  {WORD}                    { return word(); }

  {DATE}                    { return DATE; }
  {TIME}                    { return TIME; }
  {DURATION}                { return DURATION; }
  {FLOAT}                   { return FLOAT; }
  {INTEGER}                 { return INTEGER; }

  "{"                       { return L_CURLY; }
  "}"                       { return R_CURLY; }
  "("                       { return L_PAREN; }
  ")"                       { return R_PAREN; }
  ","                       { return COMMA; }
  "."                       { yybegin(AFTER_DOT); return DOT; }
  "!="                      { return OPERATOR; }
  "!"                       { yybegin(AFTER_DOT); return BANG; }
  "-"                       { return MINUS; }
  "+"                       { return PLUS; }
  "~"                       { return TILDE; }
  "@"                       { return AT; }
  "<=" | ">=" | "<" | ">" | "=" | "&" | "|" | "*" | "/" | "%" | ":" | "?" | ";"
                            { return OPERATOR; }
}

[^]                         { return TokenType.BAD_CHARACTER; }
