package fr.ledcom.tj3.taskjugglerplugin.language.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes;

%%

%public
%class TaskJugglerLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF=\R
WHITE_SPACE=[\ \n\t\f]

END_OF_LINE_COMMENT="#"[^\r\n]*

// Keywords
TASK_KEYWORD="task"
START_KEYWORD="start"
END_KEYWORD="end"
DEPENDS_KEYWORD="depends"
DURATION_KEYWORD="duration"
NOTE_KEYWORD="note"
RESPONSIBLE_KEYWORD="responsible"

// Identifiers: start with letter or underscore, followed by letters, digits, underscores
ID=[a-zA-Z_][a-zA-Z0-9_]*

SIMPLE_QUOTE_STRING=\'[^\']*\'
DOUBLE_QUOTE_STRING=\"[^\"]*\"

// Numbers
INTEGER=\d+

DATE=\d{4}-\d{2}-\d{2}
DURATION_UNIT=(min | h | d | w | m | y)

// Punctuation
L_CURLY=\{
R_CURLY=\}
DOT=\.
BANG=\!
COMA=\,

%%

<YYINITIAL> {END_OF_LINE_COMMENT}                           { return TJTypes.COMMENT; }

<YYINITIAL> {DEPENDS_KEYWORD}                               { return TJTypes.DEPENDS_KEYWORD; }
<YYINITIAL> {DURATION_KEYWORD}                              { return TJTypes.DURATION_KEYWORD; }
<YYINITIAL> {NOTE_KEYWORD}                                  { return TJTypes.NOTE_KEYWORD; }
<YYINITIAL> {RESPONSIBLE_KEYWORD}                           { return TJTypes.RESPONSIBLE_KEYWORD; }
<YYINITIAL> {START_KEYWORD}                                 { return TJTypes.START_KEYWORD; }
<YYINITIAL> {END_KEYWORD}                                   { return TJTypes.END_KEYWORD; }
<YYINITIAL> {TASK_KEYWORD}                                  { return TJTypes.TASK_KEYWORD; }

<YYINITIAL> {L_CURLY}                                       { return TJTypes.L_CURLY; }
<YYINITIAL> {R_CURLY}                                       { return TJTypes.R_CURLY; }

<YYINITIAL> {DATE}                                          { return TJTypes.DATE; }
<YYINITIAL> {INTEGER}                                       { return TJTypes.INTEGER; }
<YYINITIAL> {DURATION_UNIT}                                 { return TJTypes.DURATION_UNIT; }


<YYINITIAL> {ID}                                            { return TJTypes.ID; }
<YYINITIAL> {DOT}                                           { return TJTypes.DOT; }
<YYINITIAL> {BANG}                                          { return TJTypes.BANG; }
<YYINITIAL> {COMA}                                          { return TJTypes.COMA; }

<YYINITIAL> {SIMPLE_QUOTE_STRING}                           { return TJTypes.STRING; }
<YYINITIAL> {DOUBLE_QUOTE_STRING}                           { return TJTypes.STRING; }
<YYINITIAL> {CRLF}                                          { return TokenType.WHITE_SPACE; }
<YYINITIAL> {WHITE_SPACE}+                                  { return TokenType.WHITE_SPACE; }

[^]                                                         { return TokenType.BAD_CHARACTER; }
