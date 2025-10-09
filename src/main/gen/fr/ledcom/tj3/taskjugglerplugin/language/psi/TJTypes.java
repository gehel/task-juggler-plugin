// This is a generated file. Not intended for manual editing.
package fr.ledcom.tj3.taskjugglerplugin.language.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import fr.ledcom.tj3.taskjugglerplugin.language.psi.impl.*;

public interface TJTypes {

  IElementType ABSOLUTE_ID = new TJElementType("ABSOLUTE_ID");
  IElementType BASE_DATE = new TJElementType("BASE_DATE");
  IElementType CURRENCY = new TJElementType("CURRENCY");
  IElementType DATE = new TJElementType("DATE");
  IElementType DD = new TJElementType("DD");
  IElementType DEPENDS = new TJElementType("DEPENDS");
  IElementType DURATION = new TJElementType("DURATION");
  IElementType HH = new TJElementType("HH");
  IElementType INTERVAL_2 = new TJElementType("INTERVAL_2");
  IElementType M = new TJElementType("M");
  IElementType MM = new TJElementType("MM");
  IElementType NOTE = new TJElementType("NOTE");
  IElementType PROJECT = new TJElementType("PROJECT");
  IElementType PROJECT_ATTRIBUTES = new TJElementType("PROJECT_ATTRIBUTES");
  IElementType SS = new TJElementType("SS");
  IElementType TASK = new TJElementType("TASK");
  IElementType TASK_ATTRIBUTES = new TJElementType("TASK_ATTRIBUTES");
  IElementType TIMEZONE = new TJElementType("TIMEZONE");
  IElementType YYYY = new TJElementType("YYYY");

  IElementType DOT = new TJTokenType(".");
  IElementType ID = new TJTokenType("ID");
  IElementType INTEGER = new TJTokenType("INTEGER");
  IElementType LINE_COMMENT = new TJTokenType("line_comment");
  IElementType L_CURLY = new TJTokenType("{");
  IElementType R_CURLY = new TJTokenType("}");
  IElementType STRING = new TJTokenType("STRING");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ABSOLUTE_ID) {
        return new TJAbsoluteIdImpl(node);
      }
      else if (type == BASE_DATE) {
        return new TJBaseDateImpl(node);
      }
      else if (type == CURRENCY) {
        return new TJCurrencyImpl(node);
      }
      else if (type == DATE) {
        return new TJDateImpl(node);
      }
      else if (type == DD) {
        return new TJDdImpl(node);
      }
      else if (type == DEPENDS) {
        return new TJDependsImpl(node);
      }
      else if (type == DURATION) {
        return new TJDurationImpl(node);
      }
      else if (type == HH) {
        return new TJHhImpl(node);
      }
      else if (type == INTERVAL_2) {
        return new TJInterval2Impl(node);
      }
      else if (type == M) {
        return new TJMImpl(node);
      }
      else if (type == MM) {
        return new TJMmImpl(node);
      }
      else if (type == NOTE) {
        return new TJNoteImpl(node);
      }
      else if (type == PROJECT) {
        return new TJProjectImpl(node);
      }
      else if (type == PROJECT_ATTRIBUTES) {
        return new TJProjectAttributesImpl(node);
      }
      else if (type == SS) {
        return new TJSsImpl(node);
      }
      else if (type == TASK) {
        return new TJTaskImpl(node);
      }
      else if (type == TASK_ATTRIBUTES) {
        return new TJTaskAttributesImpl(node);
      }
      else if (type == TIMEZONE) {
        return new TJTimezoneImpl(node);
      }
      else if (type == YYYY) {
        return new TJYyyyImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
