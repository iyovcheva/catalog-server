package org.brooklyncentral.catalog.scrape;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.brooklyn.util.yaml.Yamls;
import org.brooklyncentral.catalog.dto.CatalogItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

public class CatalogItemScraper {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogItemScraper.class);

    public static Map<String, CatalogItem> scrapeCatalogItems(String repoUrl) {
        List<String> catalogItemRepoUrls = parseDirectoryYaml(repoUrl);
        Map<String, CatalogItem> scrapedCatalogItems = Maps.newHashMapWithExpectedSize(catalogItemRepoUrls
                .size());

        for (String catalogItemRepoUrl : catalogItemRepoUrls) {
            Optional<CatalogItem> catalogItem = parseCatalogItem(catalogItemRepoUrl);

            if (catalogItem.isPresent()) {
                scrapedCatalogItems.put(catalogItem.get().getToken(), catalogItem.get());
            }
        }

        return scrapedCatalogItems;
    }

    @SuppressWarnings("unchecked")
    private static List<String> parseDirectoryYaml(String repoUrl) {
        Optional<String> directoryYamlString;
        try {
            directoryYamlString = CatalogItemScraperHelper.getGithubRawText(repoUrl, "directory.yaml", true);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load blueprint catalog.", e);
        }

        return (List<String>) Yamls.parseAll(directoryYamlString.get()).iterator().next();
    }

    private static Optional<CatalogItem> parseCatalogItem(String repoUrl) {
        try {
            String[] urlTokens = repoUrl.split("/");

            String repoName = urlTokens[urlTokens.length - 1];
            String author = urlTokens[urlTokens.length - 2];

            Optional<String> description = CatalogItemScraperHelper.getGithubRawText(repoUrl, "README.md",
                    true);
            Optional<String> documentation = CatalogItemScraperHelper.getGithubRawText(repoUrl, "items.js",
                    true);

            Optional<String> catalogBomString = CatalogItemScraperHelper.getGithubRawText(repoUrl,
                    "catalog.bom", true);
            @SuppressWarnings("unchecked")
            Map<String, Object> catalogBomYaml = (Map<String, Object>) Yamls.parseAll(catalogBomString.get())
                    .iterator().next();

            Optional<String> masterCommitHash = Optional.absent();

            Optional<String> license = Optional.absent();
            String licenseUrl = CatalogItemScraperHelper.generateRawGithubUrl(repoUrl, "LICENSE");

            if (urlExists(licenseUrl)) {
                license = CatalogItemScraperHelper.getGithubRawText(repoUrl, "LICENSE", false);
            }

            Optional<String> changelog = Optional.absent();
            String changelogUrl = CatalogItemScraperHelper.generateRawGithubUrl(repoUrl, "CHANGELOG");

            if (urlExists(changelogUrl)) {
                changelog = CatalogItemScraperHelper.getGithubRawText(repoUrl, "CHANGELOG", false);
            }

            CatalogItem catalogItem = new CatalogItem(repoUrl, repoName, author, description.get(),
                    documentation.get(), catalogBomString.get(), catalogBomYaml, masterCommitHash.orNull(),
                    license.orNull(), changelog.orNull());

            return Optional.of(catalogItem);
        } catch (Exception e) {
            LOG.warn("Failed to parse catalog item repository: '" + repoUrl + "'.", e);
            return Optional.absent();
        }
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
        scrapeCatalogItems("https://github.com/brooklyncentral/brooklyn-community-catalog");
    }
}
