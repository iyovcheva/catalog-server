package org.brooklyncentral.catalog.scrape;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.brooklyn.util.stream.Streams;
import org.apache.brooklyn.util.yaml.Yamls;
import org.brooklyncentral.catalog.model.CatalogItem;

import com.google.common.base.Optional;

public class CatalogItemScraper {

    public static Map<String, CatalogItem> scrapeBlueprints(String repoUrl) {
        List<String> catalogItemRepoUrls = parseDirectoryYaml(repoUrl);
        parseCatalogItem(catalogItemRepoUrls.iterator().next());

        return null;
    }

    @SuppressWarnings("unchecked")
    private static List<String> parseDirectoryYaml(String repoUrl) {
        Optional<String> directoryYamlString = getGithubRawText(repoUrl, "directory.yaml");

        if (!directoryYamlString.isPresent()) {
            throw new IllegalStateException("Failed to read catalog directory.");
        }

        return (List<String>) Yamls.parseAll(directoryYamlString.get()).iterator().next();
    }

    private static Optional<String> getGithubRawText(String repoUrl, String fileName) {
        String rawGithubUrl = generateRawGithubUrl(repoUrl, fileName);

        try (InputStream inputStream = new URL(rawGithubUrl).openStream()) {
            return Optional.of(Streams.readFullyString(inputStream));
        } catch (Exception e) {
            // TODO Log or something: String error =
            // "Unable to read raw Github URL " + rawGithubUrl + ": " + e,
            // e);
            return Optional.absent();
        }
    }

    private static String generateRawGithubUrl(String repoUrl, String fileName) {
        return repoUrl.replace("github.com", "raw.githubusercontent.com") + "/master/" + fileName;
    }

    private static CatalogItem parseCatalogItem(String repoUrl) {
        //TODO Properly handle required vs. optional failures

        String[] urlTokens = repoUrl.split("/");

        String repoName = urlTokens[urlTokens.length - 1];
        String author = urlTokens[urlTokens.length - 2];

        Optional<String> description = getGithubRawText(repoUrl, "README.md");
        Optional<String> documentation = getGithubRawText(repoUrl, "items.js");

        Optional<String> catalogBomString = getGithubRawText(repoUrl, "catalog.bom");
        @SuppressWarnings("unchecked")
        Map<String, Object> catalogBomYaml = (Map<String, Object>) Yamls.parseAll(catalogBomString.get())
                .iterator().next();

        Optional<String> masterCommitHash = Optional.absent();

        Optional<String> license;
        String licenseUrl = generateRawGithubUrl(repoUrl, "LICENSE.txt");

        if (urlExists(licenseUrl)) {
            license = Optional.of(getGithubRawText(repoUrl, "LICENSE.txt").get());
        } else {
            license = Optional.absent();
        }

        Optional<String> changelog;
        String changelogUrl = generateRawGithubUrl(repoUrl, "CHANGELOG.md");

        if (urlExists(changelogUrl)) {
            changelog = Optional.of(getGithubRawText(repoUrl, "CHANGELOG.md").get());
        } else {
            changelog = Optional.absent();
        }

        //TODO Actually create CatalogItem
        return null;
    }

    private static boolean urlExists(String url) {
        try {
            new URL(url).openConnection().connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        scrapeBlueprints("https://github.com/brooklyncentral/brooklyn-community-catalog");
    }
}
