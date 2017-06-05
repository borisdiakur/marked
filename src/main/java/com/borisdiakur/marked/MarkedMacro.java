package com.borisdiakur.marked;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.google.gson.Gson;
import com.google.gson.internal.StringMap;
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class MarkedMacro extends BaseMacro implements Macro {

	private String fetchPage(URL url, String user, String password) {
		try {
			// try opening the URL
			URLConnection urlConnection = url.openConnection();

			// basic auth backward compatibility
			if (url.getUserInfo() != null) {
				String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(url.getUserInfo().getBytes());
				urlConnection.setRequestProperty("Authorization", basicAuth);
			}
			// basic auth
			if (user != null && password != null) {
				String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary((user + ":" + password).getBytes());
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
	public String execute(Map<String, String> parameters, String bodyContent, ConversionContext conversionContext) throws MacroExecutionException {
		URL urlObject = null;
		String url = parameters.get("URL");
		String markdown = null;
		String connectorType = parameters.get("ConnectorType");
		String httpBasicUser = parameters.get("HttpBasicUser");
		String httpBasicPassword = parameters.get("HttpBasicPassword");

		if (url == null) {
			return "";
		}

		try {
			urlObject = new URL(url);
		} catch (MalformedURLException e) {
			return "Cannot find valid resource.";
		}
		String content = fetchPage(urlObject, httpBasicUser, httpBasicPassword);

		if (connectorType.equals("BitbucketUrlApiReferenceMarkdown")) {
			markdown = this.parseBitbucketJson(content);
		} else {
			markdown = content;
		}
		return convertToHtml(markdown);
	}

	@SuppressWarnings("rawtypes")
	String parseBitbucketJson(String json) {
		StringBuilder sb = new StringBuilder();
		Map map = new Gson().fromJson(json, Map.class);
		ArrayList lines = (ArrayList) map.get("lines");
		for (Object line : lines) {
			StringMap m = (StringMap) line;
			sb.append(m.get("text"));
			sb.append("\n");
		}
		return sb.toString();
	}

	String convertToHtml(String markdown) {
		MutableDataSet options = new MutableDataSet();
		options.set(Parser.EXTENSIONS, Arrays.asList(TablesExtension.create(), StrikethroughExtension.create(), ConfluenceCodeBlockExtension.create()));
		Parser parser = Parser.builder(options).build();
		HtmlRenderer renderer = HtmlRenderer.builder(options).build();
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