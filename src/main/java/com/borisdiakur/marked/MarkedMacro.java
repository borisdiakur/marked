package com.borisdiakur.marked;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import org.pegdown.Parser;
import org.pegdown.PegDownProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class MarkedMacro extends BaseMacro implements Macro {

    private String getpage(URL url) {
        try {
            // try opening the URL
            URLConnection urlConnection = url.openConnection();
            urlConnection.setAllowUserInteraction(false);

            InputStream urlStream = url.openStream();
            byte buffer[] = new byte[1000];
            int numRead = urlStream.read(buffer);
            String content = new String(buffer, 0, numRead);

            int MAX_PAGE_SIZE = 9999999;
            while ((numRead != -1) && (content.length() < MAX_PAGE_SIZE)) {
                numRead = urlStream.read(buffer);
                if (numRead != -1) {
                    String newContent = new String(buffer, 0, numRead);
                    content += newContent;
                }
            }
            return content;
        } catch (IOException ioe) {
            return "Cannot read";
        } catch (IndexOutOfBoundsException iaobe) {
            return "Too large";
        }
    }

    public MarkedMacro() {}

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
        PegDownProcessor translator = new PegDownProcessor(Parser.ALL);
        URL url;
        try {
            url = new URL(parameters.get("url"));
        } catch (MalformedURLException e) {
            return "Cannot find";
        }
        return translator.markdownToHtml(getpage(url));
    }

    @Override
    public boolean hasBody() {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;  //To change body of implemented methods use File | Settings | File Templates.
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