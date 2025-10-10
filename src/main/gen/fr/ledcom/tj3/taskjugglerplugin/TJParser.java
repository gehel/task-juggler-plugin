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
  // BASE_DATE | '%' L_CURLY BASE_DATE ('+'|'-') DURATION R_CURLY
  public static boolean DATE(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DATE")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DATE, "<date>");
    r = consumeToken(b, BASE_DATE);
    if (!r) r = DATE_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '%' L_CURLY BASE_DATE ('+'|'-') DURATION R_CURLY
  private static boolean DATE_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "DATE_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "%");
    r = r && consumeTokens(b, 0, L_CURLY, BASE_DATE);
    r = r && DATE_1_3(b, l + 1);
    r = r && consumeTokens(b, 0, DURATION, R_CURLY);
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
  // DATE ('-' DATE | '+' DURATION)
  public static boolean INTERVAL2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "INTERVAL2")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, INTERVAL_2, "<interval 2>");
    r = DATE(b, l + 1);
    r = r && INTERVAL2_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '-' DATE | '+' DURATION
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

  // '+' DURATION
  private static boolean INTERVAL2_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "INTERVAL2_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "+");
    r = r && consumeToken(b, DURATION);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'project' [ID] STRING [STRING] INTERVAL2 [L_CURLY project_attributes R_CURLY]
  public static boolean PROJECT(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PROJECT")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROJECT, "<project>");
    r = consumeToken(b, "project");
    r = r && PROJECT_1(b, l + 1);
    r = r && consumeToken(b, STRING);
    r = r && PROJECT_3(b, l + 1);
    r = r && INTERVAL2(b, l + 1);
    r = r && PROJECT_5(b, l + 1);
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

  // [L_CURLY project_attributes R_CURLY]
  private static boolean PROJECT_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PROJECT_5")) return false;
    PROJECT_5_0(b, l + 1);
    return true;
  }

  // L_CURLY project_attributes R_CURLY
  private static boolean PROJECT_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "PROJECT_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_CURLY);
    r = r && project_attributes(b, l + 1);
    r = r && consumeToken(b, R_CURLY);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'task' [ID] STRING [L_CURLY task_attributes R_CURLY]
  public static boolean TASK(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TASK")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TASK, "<task>");
    r = consumeToken(b, "task");
    r = r && TASK_1(b, l + 1);
    r = r && consumeToken(b, STRING);
    r = r && TASK_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // [ID]
  private static boolean TASK_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TASK_1")) return false;
    consumeToken(b, ID);
    return true;
  }

  // [L_CURLY task_attributes R_CURLY]
  private static boolean TASK_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TASK_3")) return false;
    TASK_3_0(b, l + 1);
    return true;
  }

  // L_CURLY task_attributes R_CURLY
  private static boolean TASK_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "TASK_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_CURLY);
    r = r && task_attributes(b, l + 1);
    r = r && consumeToken(b, R_CURLY);
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
  // 'depends' (ID|ABSOLUTE_ID)
  public static boolean depends(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "depends")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DEPENDS, "<depends>");
    r = consumeToken(b, "depends");
    r = r && depends_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ID|ABSOLUTE_ID
  private static boolean depends_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "depends_1")) return false;
    boolean r;
    r = consumeToken(b, ID);
    if (!r) r = ABSOLUTE_ID(b, l + 1);
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
  // (currency|timezone)*
  static boolean project_attributes(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "project_attributes")) return false;
    while (true) {
      int c = current_position_(b);
      if (!project_attributes_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "project_attributes", c)) break;
    }
    return true;
  }

  // currency|timezone
  private static boolean project_attributes_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "project_attributes_0")) return false;
    boolean r;
    r = currency(b, l + 1);
    if (!r) r = timezone(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // (PROJECT|TASK)*
  static boolean taskJugglerFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "taskJugglerFile")) return false;
    while (true) {
      int c = current_position_(b);
      if (!taskJugglerFile_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "taskJugglerFile", c)) break;
    }
    return true;
  }

  // PROJECT|TASK
  private static boolean taskJugglerFile_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "taskJugglerFile_0")) return false;
    boolean r;
    r = PROJECT(b, l + 1);
    if (!r) r = TASK(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // (depends|note)*
  static boolean task_attributes(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "task_attributes")) return false;
    while (true) {
      int c = current_position_(b);
      if (!task_attributes_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "task_attributes", c)) break;
    }
    return true;
  }

  // depends|note
  private static boolean task_attributes_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "task_attributes_0")) return false;
    boolean r;
    r = depends(b, l + 1);
    if (!r) r = note(b, l + 1);
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
