package org.brooklyncentral.catalog;

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.brooklyn.util.time.Duration;
import org.brooklyncentral.catalog.rest.server.Catalog;
import org.brooklyncentral.catalog.rest.server.CatalogServerConfig;
import org.brooklyncentral.catalog.scrape.CatalogScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerServletContextInitializer implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(ServerServletContextInitializer.class);

    public static final String CATALOG_CONFIG = CatalogServerConfig.class.getName();
    public static final String CATALOG = Catalog.class.getName();

    private static final long PERIOD = Duration.minutes(30).toMilliseconds();

    private final Timer timer = new Timer();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        CatalogServerConfig config = CatalogServerConfig.newDefault();
        Catalog catalog = CatalogScraper.scrapeCatalog(config.getRepositoriesUrl());

        ServletContext context = servletContextEvent.getServletContext();
        context.setAttribute(CATALOG_CONFIG, config);
        context.setAttribute(CATALOG, catalog);

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
            } catch (IllegalStateException e) {
                LOG.warn("Fail to refresh catalog: " + e.getMessage(), e);
            }
        }
    }
}
