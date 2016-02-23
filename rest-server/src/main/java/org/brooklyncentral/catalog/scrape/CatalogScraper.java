package org.brooklyncentral.catalog.scrape;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.apache.brooklyn.util.yaml.Yamls;
import org.brooklyncentral.catalog.dto.CatalogItem;
import org.brooklyncentral.catalog.rest.server.Catalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class CatalogScraper {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogScraper.class);

    public static Catalog scrapeCatalog(String repoUrl) {
        LOG.info("Start scraping...");

        List<String> catalogItemRepoUrls = parseDirectoryYaml(repoUrl);
        Map<String, CatalogItem> scrapedCatalogItems = Maps.newHashMapWithExpectedSize(catalogItemRepoUrls
                .size());

        for (String catalogItemRepoUrl : catalogItemRepoUrls) {
            Optional<CatalogItem> catalogItem = parseCatalogItem(catalogItemRepoUrl);

            if (catalogItem.isPresent()) {
                scrapedCatalogItems.put(catalogItem.get().getToken(), catalogItem.get());
            }
        }

        LOG.info("Scraping complete");

        return new Catalog(scrapedCatalogItems);
    }

    @SuppressWarnings("unchecked")
    public static List<String> parseDirectoryYaml(String repoUrl) {
        Optional<String> directoryYamlString;
        try {
            directoryYamlString = CatalogScraperHelper.getGithubRawText(repoUrl, "directory.yaml", true);
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

            Optional<String> description = CatalogScraperHelper.getGithubRawText(repoUrl, "README.md",
                    true);

            Optional<String> catalogBomString = CatalogScraperHelper.getGithubRawText(repoUrl,
                    "catalog.bom", true);
            @SuppressWarnings("unchecked")
            Map<String, Object> catalogBomYaml = (Map<String, Object>) Yamls.parseAll(catalogBomString.get())
                    .iterator().next();

            Optional<String> documentation = CatalogScraperHelper.getGithubRawText(repoUrl, "items.js",
                    false);

            Optional<String> masterCommitHash = Optional.absent();

            Optional<String> license = Optional.absent();
            String licenseUrl = CatalogScraperHelper.generateRawGithubUrl(repoUrl, "LICENSE.txt");

            if (urlExists(licenseUrl)) {
                license = CatalogScraperHelper.getGithubRawText(repoUrl, "LICENSE.txt", false);
            }

            Optional<String> changelog = Optional.absent();
            String changelogUrl = CatalogScraperHelper.generateRawGithubUrl(repoUrl, "CHANGELOG.md");

            if (urlExists(changelogUrl)) {
                changelog = CatalogScraperHelper.getGithubRawText(repoUrl, "CHANGELOG.md", false);
            }

            CatalogItem catalogItem = new CatalogItem(repoUrl, repoName, author, description.get(),
                    catalogBomString.get(), catalogBomYaml, documentation.orNull(), masterCommitHash.orNull(),
                    license.orNull(), changelog.orNull());

            return Optional.of(catalogItem);
        } catch (Exception e) {
            LOG.warn("Failed to parse catalog item repository: '" + repoUrl + "'.", e);
            return Optional.absent();
        }
    }

    public static boolean urlExists(String url) {
        try {
            new URL(url).openConnection().connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
