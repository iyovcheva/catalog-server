package org.brooklyncentral.catalog.rest.resources;

import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.brooklyncentral.catalog.dto.CatalogItem;
import org.brooklyncentral.catalog.dto.Repository;
import org.brooklyncentral.catalog.rest.api.MainApi;
import org.brooklyncentral.catalog.rest.server.CatalogServerState;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class MainResource implements MainApi {

    @Context ServletContext servletContext;

    // TODO Support filtering by regex and fragment (currently parameters are ignored)
    @Override
    public List<Repository> listRepositories(String regex, String fragment) {
        List<CatalogItem> catalogItems = CatalogServerState.getInstance(servletContext).getCatalogItems();

        return Lists.transform(catalogItems, new Function<CatalogItem, Repository>() {
            @Override
            public Repository apply(CatalogItem input) {
                return input.getRepository();
            }
        });
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
