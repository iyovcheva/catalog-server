package org.brooklyncentral.catalog.model;

import java.util.Map;

import javax.annotation.Nullable;

import org.apache.brooklyn.util.yaml.Yamls;

public class CatalogItem {

    private final String repoUrl;
    private final String repoName;
    private final String author;
    private final String token;

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

        this.repoUrl = repoUrl;
        this.repoName = repoName;
        this.author = author;
        this.token = author + "/" + repoName;

        this.description = description;
        this.documentation = documentation;

        this.catalogBomString = catalogBomString;
        this.catalogBomYaml = (Map<String, Object>) Yamls.parseAll(catalogBomString).iterator().next();

        this.masterCommitHash = masterCommitHash;
        this.license = license;
        this.changelog = changelog;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public String getRepoName() {
        return repoName;
    }

    public String getAuthor() {
        return author;
    }

    public String getToken() {
        return token;
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
