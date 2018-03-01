package com.borisdiakur.marked;

import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ast.IndentedCodeBlock;
import com.vladsch.flexmark.html.CustomNodeRenderer;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.HtmlWriter;
import com.vladsch.flexmark.html.renderer.NodeRenderer;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.NodeRendererFactory;
import com.vladsch.flexmark.html.renderer.NodeRenderingHandler;
import com.vladsch.flexmark.util.options.DataHolder;
import com.vladsch.flexmark.util.options.MutableDataHolder;
import com.vladsch.flexmark.util.sequence.BasedSequence;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ConfluenceCodeBlockExtension implements HtmlRenderer.HtmlRendererExtension {

    static Extension create() {
        return new ConfluenceCodeBlockExtension();
    }

    @Override
    public void rendererOptions(MutableDataHolder mutableDataHolder) {

    }

    @Override
    public void extend(HtmlRenderer.Builder rendererBuilder, String rendererType) {
        rendererBuilder.nodeRendererFactory(new Factory());
    }

    public static class Factory implements NodeRendererFactory {

        @Override
        public NodeRenderer create(DataHolder options) {
            return new ConfluenceCodeBlockNodeRenderer(options);
        }
    }

    public static class ConfluenceCodeBlockNodeRenderer implements NodeRenderer {

        static final String CONFLUENCE_CODE_BLOCK_HTML_OPEN_TEMPLATE = "<div class=\"code panel pdl conf-macro output-block\" data-hasbody=\"true\" data-macro-name=\"code\" style=\"border-width: 1px;\">" +
                "<div class=\"codeContent panelContent pdl\">" +
                "<pre class=\"syntaxhighlighter-pre\" data-syntaxhighlighter-params=\"brush: %s; gutter: false; theme: Confluence\" data-theme=\"Confluence\">";
        static final String CONFLUENCE_CODE_BLOCK_HTML_CLOSE = "</pre></div></div>";

        ConfluenceCodeBlockNodeRenderer(DataHolder options) {

        }

        @Override
        public Set<NodeRenderingHandler<?>> getNodeRenderingHandlers() {
            HashSet<NodeRenderingHandler<?>> handlers = new HashSet<NodeRenderingHandler<?>>();
            NodeRenderingHandler<FencedCodeBlock> fencedCodeBlockHandler = new NodeRenderingHandler<FencedCodeBlock>(FencedCodeBlock.class, new CustomNodeRenderer<FencedCodeBlock>() {
                @Override
                public void render(FencedCodeBlock node, NodeRendererContext context, HtmlWriter htmlWriter) {
                    ConfluenceCodeBlockNodeRenderer.this.renderFencedCodeBlock(node, context, htmlWriter);
                }
            });

            NodeRenderingHandler<IndentedCodeBlock> indentedCodeBlockHandler = new NodeRenderingHandler<IndentedCodeBlock>(IndentedCodeBlock.class, new CustomNodeRenderer<IndentedCodeBlock>() {
                @Override
                public void render(IndentedCodeBlock node, NodeRendererContext context, HtmlWriter htmlWriter) {
                    ConfluenceCodeBlockNodeRenderer.this.renderIndentedCodeBlock(node, context, htmlWriter);
                }
            });

            handlers.add(fencedCodeBlockHandler);
            handlers.add(indentedCodeBlockHandler);

            return handlers;
        }

        private void renderFencedCodeBlock(FencedCodeBlock fencedCodeBlock, NodeRendererContext context, HtmlWriter htmlWriter) {
            String language = fencedCodeBlock.getInfo().toString();
            String[] supportedLanguages = new String[] {
                "actionsscript3",
                "applescript",
                "bash",
                "c#",
                "cpp",
                "css",
                "coldfusion",
                "delphi",
                "diff",
                "erl",
                "groovy",
                "xml",
                "java",
                "jfx",
                "js",
                "php",
                "perl",
                "text",
                "powershell",
                "py",
                "ruby",
                "sql",
                "sass",
                "scala",
                "vb"
            };

            if (language.isEmpty() || !Arrays.asList(supportedLanguages).contains(language)) {
                // confluence defaults to java
                language = "java";
            }
            String code = fencedCodeBlock.getChildChars().toString();

            write(code, language, htmlWriter);
        }

        private void renderIndentedCodeBlock(IndentedCodeBlock indentedCodeBlock, NodeRendererContext context, HtmlWriter htmlWriter) {

            StringBuilder builder = new StringBuilder();
            for (BasedSequence line : indentedCodeBlock.getContentLines()) {
                builder.append(line.toString());
            }

            String code = builder.toString();

            // confluence defaults to java
            write(code, "java", htmlWriter);
        }

        private void write(String code, String language, HtmlWriter htmlWriter) {
            String htmlOpen = String.format(CONFLUENCE_CODE_BLOCK_HTML_OPEN_TEMPLATE, language);

            htmlWriter.raw(htmlOpen);
            htmlWriter.openPre();
            htmlWriter.raw(code);
            htmlWriter.closePre();
            htmlWriter.raw(CONFLUENCE_CODE_BLOCK_HTML_CLOSE);
        }
    }
}
