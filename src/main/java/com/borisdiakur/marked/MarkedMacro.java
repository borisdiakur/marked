package com.borisdiakur.marked;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

public class MarkedMacro extends BaseMacro implements Macro {

    private String fetchPage(URL url) {
        try {
            // First set the default cookie manager.
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));

            // try opening the URL
            URLConnection urlConnection = url.openConnection();

            // basic/bearer auth
            if (url.getUserInfo() != null) {
                String userInfo = url.getUserInfo();
                String auth;
                if (userInfo.startsWith("x-token-auth:")) {
                    auth = "Bearer " + userInfo.substring(userInfo.indexOf(':') + 1);
                }
                else {
                    auth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userInfo.getBytes());
                }
                urlConnection.setRequestProperty("Authorization", auth);
            }

            InputStream urlStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(urlStream, StandardCharsets.UTF_8);
            BufferedReader bufReader = new BufferedReader(inputStreamReader);

            char buffer[] = new char[1000];
            int numRead = bufReader.read(buffer);
            String content = new String(buffer, 0, numRead);

            int MAX_PAGE_SIZE = Integer.MAX_VALUE;
            while ((numRead != -1) && (content.length() < MAX_PAGE_SIZE)) {
                numRead = bufReader.read(buffer);
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
    public String execute(Map<String, String> parameters, String bodyContent, ConversionContext conversionContext) throws MacroExecutionException {
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
        // builder.percentEncodeUrls(true);
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
