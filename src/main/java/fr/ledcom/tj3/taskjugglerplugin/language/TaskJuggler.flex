package fr.ledcom.tj3.taskjugglerplugin.language;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;

%%

%class TaskJugglerLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF=\R
WHITE_SPACE=[\ \n\t\f]

END_OF_LINE_COMMENT=("#"|"!")[^\r\n]*

// Keywords
TASK_KEYWORD="task"
DEPENDS_KEYWORD="depends"
NOTE_KEYWORD="note"

// Identifiers: start with letter or underscore, followed by letters, digits, underscores
ID=[a-zA-Z_][a-zA-Z0-9_]*

// Strings: single or double quoted with proper escaping
SINGLE_QUOTED_STRING='([^'\\]|\\.)*'
DOUBLE_QUOTED_STRING="([^\"\\]|\\.)*"
STRING={SINGLE_QUOTED_STRING}|{DOUBLE_QUOTED_STRING}

// Numbers
INTEGER=\d+

// Punctuation
LBRACE=\{
RBRACE=\}
DOT=\.

%%

<YYINITIAL> {TASK_KEYWORD}                                  { return TJTypes.TASK; }
<YYINITIAL> {DEPENDS_KEYWORD}                               { return TJTypes.DEPENDS; }
<YYINITIAL> {NOTE_KEYWORD}                                  { return TJTypes.NOTE; }

<YYINITIAL> {STRING}                                        { return TJTypes.STRING; }
<YYINITIAL> {INTEGER}                                       { return TJTypes.INTEGER; }
<YYINITIAL> {ID}                                            { return TJTypes.ID; }

<YYINITIAL> {DOT}                                           { return TJTypes.DOT; }

<YYINITIAL> {WHITE_SPACE}+                                  { return TokenType.WHITE_SPACE; }

<YYINITIAL> {CRLF}                                          { return TokenType.WHITE_SPACE; }

[^]                                                         { return TokenType.BAD_CHARACTER; }