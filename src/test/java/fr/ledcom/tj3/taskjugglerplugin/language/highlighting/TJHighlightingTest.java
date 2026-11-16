package fr.ledcom.tj3.taskjugglerplugin.language.highlighting;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class TJHighlightingTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/highlighting";
    }

    public void testSemanticHighlighting() {
        myFixture.configureByText("test.tjp", """
                <info textAttributesKey="TJ_PROPERTY_KEYWORD">task</info> <info textAttributesKey="TJ_DECLARATION">start</info> "Start" {
                  depends !<info textAttributesKey="TJ_REFERENCE">other</info>
                  scheduling <info textAttributesKey="TJ_VALUE">asap</info>
                  <info textAttributesKey="TJ_CUSTOM_ATTRIBUTE">Phone</info> "x"
                  ${<info textAttributesKey="TJ_MACRO_CALL">m</info>}
                }
                <info textAttributesKey="TJ_PROPERTY_KEYWORD">taskreport</info> <info textAttributesKey="TJ_DECLARATION">r</info> "" {
                  hidetask ~<info textAttributesKey="TJ_FUNCTION">isleaf</info>()
                }
                """);
        myFixture.checkHighlighting(false, true, false, true);
    }
}
