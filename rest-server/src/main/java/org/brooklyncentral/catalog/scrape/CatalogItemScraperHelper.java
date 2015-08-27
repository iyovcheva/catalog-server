package org.brooklyncentral.catalog.scrape;

import java.io.InputStream;
import java.net.URL;

import org.apache.brooklyn.util.stream.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

public class CatalogItemScraperHelper {

    private static final Logger LOG = LoggerFactory.getLogger(CatalogItemScraperHelper.class);

    public static Optional<String> getGithubRawText(String repoUrl, String fileName, boolean required) {
        String rawGithubUrl = generateRawGithubUrl(repoUrl, fileName);

        try (InputStream inputStream = new URL(rawGithubUrl).openStream()) {
            return Optional.of(Streams.readFullyString(inputStream));
        } catch (Exception e) {
            String errorMsg = "File: '" + fileName + "' could not be read from repository: '" + repoUrl
                    + "'.";

            if (required) {
                throw new IllegalStateException(errorMsg, e);
            } else {
                LOG.info(errorMsg);
                return Optional.absent();
            }
        }
    }

    public static String generateRawGithubUrl(String repoUrl, String fileName) {
        return repoUrl.replace("github.com", "raw.githubusercontent.com") + "/master/" + fileName;
    }
}
