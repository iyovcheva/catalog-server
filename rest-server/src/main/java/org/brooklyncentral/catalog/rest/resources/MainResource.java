package org.brooklyncentral.catalog.rest.resources;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.brooklyncentral.catalog.model.Repository;
import org.brooklyncentral.catalog.model.RepositoryLinkList;
import org.brooklyncentral.catalog.rest.api.MainApi;
import org.brooklyncentral.catalog.rest.server.CatalogServerState;

import brooklyn.util.collections.MutableMap;

public class MainResource implements MainApi {

    @Context ServletContext servletContext;
    
    @Override
    public List<Map<String,String>> listRepositories(String regex, String fragment) {
        // TODO support filtering by regex and fragment (currently parameters are ignored)
        RepositoryLinkList rl =
            CatalogServerState.getInstance(servletContext).getRepositories();
            //RepositoriesList.newInstance(CatalogServerConfig.retrieve(servletContext));
        return rl.asList();
    }
        
    @Override
    public Map<String,Object> getRepo(String ownerName, String repoName) {
        Repository repo = CatalogServerState.getInstance(servletContext).getRepository(ownerName, repoName);
        if (repo==null) {
            throw new WebApplicationException(Response.status(404).build());
        }
        MutableMap<String,Object> result = MutableMap.<String,Object>copyOf(repo.getLink().asMap());
        result.put("bomText", repo.getCatalogBomString());
        result.put("bomMap", repo.getCatalogBomYaml());
        return result;
    }
    
    @Override
    public String getRepoCatalog(String ownerName, String repoName) {
        Repository repo = CatalogServerState.getInstance(servletContext).getRepository(ownerName, repoName);
        if (repo==null) {
            throw new WebApplicationException(Response.status(404).build());
        }
        
//        url:null,
//        name:null,
//        owner:null

        return repo.getCatalogBomString();
    }

}
