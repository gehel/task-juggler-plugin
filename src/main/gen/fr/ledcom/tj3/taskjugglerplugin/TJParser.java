// This is a generated file. Not intended for manual editing.
package fr.ledcom.tj3.taskjugglerplugin;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static fr.ledcom.tj3.taskjugglerplugin.language.psi.TJTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class TJParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return taskJugglerFile(b, l + 1);
  }

  /* ********************************************************** */
  // ID{DOT ID}*
  public static boolean ABSOLUTE_ID(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ABSOLUTE_ID")) return false;
    if (!nextTokenIs(b, ID)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ID);
    r = r && ABSOLUTE_ID_1(b, l + 1);
    exit_section_(b, m, ABSOLUTE_ID, r);
    return r;
  }

  // {DOT ID}*
  private static boolean ABSOLUTE_ID_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ABSOLUTE_ID_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!ABSOLUTE_ID_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ABSOLUTE_ID_1", c)) break;
    }
    return true;
  }

  // DOT ID
  private static boolean ABSOLUTE_ID_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ABSOLUTE_ID_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, DOT, ID);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // BASE_DATE | '%' L_CURLY BASE_DATE ('+'|'-') DURATION_ R_CURLY
  public static boolean DATE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DATE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DATE, "<date>");
    r = consumeToken(b, BASE_DATE);
    if (!r) r = DATE_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '%' L_CURLY BASE_DATE ('+'|'-') DURATION_ R_CURLY
  private static boolean DATE_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DATE_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "%");
    r = r && consumeTokens(b, 0, L_CURLY, BASE_DATE);
    r = r && DATE_1_3(b, l + 1);
    r = r && consumeTokens(b, 0, DURATION_, R_CURLY);
    exit_section_(b, m, null, r);
    return r;
  }

  // '+'|'-'
  private static boolean DATE_1_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DATE_1_3")) return false;
    boolean r;
    r = consumeToken(b, "+");
    if (!r) r = consumeToken(b, "-");
    return r;
  }

  /* ********************************************************** */
  // DATE ('-' DATE | '+' DURATION_)
  public static boolean INTERVAL2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "INTERVAL2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INTERVAL_2, "<interval 2>");
    r = DATE(b, l + 1);
    r = r && INTERVAL2_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '-' DATE | '+' DURATION_
  private static boolean INTERVAL2_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "INTERVAL2_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = INTERVAL2_1_0(b, l + 1);
    if (!r) r = INTERVAL2_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '-' DATE
  private static boolean INTERVAL2_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "INTERVAL2_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "-");
    r = r && DATE(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '+' DURATION_
  private static boolean INTERVAL2_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "INTERVAL2_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "+");
    r = r && consumeToken(b, DURATION_);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'project' [ID] STRING [STRING] INTERVAL2 L_CURLY (project_attributes_)* R_CURLY
  public static boolean PROJECT(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PROJECT")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROJECT, "<project>");
    r = consumeToken(b, "project");
    r = r && PROJECT_1(b, l + 1);
    r = r && consumeToken(b, STRING);
    r = r && PROJECT_3(b, l + 1);
    r = r && INTERVAL2(b, l + 1);
    r = r && consumeToken(b, L_CURLY);
    r = r && PROJECT_6(b, l + 1);
    r = r && consumeToken(b, R_CURLY);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // [ID]
  private static boolean PROJECT_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PROJECT_1")) return false;
    consumeToken(b, ID);
    return true;
  }

  // [STRING]
  private static boolean PROJECT_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PROJECT_3")) return false;
    consumeToken(b, STRING);
    return true;
  }

  // (project_attributes_)*
  private static boolean PROJECT_6(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PROJECT_6")) return false;
    while (true) {
      int c = current_position_(b);
      if (!PROJECT_6_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "PROJECT_6", c)) break;
    }
    return true;
  }

  // (project_attributes_)
  private static boolean PROJECT_6_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PROJECT_6_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = project_attributes_(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // REL_ID_MARK ID{DOT ID}*
  public static boolean RELATIVE_ID(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "RELATIVE_ID")) return false;
    if (!nextTokenIs(b, REL_ID_MARK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, REL_ID_MARK, ID);
    r = r && RELATIVE_ID_2(b, l + 1);
    exit_section_(b, m, RELATIVE_ID, r);
    return r;
  }

  // {DOT ID}*
  private static boolean RELATIVE_ID_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "RELATIVE_ID_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!RELATIVE_ID_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "RELATIVE_ID_2", c)) break;
    }
    return true;
  }

  // DOT ID
  private static boolean RELATIVE_ID_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "RELATIVE_ID_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, DOT, ID);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'task' [ID] STRING L_CURLY (task_attributes_)* R_CURLY
  public static boolean TASK(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TASK")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TASK, "<task>");
    r = consumeToken(b, "task");
    r = r && TASK_1(b, l + 1);
    r = r && consumeTokens(b, 0, STRING, L_CURLY);
    r = r && TASK_4(b, l + 1);
    r = r && consumeToken(b, R_CURLY);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // [ID]
  private static boolean TASK_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TASK_1")) return false;
    consumeToken(b, ID);
    return true;
  }

  // (task_attributes_)*
  private static boolean TASK_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TASK_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!TASK_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "TASK_4", c)) break;
    }
    return true;
  }

  // (task_attributes_)
  private static boolean TASK_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TASK_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = task_attributes_(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'currency' STRING
  public static boolean currency(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "currency")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CURRENCY, "<currency>");
    r = consumeToken(b, "currency");
    r = r && consumeToken(b, STRING);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'depends' (RELATIVE_ID|ABSOLUTE_ID)
  public static boolean depends(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "depends")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DEPENDS, "<depends>");
    r = consumeToken(b, "depends");
    r = r && depends_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // RELATIVE_ID|ABSOLUTE_ID
  private static boolean depends_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "depends_1")) return false;
    boolean r;
    r = RELATIVE_ID(b, l + 1);
    if (!r) r = ABSOLUTE_ID(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // 'duration' DURATION_
  public static boolean duration(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "duration")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DURATION, "<duration>");
    r = consumeToken(b, "duration");
    r = r && consumeToken(b, DURATION_);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'note' STRING
  public static boolean note(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "note")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NOTE, "<note>");
    r = consumeToken(b, "note");
    r = r && consumeToken(b, STRING);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // currency|timezone
  static boolean project_attributes_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "project_attributes_")) return false;
    boolean r;
    r = currency(b, l + 1);
    if (!r) r = timezone(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // 'responsible' ID
  public static boolean responsible(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "responsible")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, RESPONSIBLE, "<responsible>");
    r = consumeToken(b, "responsible");
    r = r && consumeToken(b, ID);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'start' DATE
  public static boolean start(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "start")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, START, "<start>");
    r = consumeToken(b, "start");
    r = r && DATE(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (TASK|PROJECT)*
  static boolean taskJugglerFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "taskJugglerFile")) return false;
    while (true) {
      int c = current_position_(b);
      if (!taskJugglerFile_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "taskJugglerFile", c)) break;
    }
    return true;
  }

  // TASK|PROJECT
  private static boolean taskJugglerFile_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "taskJugglerFile_0")) return false;
    boolean r;
    r = TASK(b, l + 1);
    if (!r) r = PROJECT(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // start|responsible|note|duration|depends|TASK
  static boolean task_attributes_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "task_attributes_")) return false;
    boolean r;
    r = start(b, l + 1);
    if (!r) r = responsible(b, l + 1);
    if (!r) r = note(b, l + 1);
    if (!r) r = duration(b, l + 1);
    if (!r) r = depends(b, l + 1);
    if (!r) r = TASK(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // 'timezone' STRING
  public static boolean timezone(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "timezone")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TIMEZONE, "<timezone>");
    r = consumeToken(b, "timezone");
    r = r && consumeToken(b, STRING);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

}
