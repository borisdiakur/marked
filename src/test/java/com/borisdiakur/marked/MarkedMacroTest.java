package com.borisdiakur.marked;

import static com.borisdiakur.marked.ConfluenceCodeBlockExtension.ConfluenceCodeBlockNodeRenderer.CONFLUENCE_CODE_BLOCK_HTML_CLOSE;
import static com.borisdiakur.marked.ConfluenceCodeBlockExtension.ConfluenceCodeBlockNodeRenderer.CONFLUENCE_CODE_BLOCK_HTML_OPEN_TEMPLATE;
import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

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
                "    int x = 5;\n" + "        int y = 10;\n";

        // when converting markdown to html
        String html = markedMacro.convertToHtml(markdown);

        // then code tag is wrapped in confluence blocks
        String expectedHtml = String.format(CONFLUENCE_CODE_BLOCK_HTML_OPEN_TEMPLATE, "java") + "System.out.println(\"foobar\");\n" +
                "int x = 5;\n" + "    int y = 10;\n" + CONFLUENCE_CODE_BLOCK_HTML_CLOSE + "\n";

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

    @Test
    public void convertsFencedCodeBlockToConfluenceCodeBlockWhenNoLanguageIsSpecified() {
        // given a markdown containing code fence
        String markdown = "```\n" +
                "System.out.println(\"foobar\")\n" +
                "int x = 5\n" +
                "```";

        // when converting markdown to html
        String html = markedMacro.convertToHtml(markdown);

        // then code tag is wrapped in confluence blocks
        String expectedHtml = String.format(CONFLUENCE_CODE_BLOCK_HTML_OPEN_TEMPLATE, "java") + "System.out.println(\"foobar\")\n" +
                "int x = 5\n" + CONFLUENCE_CODE_BLOCK_HTML_CLOSE + "\n";

        assertEquals(expectedHtml, html);
    }

    @Test
    public void doesNotStripIndentationInsideFencedCodeBlock() {
        // given markdown containing code fence with code indented with spaces
        String markdown = "\n" +
                "```java\n" +
                "package com.vladsch.flexmark.samples;\n" +
                "\n" +
                "import com.vladsch.flexmark.ast.Node;\n" +
                "import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;\n" +
                "import com.vladsch.flexmark.ext.tables.TablesExtension;\n" +
                "import com.vladsch.flexmark.html.HtmlRenderer;\n" +
                "import com.vladsch.flexmark.parser.Parser;\n" +
                "import com.vladsch.flexmark.util.options.MutableDataSet;\n" +
                "\n" +
                "import java.util.Arrays;\n" +
                "\n" +
                "public class BasicSample {\n" +
                "    public static void main(String[] args) {\n" +
                "        MutableDataSet options = new MutableDataSet();\n" +
                "\n" +
                "        // uncomment to set optional extensions\n" +
                "        //options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));\n" +
                "\n" +
                "        // uncomment to convert soft-breaks to hard breaks\n" +
                "        //options.set(HtmlRenderer.SOFT_BREAK, \"<br />\\n\");\n" +
                "\n" +
                "        Parser parser = Parser.builder(options).build();\n" +
                "        HtmlRenderer renderer = HtmlRenderer.builder(options).build();\n" +
                "\n" +
                "        // You can re-use parser and renderer instances\n" +
                "        Node document = parser.parse(\"This is *Sparta*\");\n" +
                "        String html = renderer.render(document);  // \"<p>This is <em>Sparta</em></p>\\n\"\n" +
                "        System.out.println(html);\n" +
                "    }\n" +
                "}\n" +
                "```";

        // when converting markdown to html
        String html = markedMacro.convertToHtml(markdown);

        // spaces are not stripped
        String expectedHtml = "<div class=\"code panel pdl conf-macro output-block\" data-hasbody=\"true\" data-macro-name=\"code\" style=\"border-width: 1px;\"><div class=\"codeContent panelContent pdl\"><pre class=\"syntaxhighlighter-pre\" data-syntaxhighlighter-params=\"brush: java; gutter: false; theme: Confluence\" data-theme=\"Confluence\">package com.vladsch.flexmark.samples;\n" +
                "\n" +
                "import com.vladsch.flexmark.ast.Node;\n" +
                "import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;\n" +
                "import com.vladsch.flexmark.ext.tables.TablesExtension;\n" +
                "import com.vladsch.flexmark.html.HtmlRenderer;\n" +
                "import com.vladsch.flexmark.parser.Parser;\n" +
                "import com.vladsch.flexmark.util.options.MutableDataSet;\n" +
                "\n" +
                "import java.util.Arrays;\n" +
                "\n" +
                "public class BasicSample {\n" +
                "    public static void main(String[] args) {\n" +
                "        MutableDataSet options = new MutableDataSet();\n" +
                "\n" +
                "        // uncomment to set optional extensions\n" +
                "        //options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));\n" +
                "\n" +
                "        // uncomment to convert soft-breaks to hard breaks\n" +
                "        //options.set(HtmlRenderer.SOFT_BREAK, \"<br />\\n\");\n" +
                "\n" +
                "        Parser parser = Parser.builder(options).build();\n" +
                "        HtmlRenderer renderer = HtmlRenderer.builder(options).build();\n" +
                "\n" +
                "        // You can re-use parser and renderer instances\n" +
                "        Node document = parser.parse(\"This is *Sparta*\");\n" +
                "        String html = renderer.render(document);  // \"<p>This is <em>Sparta</em></p>\\n\"\n" +
                "        System.out.println(html);\n" +
                "    }\n" +
                "}\n" +
                "</pre></div></div>\n";


        assertEquals(expectedHtml, html);
    }
    
    @Test
    public void convertsRelativeURL() {
        URL baseURL;
        try {
            baseURL = new URL("http://localhost/test/test2/index.md");

            // test a set of cases

            Map<String, String> tests = new HashMap<String, String>();
            tests.put("[A hyperlink](./hyperlink.md)",
                    "<p><a href=\"http://localhost/test/test2/hyperlink.md\">A hyperlink</a></p>\n");
            tests.put("[A hyperlink](./hyperlink.md?param=test)",
                    "<p><a href=\"http://localhost/test/test2/hyperlink.md?param=test\">A hyperlink</a></p>\n");
            tests.put("[A hyperlink](../hyperlink.md)",
                    "<p><a href=\"http://localhost/test/hyperlink.md\">A hyperlink</a></p>\n");
            tests.put("[A hyperlink](../../hyperlink.md)",
                    "<p><a href=\"http://localhost/hyperlink.md\">A hyperlink</a></p>\n");
            tests.put("[A hyperlink](./../hyperlink.md)",
                    "<p><a href=\"http://localhost/test/hyperlink.md\">A hyperlink</a></p>\n");
            tests.put("[A hyperlink](test/hyperlink.md)",
                    "<p><a href=\"http://localhost/test/test2/test/hyperlink.md\">A hyperlink</a></p>\n");
            tests.put("[A hyperlink](/root/hyperlink.md)",
                    "<p><a href=\"http://localhost/root/hyperlink.md\">A hyperlink</a></p>\n");
            tests.put("[A hyperlink](http://www.myHyperlink.com)",
                    "<p><a href=\"http://www.myHyperlink.com\">A hyperlink</a></p>\n");
            tests.put("![Logo](./images/logo.png)",
                    "<p><img src=\"http://localhost/test/test2/images/logo.png\" alt=\"Logo\" /></p>\n");
            tests.put("![Logo](../images/logo.png)",
                    "<p><img src=\"http://localhost/test/images/logo.png\" alt=\"Logo\" /></p>\n");
            tests.put("![Logo](../../images/logo.png)",
                    "<p><img src=\"http://localhost/images/logo.png\" alt=\"Logo\" /></p>\n");
            tests.put("![Logo](./../images/logo.png)",
                    "<p><img src=\"http://localhost/test/images/logo.png\" alt=\"Logo\" /></p>\n");
            tests.put("![Logo](/root/images/logo.png)",
                    "<p><img src=\"http://localhost/root/images/logo.png\" alt=\"Logo\" /></p>\n");
            tests.put("![Logo](http://www.myHyperlink.com/images.jpg)",
                    "<p><img src=\"http://www.myHyperlink.com/images.jpg\" alt=\"Logo\" /></p>\n");

            for (Entry<String, String> entry : tests.entrySet()) {
                String markdown = entry.getKey();
                String expectedHtml = entry.getValue();
                // when converting markdown to html
                String h = markedMacro.convertToHtml(markdown, baseURL);
                // then relative hyperlink url is transformed to correct url
                String errorMessage = String.format("ERROR, expected %s from \n\t%s\n\tresult is : %s", expectedHtml,
                        markdown, h);
                assertEquals(errorMessage, expectedHtml, h);
            }

            // test no regression is null
            String hNoRegression = markedMacro.convertToHtml("[A hyperlink](./hyperlink.md)");
            assertEquals("<p><a href=\"./hyperlink.md\">A hyperlink</a></p>\n", hNoRegression);

            // test if url is null
            String h = markedMacro.convertToHtml("[A hyperlink](./hyperlink.md)", null);
            assertEquals("<p><a href=\"./hyperlink.md\">A hyperlink</a></p>\n", h);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }


}