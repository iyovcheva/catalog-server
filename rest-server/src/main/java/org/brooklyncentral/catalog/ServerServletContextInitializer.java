package org.brooklyncentral.catalog;

import org.apache.brooklyn.util.time.Duration;
import org.brooklyncentral.catalog.dto.CatalogItem;
import org.brooklyncentral.catalog.rest.server.Catalog;
import org.brooklyncentral.catalog.rest.server.CatalogServerConfig;
import org.brooklyncentral.catalog.scrape.CatalogItemScraper;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ServerServletContextInitializer extends ResteasyBootstrap {

    private static final Logger LOG = LoggerFactory.getLogger(ServerServletContextInitializer.class);
    private static final long PERIOD = Duration.minutes(30).toMilliseconds();

    public static final String CATALOG_CONFIG = CatalogServerConfig.class.getName();
    public static final String CATALOG = Catalog.class.getName();

    private final Timer timer = new Timer();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        LOG.info("Starting scraper...");
        ServletContext context = servletContextEvent.getServletContext();
        CatalogServerConfig config = CatalogServerConfig.newDefault();
        Map<String, CatalogItem> catalogItems = CatalogItemScraper.scrapeCatalogItems(config.getRepositoriesUrl());

        LOG.info("Scraping complete");
        context.setAttribute(CATALOG_CONFIG, config);
        context.setAttribute(CATALOG, new Catalog(catalogItems));

        LOG.info("Starting scraper task");
        timer.schedule(new ScraperTask(context), PERIOD, PERIOD);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        timer.purge();
        timer.cancel();
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
                Map<String, CatalogItem> catalogItems = CatalogItemScraper.scrapeCatalogItems(((CatalogServerConfig) servletContext.getAttribute(CATALOG_CONFIG)).getRepositoriesUrl());
                servletContext.setAttribute(CATALOG, new Catalog(catalogItems));
                LOG.info("Catalog refreshed");
            } catch (IllegalStateException e) {
                LOG.warn("Fail to refresh catalog: " + e.getMessage(), e);
            }
        }
    }
}
