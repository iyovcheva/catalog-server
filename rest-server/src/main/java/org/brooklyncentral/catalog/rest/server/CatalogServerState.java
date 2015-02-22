package org.brooklyncentral.catalog.rest.server;

import javax.servlet.ServletContext;

public class CatalogServerState {

    RepositoriesList repos;
    
    private CatalogServerState() {}
    
    public static CatalogServerState newInstance(RepositoriesList repos) {
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
        result = newInstance(RepositoriesList.newInstance( config ));
        return result;
    }

    public RepositoriesList getRepositories() {
        return repos;
    }
    
}
