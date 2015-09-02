package org.brooklyncentral.catalog.rest.resources;

import org.brooklyncentral.catalog.ServerServletContextInitializer;
import org.brooklyncentral.catalog.dto.CatalogItem;
import org.brooklyncentral.catalog.dto.Repository;
import org.brooklyncentral.catalog.rest.api.MainApi;
import org.brooklyncentral.catalog.rest.server.Catalog;

import javax.servlet.ServletContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.List;

public class MainResource implements MainApi {

    @Context ServletContext servletContext;

    // TODO Support filtering by regex and fragment (currently parameters are ignored)
    @Override
    public List<Repository> listRepositories(String regex, String fragment) {
        Catalog catalog = (Catalog) servletContext.getAttribute(ServerServletContextInitializer.CATALOG);
        if (catalog == null) {
            throw new WebApplicationException(Response.status(404).build());
        }

        return catalog.getRepositories();
    }

    @Override
    public CatalogItem getCatalogItem(String ownerName, String repoName) {
        Catalog catalog = (Catalog) servletContext.getAttribute(ServerServletContextInitializer.CATALOG);
        if (catalog == null) {
            throw new WebApplicationException(Response.status(404).build());
        }

        return catalog.getCatalogItem(ownerName, repoName);
    }

}
