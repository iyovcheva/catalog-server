package org.brooklyncentral.catalog.validations;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import com.google.common.base.Optional;
import org.brooklyncentral.catalog.scrape.CatalogScraper;
import org.brooklyncentral.catalog.scrape.CatalogScraperHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CatalogValidator {


    private static final Logger LOG = LoggerFactory.getLogger(CatalogScraper.class);

    public static boolean validate(String repoUrl) {
        LOG.info("Start validation...");

        List<String> catalogItemRepoUrls = CatalogScraper.parseDirectoryYaml(repoUrl);

        boolean successfullyValidated = true;
        for (String catalogItemRepoUrl : catalogItemRepoUrls) {
            successfullyValidated = successfullyValidated && validateCatalogItem(catalogItemRepoUrl);
        }

        LOG.info("Validation complete");

        return successfullyValidated;
    }

    public static boolean validateCatalogItem(String repoUrl) {
        String[] urlTokens = repoUrl.split("/");
        String repoName = urlTokens[urlTokens.length - 1];
        String author = urlTokens[urlTokens.length - 2];

        LOG.info("Validating catalog " + author + "/" + repoName);

        Optional<String> description = Optional.absent();
        Optional<String> catalogBomString = Optional.absent();
        boolean isValid = true;
        try {
            description = CatalogScraperHelper.getGithubRawText(repoUrl, "README.md", true);
            catalogBomString = CatalogScraperHelper.getGithubRawText(repoUrl, "catalog.bom", true);
            LOG.info("Successfull validation");
        } catch (IllegalStateException e) {
            if (!description.isPresent()) {
                LOG.info("Validation failed - README.md is required");
            }
            if (!catalogBomString.isPresent()) {
                LOG.info("Validation failed - catalog.bom is required");
            }
            isValid = false;
        }
        return isValid;
    }
}
