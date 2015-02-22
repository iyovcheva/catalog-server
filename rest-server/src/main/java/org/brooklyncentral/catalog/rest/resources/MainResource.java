package org.brooklyncentral.catalog.rest.resources;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.brooklyncentral.catalog.rest.api.MainApi;
import org.brooklyncentral.catalog.rest.server.CatalogServerState;
import org.brooklyncentral.catalog.rest.server.RepositoriesList;

public class MainResource implements MainApi {

    @Context ServletContext servletContext;
    
    @Override
    public List<Map<String,String>> listRepositories(String regex, String fragment) {
        // TODO support filtering by regex and fragment (currently parameters are ignored)
        RepositoriesList rl =
            CatalogServerState.getInstance(servletContext).getRepositories();
            //RepositoriesList.newInstance(CatalogServerConfig.retrieve(servletContext));
        return rl.asList();
    }

}
