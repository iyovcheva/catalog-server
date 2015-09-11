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

package org.brooklyncentral.catalog;

import org.apache.brooklyn.util.time.Duration;
import org.brooklyncentral.catalog.rest.server.Catalog;
import org.brooklyncentral.catalog.rest.server.CatalogServerConfig;
import org.brooklyncentral.catalog.scrape.CatalogScraper;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.util.Timer;
import java.util.TimerTask;

public class ServerServletContextInitializer extends ResteasyBootstrap {

    private static final Logger LOG = LoggerFactory.getLogger(ServerServletContextInitializer.class);

    public static final String CATALOG_CONFIG = CatalogServerConfig.class.getName();
    public static final String CATALOG = Catalog.class.getName();

    private static final long PERIOD = Duration.minutes(30).toMilliseconds();

    private final Timer timer = new Timer();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        LOG.info("Starting scraper...");
        ServletContext context = servletContextEvent.getServletContext();
        CatalogServerConfig config = CatalogServerConfig.newDefault();
        Catalog catalog = CatalogScraper.scrapeCatalog(config.getRepositoriesUrl());

        LOG.info("Scraping complete");
        context.setAttribute(CATALOG_CONFIG, config);
        context.setAttribute(CATALOG, catalog);

        LOG.info("Starting scraper task");
        timer.schedule(new ScraperTask(context), PERIOD, PERIOD);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        timer.cancel();
        timer.purge();
    }

    private static class ScraperTask extends TimerTask {

        private final ServletContext servletContext;

        protected ScraperTask(ServletContext servletContext) {
            super();
            this.servletContext = servletContext;
        }

        @Override
        public void run() {
            try {
                Catalog catalog = CatalogScraper.scrapeCatalog(((CatalogServerConfig) servletContext.getAttribute(CATALOG_CONFIG)).getRepositoriesUrl());
                servletContext.setAttribute(CATALOG, catalog);
                LOG.info("Catalog refreshed");
            } catch (IllegalStateException e) {
                LOG.warn("Fail to refresh catalog: " + e.getMessage(), e);
            }
        }
    }
}
