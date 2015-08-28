package org.brooklyncentral.catalog.dto;

public class Repository {

    private final String repoUrl;
    private final String repoName;
    private final String author;
    private final String token;

    public Repository(String repoUrl, String repoName, String author) {
        this.repoUrl = repoUrl;
        this.repoName = repoName;
        this.author = author;
        this.token = author + "/" + repoName;
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
}
