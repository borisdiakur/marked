package com.borisdiakur.marked;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.LinkResolver;
import com.vladsch.flexmark.html.LinkResolverFactory;
import com.vladsch.flexmark.html.renderer.NodeRendererContext;
import com.vladsch.flexmark.html.renderer.ResolvedLink;

/**
 * Resolve url contained in markdown to base url of the confluence page
 *
 */
public class RelativeResolverFactory implements LinkResolverFactory {

    private URL currentURL;

    public RelativeResolverFactory(URL currentURL) {
        this.currentURL = currentURL;
    }

    @Override
    public boolean affectsGlobalScope() {
        return false;
    }

    @Override
    public LinkResolver create(NodeRendererContext arg0) {
        return new RelativeLinkResolver();
    }

    @Override
    public Set<Class<? extends LinkResolverFactory>> getAfterDependents() {
        return null;
    }

    @Override
    public Set<Class<? extends LinkResolverFactory>> getBeforeDependents() {
        return null;
    }

    private class RelativeLinkResolver implements LinkResolver {

        @Override
        public ResolvedLink resolveLink(Node node, NodeRendererContext context, ResolvedLink originalLink) {
            try {
                if (currentURL != null) {
                    URI resolved = currentURL.toURI().resolve(originalLink.getUrl());
                    return new ResolvedLink(originalLink.getLinkType(), resolved.toString());
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return originalLink;
        }

    }

}
