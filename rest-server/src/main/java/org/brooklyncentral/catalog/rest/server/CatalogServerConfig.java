package org.brooklyncentral.catalog.rest.server;

import org.eclipse.jetty.servlet.ServletContextHandler;

public class CatalogServerConfig {

    public static String DEFAULT_REPOSITORIES_URL = "https://github.com/brooklyncentral/brooklyn-community-catalog";

    private String repositoriesUrl;

    public static CatalogServerConfig newDefault() {
        CatalogServerConfig result = new CatalogServerConfig();
        result.setRepositoriesUrl(DEFAULT_REPOSITORIES_URL);
        return result;
    }

    public void setRepositoriesUrl(String repositoriesUrl) {
        this.repositoriesUrl = repositoriesUrl;
    }

    public String getRepositoriesUrl() {
        return repositoriesUrl;
    }

    public static void apply(ServletContextHandler context, CatalogServerConfig serverConfig) {
        context.setAttribute(CatalogServerConfig.class.getName(), serverConfig);
    }

}
