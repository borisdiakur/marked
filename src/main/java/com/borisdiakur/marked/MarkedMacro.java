package com.borisdiakur.marked;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Map;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.html.HtmlRenderer.Builder;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;

public class MarkedMacro extends BaseMacro implements Macro {

    private String fetchPage(URL url) {
        try {
            // try opening the URL
            URLConnection urlConnection = url.openConnection();

            // basic auth
            if (url.getUserInfo() != null) {
                String basicAuth = "Basic "
                        + javax.xml.bind.DatatypeConverter.printBase64Binary(url.getUserInfo().getBytes());
                urlConnection.setRequestProperty("Authorization", basicAuth);
            }

            InputStream urlStream = urlConnection.getInputStream();
            byte buffer[] = new byte[1000];
            int numRead = urlStream.read(buffer);
            String content = new String(buffer, 0, numRead);

            int MAX_PAGE_SIZE = Integer.MAX_VALUE;
            while ((numRead != -1) && (content.length() < MAX_PAGE_SIZE)) {
                numRead = urlStream.read(buffer);
                if (numRead != -1) {
                    String newContent = new String(buffer, 0, numRead);
                    content += newContent;
                }
            }
            return content;
        } catch (IOException ioe) {
            return "Cannot read resource.\n".concat(ioe.getLocalizedMessage());
        } catch (IndexOutOfBoundsException iaobe) {
            return "Resource is too large.";
        }
    }

    @Override
    public BodyType getBodyType() {
        return BodyType.NONE;
    }

    @Override
    public OutputType getOutputType() {
        return OutputType.BLOCK;
    }

    @Override
    public String execute(Map<String, String> parameters, String bodyContent, ConversionContext conversionContext)
            throws MacroExecutionException {
        URL url;
        if (parameters.get("URL") == null) {
            return "";
        }
        try {
            url = new URL(parameters.get("URL"));
        } catch (MalformedURLException e) {
            return "Cannot find valid resource.";
        }

        String markdown = fetchPage(url);
        return convertToHtml(markdown, url);
    }

    String convertToHtml(String markdown) {
        return convertToHtml(markdown, null);
    }

    String convertToHtml(String markdown, URL currentURL) {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create(),
                ConfluenceCodeBlockExtension.create()));
        Parser parser = Parser.builder(options).build();
        Builder builder = HtmlRenderer.builder(options);
        if (currentURL != null) {
            builder.linkResolverFactory(new RelativeResolverFactory(currentURL));
        }
        HtmlRenderer renderer = builder.build();
        return renderer.render(parser.parse(markdown));
    }

    @Override
    public boolean hasBody() {
        return true;
    }

    @Override
    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    @Override
    public String execute(Map map, String s, RenderContext renderContext) throws MacroException {
        try {
            return execute(map, s, new DefaultConversionContext(renderContext));
        } catch (MacroExecutionException e) {
            throw new MacroException(e.getMessage(), e);
        }
    }
}