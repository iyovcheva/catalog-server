package org.brooklyncentral.catalog.rest.server;

import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.brooklyn.util.collections.MutableMap;
import org.brooklyncentral.catalog.model.Repository;
import org.brooklyncentral.catalog.model.RepositoryLink;
import org.brooklyncentral.catalog.model.RepositoryLinkList;

public class CatalogServerState {

    RepositoryLinkList repos;
    Map<String,Repository> cache = MutableMap.of();
    
    private CatalogServerState() {}
    
    public static CatalogServerState newInstance(RepositoryLinkList repos) {
        CatalogServerState result = new CatalogServerState();
        result.repos = repos;
        return result;
    }
    
    public static CatalogServerState getInstance(ServletContext context) {
        CatalogServerState result = (CatalogServerState) context.getAttribute(CatalogServerState.class.getName());
        if (result!=null) return result;
        CatalogServerConfig config = CatalogServerConfig.retrieve(context);
        if (config==null) {
            throw new IllegalStateException(CatalogServerConfig.class.getName()+" not set on "+context);
        }
        synchronized (config) {
            result = newInstance(context, config);
            context.setAttribute(CatalogServerState.class.getName(), result);
        }
        return result;
    }

    private static CatalogServerState newInstance(ServletContext context, CatalogServerConfig config) {
        CatalogServerState result;
        result = (CatalogServerState) context.getAttribute(CatalogServerState.class.getName());
        if (result!=null) return result;
        result = newInstance(RepositoryLinkList.newInstance( config ));
        return result;
    }

    public RepositoryLinkList getRepositories() {
        return repos;
    }

    public Repository getRepository(String ownerName, String repoName) {
        String token = ownerName+"/"+repoName;
        Repository r = cache.get(token);
        if (r!=null) return r;
        
        for (Map<String,String> rm : repos.asList()) {
            RepositoryLink rl = RepositoryLink.newInstance(rm);
            if (rl.getOwner().equals(ownerName) && rl.getShortName().equals(repoName)) {
                r = loadAndAddToCache(rl);
                cache.put(token, r);
                return r;
            }
        }
        
        return null;
    }

    private Repository loadAndAddToCache(RepositoryLink rl) {
        // load it
        Repository r = Repository.newInstance(rl);
        // now cache it (unless someone else has also loaded it meanwhile)
        synchronized (cache) {
            Repository r0 = cache.get(rl.getOwnerSlashName());
            if (r0!=null) {
                // don't add to cache, someone else populated it at the same time;
                // ensure everyone sees same object (probably doesnt' matter, but slightly cleaner)
                return r0;
            }
            cache.put(r.getOwnerSlashName(), r);
            return r;
        }
    }
    
}
