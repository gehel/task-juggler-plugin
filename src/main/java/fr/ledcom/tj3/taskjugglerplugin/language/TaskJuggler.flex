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

END_OF_LINE_COMMENT="#"[^\r\n]*

// Keywords
TASK_KEYWORD="task"
DEPENDS_KEYWORD="depends"

// Identifiers: start with letter or underscore, followed by letters, digits, underscores
ID=[a-zA-Z_][a-zA-Z0-9_]*

STRING=\"[^\"]*\"

// Numbers
//INTEGER=\d+

DATE=\d{4}-\d{2}-\d{2}

// Punctuation
L_CURLY=\{
R_CURLY=\}
DOT=\.

%%

<YYINITIAL> {END_OF_LINE_COMMENT}                           { return TJTypes.COMMENT; }
<YYINITIAL> {TASK_KEYWORD}                                  { return TJTypes.TASK_KEYWORD; }
<YYINITIAL> {DEPENDS_KEYWORD}                               { return TJTypes.DEPENDS_KEYWORD; }

<YYINITIAL> {L_CURLY}                                       { return TJTypes.L_CURLY; }
<YYINITIAL> {R_CURLY}                                       { return TJTypes.R_CURLY; }

<YYINITIAL> {ID}                                            { return TJTypes.ID; }
<YYINITIAL> {DOT}                                           { return TJTypes.DOT; }

<YYINITIAL> {STRING}                                        { return TJTypes.STRING; }
<YYINITIAL> {WHITE_SPACE}+                                  { return TokenType.WHITE_SPACE; }

[^]                                                         { return TokenType.BAD_CHARACTER; }
