package com.borisdiakur.marked;

import org.junit.Before;
import org.junit.Test;

import static com.borisdiakur.marked.ConfluenceCodeBlockExtension.ConfluenceCodeBlockNodeRenderer.CONFLUENCE_CODE_BLOCK_HTML_CLOSE;
import static com.borisdiakur.marked.ConfluenceCodeBlockExtension.ConfluenceCodeBlockNodeRenderer.CONFLUENCE_CODE_BLOCK_HTML_OPEN_TEMPLATE;
import static org.junit.Assert.*;

public class MarkedMacroTest {

    private MarkedMacro markedMacro;

    @Before
    public void setUp() {
        markedMacro = new MarkedMacro();
    }

    @Test
    public void convertsIndentedCodeBlockToConfluenceCodeBlock() {
        // given a markdown containing indented code
        String markdown = "    System.out.println(\"foobar\");\n" +
                "    int x = 5;";

        // when converting markdown to html
        String html = markedMacro.convertToHtml(markdown);

        // then code tag is wrapped in confluence blocks
        String expectedHtml = String.format(CONFLUENCE_CODE_BLOCK_HTML_OPEN_TEMPLATE, "java") + "System.out.println(\"foobar\");\n" +
                "int x = 5;" + CONFLUENCE_CODE_BLOCK_HTML_CLOSE + "\n";

        assertEquals(expectedHtml, html);
    }

    @Test
    public void convertsFencedCodeBlockToConfluenceCodeBlock() {
        // given a markdown containing code fence
        String markdown = "```groovy\n" +
                "System.out.println(\"foobar\")\n" +
                "int x = 5\n" +
                "```";

        // when converting markdown to html
        String html = markedMacro.convertToHtml(markdown);

        // then code tag is wrapped in confluence blocks
        String expectedHtml = String.format(CONFLUENCE_CODE_BLOCK_HTML_OPEN_TEMPLATE, "groovy") + "System.out.println(\"foobar\")\n" +
                "int x = 5\n" + CONFLUENCE_CODE_BLOCK_HTML_CLOSE + "\n";

        assertEquals(expectedHtml, html);
    }

}