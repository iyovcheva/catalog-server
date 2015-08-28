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
package org.brooklyncentral.catalog.rest.server;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.apache.brooklyn.api.mgmt.ManagementContext;
import org.apache.brooklyn.util.exceptions.Exceptions;
import org.apache.brooklyn.util.net.Networking;
import org.apache.brooklyn.util.net.Urls;
import org.apache.brooklyn.util.text.WildcardGlobs;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.Beta;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/** Convenience and demo for launching programmatically. Also used for automated tests.
 * <p>
 * BrooklynLauncher has a more full-featured CLI way to start, 
 * but if you want more control you can:
 * <li> take the WAR this project builds (REST API) -- NB probably want the unshaded one (containing all deps)
 * <li> take the WAR from the jsgui project _and_ this WAR and combine them 
 *      (this one should run as a filter on the others, _not_ as a ResourceCollection where they fight over who's got root)
 * <li> programmatically install things, following the examples herein; 
 *      in particular {@link #installAsServletFilter(ServletContextHandler)} is quite handy! 
 * <p>
 * You can also just run this class. In most installs it just works, assuming your IDE or maven-fu gives you the classpath.
 * Add more apps and entities on the classpath and they'll show up in the catalog.
 **/
public class CatalogRestApiLauncher {

    private static final Logger log = LoggerFactory.getLogger(CatalogRestApiLauncher.class);
    final static int FAVOURITE_PORT = 8081;

    enum StartMode {
        FILTER, SERVLET, WEB_XML
    }

    public static final List<Class<? extends Filter>> DEFAULT_FILTERS = ImmutableList.of();

    private List<Class<? extends Filter>> filters = DEFAULT_FILTERS;
    private CatalogServerConfig serverConfig;
    private StartMode mode = StartMode.FILTER;
    private ContextHandler customContext;
    private boolean deployJsgui = true;

    protected CatalogRestApiLauncher() {}

    /**
     * Runs the server with the given set of filters. 
     * Overrides any previously supplied set (or {@link #DEFAULT_FILTERS} which is used by default).
     */
    public CatalogRestApiLauncher filters(Class<? extends Filter>... filters) {
        this.filters = Lists.newArrayList(filters);
        return this;
    }

    public CatalogRestApiLauncher mode(StartMode mode) {
        this.mode = checkNotNull(mode, "mode");
        return this;
    }

    /** Overrides start mode to use an explicit context */
    public CatalogRestApiLauncher customContext(ContextHandler customContext) {
        this.customContext = checkNotNull(customContext, "customContext");
        return this;
    }

    public CatalogRestApiLauncher withJsgui() {
        this.deployJsgui = true;
        return this;
    }

    public CatalogRestApiLauncher withoutJsgui() {
        this.deployJsgui = false;
        return this;
    }

    public CatalogRestApiLauncher useServerConfig(CatalogServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        return this;
    }
    
    public Server start() {
        ContextHandler context;
        String summary;
        if (customContext == null) {
            switch (mode) {
            case SERVLET:
                context = servletContextHandler();
                summary = "programmatic Jersey ServletContainer servlet";
                break;
            case WEB_XML:
                context = webXmlContextHandler();
                summary = "from WAR at " + ((WebAppContext) context).getWar();
                break;
            case FILTER:
            default:
                context = filterContextHandler();
                summary = "programmatic Jersey ServletContainer filter on webapp at " + ((WebAppContext) context).getWar();
                break;
            }
        } else {
            context = customContext;
            summary = (context instanceof WebAppContext)
                    ? "from WAR at " + ((WebAppContext) context).getWar()
                    : "from custom context";
        }

        return startServer(context, summary);
    }

    private ContextHandler filterContextHandler() {
        WebAppContext context = new WebAppContext();
        initializeForCatalog(context);
        context.setContextPath("/");
        // here we run with the JS GUI, for convenience, if we can find it, else set up an empty dir
        // TODO pretty sure there is an option to monitor this dir and load changes to static content
        // NOTE: When running Brooklyn from an IDE (i.e. by launching BrooklynJavascriptGuiLauncher.main())
        // you will need to ensure that the working directory is set to the jsgui folder. For IntelliJ,
        // set the 'Working directory' of the Run/Debug Configuration to $MODULE_DIR/../jsgui.
        // For Eclipse, use the default option of ${workspace_loc:brooklyn-jsgui}.
        // If the working directory is not set correctly, Brooklyn will be unable to find the jsgui .war
        // file and the 'gui not available' message will be shown.
        context.setWar(this.deployJsgui && findJsguiWebapp() != null
                       ? findJsguiWebapp()
                       : createTempWebDirWithIndexHtml("Brooklyn Central Catalog REST API <p> (gui not available)"));
        installAsServletFilter(context, this.filters);
        return context;
    }

    private ContextHandler servletContextHandler() {
        ResourceConfig config = new DefaultResourceConfig();
        for (Object r: CatalogRestApi.getAllResources())
            config.getSingletons().add(r);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        initializeForCatalog(context);
        ServletHolder servletHolder = new ServletHolder(new ServletContainer(config));
        context.addServlet(servletHolder, "/*");
        context.setContextPath("/");

        installBrooklynFilters(context, this.filters);
        return context;
    }

    public void initializeForCatalog(ServletContextHandler context) {
        CatalogServerConfig.apply(context, serverConfig);
    }

    private ContextHandler webXmlContextHandler() {
        // TODO add security to web.xml
        WebAppContext context;
        if (findMatchingFile("src/main/webapp")!=null) {
            // running in source mode; need to use special classpath
            context = new WebAppContext("src/main/webapp", "/");
            context.setExtraClasspath("./target/classes");
        } else if (findRestApiWar()!=null) {
            context = new WebAppContext(findRestApiWar(), "/");
        } else {
            throw new IllegalStateException("Cannot find WAR for REST API. Expected in target/*.war, Maven repo, or in source directories.");
        }
        initializeForCatalog(context);

        return context;
    }

    /** starts a server, on all NICs if security is configured,
     * otherwise (no security) only on loopback interface */
    public static Server startServer(ManagementContext mgmt, ContextHandler context, String summary) {
        // TODO this repeats code in BrooklynLauncher / WebServer. should merge the two paths.
        boolean secure = true;
        InetSocketAddress bindLocation = new InetSocketAddress(
                secure ? Networking.ANY_NIC : Networking.LOOPBACK,
                        Networking.nextAvailablePort(FAVOURITE_PORT));
        return startServer(context, summary, bindLocation);
    }

    public static Server startServer(ContextHandler context, String summary, InetSocketAddress bindLocation) {
        Server server = new Server(bindLocation);
        server.setHandler(context);
        try {
            server.start();
        } catch (Exception e) {
            throw Exceptions.propagate(e);
        }
        log.info("Brooklyn REST server started ("+summary+") on");
        log.info("  http://localhost:"+server.getConnectors()[0].getLocalPort()+"/");

        return server;
    }

    public static CatalogRestApiLauncher launcher() {
        return new CatalogRestApiLauncher();
    }

    private static boolean testContentResolutionEnabled = false;
    public static synchronized void supportTestContentResolution() {
        if (testContentResolutionEnabled) return;
        testContentResolutionEnabled = true;
        URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {
            @Override
            public URLStreamHandler createURLStreamHandler(String protocol) {
                if (protocol.equals("catalog-test-content")) {
                    return new URLStreamHandler() {
                        @Override
                        protected URLConnection openConnection(URL u) throws IOException {
                            String path = Urls.mergePaths("content", u.getPath());
                            URL resolvedUrl = getClass().getClassLoader().getResource(path);
                            if (resolvedUrl==null)
                                throw new IOException("Could not find "+path+" on classpath when looking up "+u);
                            return resolvedUrl.openConnection();
                        }
                    };
                }
                return null;
            }
        });
    }

    public static void main(String[] args) throws Exception {
        // TODO accept "--live" to use the GitHub endpoint; right now local is hard-coded
        startRestResourcesViaFilter(CatalogServerConfig.newDefault());
        log.info("Press Ctrl-C to quit.");
    }

    public static CatalogServerConfig newTestContentServerConfig() {
        supportTestContentResolution();
        CatalogServerConfig config = CatalogServerConfig.newDefault();
        config.setRepositoriesUrl("catalog-test-content:repositories.yml");
        return config;
    }

    public static Server startRestResourcesViaFilter(CatalogServerConfig config) {
        return new CatalogRestApiLauncher()
                .useServerConfig(config)
                .mode(StartMode.FILTER)
                .start();
    }

    public static Server startRestResourcesViaServlet() throws Exception {
        return new CatalogRestApiLauncher()
                .mode(StartMode.SERVLET)
                .start();
    }

    public static Server startRestResourcesViaWebXml() throws Exception {
        return new CatalogRestApiLauncher()
                .mode(StartMode.WEB_XML)
                .start();
    }

    public static void installAsServletFilter(ServletContextHandler context) {
        installAsServletFilter(context, DEFAULT_FILTERS);
    }

    private static void installAsServletFilter(ServletContextHandler context, List<Class<? extends Filter>> filters) {
        installBrooklynFilters(context, filters);

        // now set up the REST servlet resources
        ResourceConfig config = new DefaultResourceConfig();
        // load all our REST API modules, JSON, and Swagger
        for (Object r: CatalogRestApi.getAllResources())
            config.getSingletons().add(r);

        // disable caching for dynamic content
//        config.getProperties().put(ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS, NoCacheFilter.class.getName());
        // configure to match empty path, or any thing which looks like a file path with /assets/ and extension html, css, js, or png
        // and treat that as static content
        config.getProperties().put(ServletContainer.PROPERTY_WEB_PAGE_CONTENT_REGEX, "(/?|[^?]*/assets/[^?]+\\.[A-Za-z0-9_]+)");
        // and anything which is not matched as a servlet also falls through (but more expensive than a regex check?)
        config.getFeatures().put(ServletContainer.FEATURE_FILTER_FORWARD_ON_404, true);
        // finally create this as a _filter_ which falls through to a web app or something (optionally)
        FilterHolder filterHolder = new FilterHolder(new ServletContainer(config));
        context.addFilter(filterHolder, "/*", EnumSet.allOf(DispatcherType.class));
    }

    private static void installBrooklynFilters(ServletContextHandler context, List<Class<? extends Filter>> filters) {
        for (Class<? extends Filter> filter : filters) {
            context.addFilter(filter, "/*", EnumSet.allOf(DispatcherType.class));
        }
    }

    /**
     * Starts the server on all nics (even if security not enabled).
     * @deprecated since 0.6.0; use {@link #launcher()} and set a custom context
     */
    @Deprecated
    public static Server startServer(ContextHandler context, String summary) {
        return CatalogRestApiLauncher.startServer(context, summary,
                new InetSocketAddress(Networking.ANY_NIC, Networking.nextAvailablePort(FAVOURITE_PORT)));
    }

    /** look for the JS GUI webapp in common places, returning path to it if found, or null */
    private static String findJsguiWebapp() {
        // could also look in maven repo ?
        return Optional
                .fromNullable(findMatchingFile("../jsgui/src/main/webapp"))
                .or(findMatchingFile("../jsgui/target/*.war"))
                .orNull();
    }

    /** look for the REST WAR file in common places, returning path to it if found, or null */
    private static String findRestApiWar() {
        // don't look at src/main/webapp here -- because classes won't be there!
        // could also look in maven repo ?
        return findMatchingFile("../rest/target/*.war").orNull();
    }

    /** returns the supplied filename if it exists (absolute or relative to the current directory);
     * supports globs in the filename portion only, in which case it returns the _newest_ matching file.
     * <p>
     * otherwise returns null */
    @Beta // public because used in dependent test projects
    public static Optional<String> findMatchingFile(String filename) {
        final File f = new File(filename);
        if (f.exists()) return Optional.of(filename);
        File dir = f.getParentFile();
        File result = null;
        if (dir.exists()) {
            File[] matchingFiles = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return WildcardGlobs.isGlobMatched(f.getName(), name);
                }
            });
            for (File mf: matchingFiles) {
                if (result==null || mf.lastModified() > result.lastModified()) result = mf;
            }
        }
        if (result==null) return Optional.absent();
        return Optional.of(result.getAbsolutePath());
    }

    /** create a directory with a simple index.html so we have some content being served up */
    private static String createTempWebDirWithIndexHtml(String indexHtmlContent) {
        File dir = Files.createTempDir();
        dir.deleteOnExit();
        try {
            Files.write(indexHtmlContent, new File(dir, "index.html"), Charsets.UTF_8);
        } catch (IOException e) {
            Exceptions.propagate(e);
        }
        return dir.getAbsolutePath();
    }
    
}
