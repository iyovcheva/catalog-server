package org.brooklyncentral.catalog.model;

import java.util.Map;

import javax.annotation.Nullable;

import org.apache.brooklyn.util.yaml.Yamls;

import com.google.common.base.Optional;

public class CatalogItem {

    private final String repoUrl;
    private final String repoName;
    private final String author;
    private final String token;

    private final String description;
    private final String documentation;

    private final String catalogBomString;
    private final Map<String, Object> catalogBomYaml;

    private final Optional<String> masterCommitHash;
    private final Optional<String> license;
    private final Optional<String> changelog;

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

        this.masterCommitHash = Optional.of(masterCommitHash);
        this.license = Optional.of(license);
        this.changelog = Optional.of(changelog);
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

    public Optional<String> getMasterCommitHash() {
        return masterCommitHash;
    }

    public Optional<String> getLicense() {
        return license;
    }

    public Optional<String> getChangelog() {
        return changelog;
    }
}
