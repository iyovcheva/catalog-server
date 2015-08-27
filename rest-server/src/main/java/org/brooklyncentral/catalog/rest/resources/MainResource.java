package org.brooklyncentral.catalog.rest.resources;

import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.brooklyncentral.catalog.dto.CatalogItem;
import org.brooklyncentral.catalog.rest.api.MainApi;
import org.brooklyncentral.catalog.rest.server.CatalogServerState;

public class MainResource implements MainApi {

    @Context ServletContext servletContext;

    // TODO Support filtering by regex and fragment (currently parameters are ignored)
    @Override
    public List<CatalogItem> listCatalogItems(String regex, String fragment) {
        return CatalogServerState.getInstance(servletContext).getCatalogItems();
    }

    @Override
    public CatalogItem getCatalogItem(String ownerName, String repoName) {
        CatalogItem catalogItem = CatalogServerState.getInstance(servletContext).getCatalogItem(ownerName, repoName);
        if (catalogItem == null) {
            throw new WebApplicationException(Response.status(404).build());
        }

        return catalogItem;
    }

}
