package org.brooklyncentral.catalog.rest.server;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.brooklyncentral.catalog.dto.CatalogItem;
import org.brooklyncentral.catalog.scrape.CatalogItemScraper;

import com.google.common.collect.ImmutableList;

public class CatalogServerState {

    private final Map<String, CatalogItem> catalogItems;

    private CatalogServerState(Map<String, CatalogItem> catalogItems) {
        this.catalogItems = catalogItems;
    }

    public static CatalogServerState newInstance(Map<String, CatalogItem> catalogItems) {
        return new CatalogServerState(catalogItems);
    }

    private static CatalogServerState newInstance(ServletContext context, CatalogServerConfig config) {
        CatalogServerState result = (CatalogServerState) context.getAttribute(CatalogServerState.class.getName());
        if (result!=null) return result;

        Map<String, CatalogItem> catalogItems = CatalogItemScraper.scrapeCatalogItems(config.getRepositoriesUrl());
        return newInstance(catalogItems);
    }

    public static CatalogServerState getInstance(ServletContext context) {
        CatalogServerState result = (CatalogServerState) context.getAttribute(CatalogServerState.class.getName());
        if (result!=null) return result;

        CatalogServerConfig config = CatalogServerConfig.retrieve(context);
        if (config==null) {
            throw new IllegalStateException(CatalogServerConfig.class.getName() + " not set on " + context);
        }

        synchronized (config) {
            result = newInstance(context, config);
            context.setAttribute(CatalogServerState.class.getName(), result);
        }

        return result;
    }

    public List<CatalogItem> getCatalogItems() {
        return ImmutableList.copyOf(catalogItems.values());
    }

    public CatalogItem getCatalogItem(String ownerName, String repoName) {
        String token = ownerName + "/" + repoName;
        return catalogItems.get(token);
    }

}
