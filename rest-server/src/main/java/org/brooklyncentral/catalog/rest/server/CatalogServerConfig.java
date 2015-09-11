package org.brooklyncentral.catalog.rest.server;

public class CatalogServerConfig {

    public static final String DEFAULT_REPOSITORIES_URL = "https://github.com/brooklyncentral/brooklyn-community-catalog";

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
}
