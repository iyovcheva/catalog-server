package org.brooklyncentral.catalog.dto;

import java.util.Map;

import javax.annotation.Nullable;

import org.apache.brooklyn.util.yaml.Yamls;

public class CatalogItem {

    private final Repository repository;

    private final String description;
    private final String documentation;

    private final String catalogBomString;
    private final Map<String, Object> catalogBomYaml;

    private final String masterCommitHash;
    private final String license;
    private final String changelog;

    @SuppressWarnings("unchecked")
    public CatalogItem(String repoUrl, String repoName, String author, String description,
            String documentation, String catalogBomString, Map<String, Object> catalogBomYaml,
            @Nullable String masterCommitHash, @Nullable String license, @Nullable String changelog) {

        this.repository = new Repository(repoUrl, repoName, author);

        this.description = description;
        this.documentation = documentation;

        this.catalogBomString = catalogBomString;
        this.catalogBomYaml = (Map<String, Object>) Yamls.parseAll(catalogBomString).iterator().next();

        this.masterCommitHash = masterCommitHash;
        this.license = license;
        this.changelog = changelog;
    }

    public Repository getRepository() {
        return repository;
    }

    public String getToken() {
        return getRepository().getToken();
    }

    public String getDescription() {
        return description;
    }

    public String getDocumentation() {
        return documentation;
    }

    public String getCatalogBomString() {
        return catalogBomString;
    }

    public Map<String, Object> getCatalogBomYaml() {
        return catalogBomYaml;
    }

    public String getMasterCommitHash() {
        return masterCommitHash;
    }

    public String getLicense() {
        return license;
    }

    public String getChangelog() {
        return changelog;
    }
}
