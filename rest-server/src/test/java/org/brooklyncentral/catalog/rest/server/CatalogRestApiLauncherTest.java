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

import static org.brooklyncentral.catalog.rest.server.CatalogRestApiLauncher.StartMode.FILTER;
import static org.brooklyncentral.catalog.rest.server.CatalogRestApiLauncher.StartMode.SERVLET;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.testng.Assert;
import org.testng.annotations.Test;

import brooklyn.test.HttpTestUtils;
import brooklyn.util.stream.Streams;

public class CatalogRestApiLauncherTest extends CatalogRestApiLauncherTestFixture {

    @Test
    public void testFilterStart() throws Exception {
        checkRestCatalogApplications(useServerForTest(baseLauncher().mode(FILTER).start()));
    }

    @Test
    public void testServletStart() throws Exception {
        checkRestCatalogApplications(useServerForTest(baseLauncher().mode(SERVLET).start()));
    }

    // TODO web-xml not working because the CatalogServerConfig is being loaded from different jar's
    // (for now we'll just skip support for web.xml)
//    @Test
//    public void testWebAppStart() throws Exception {
//        checkRestCatalogApplications(useServerForTest(baseLauncher().mode(WEB_XML).start()));
//    }

    private CatalogRestApiLauncher baseLauncher() {
        return CatalogRestApiLauncher.launcher().useServerConfig(CatalogRestApiLauncher.newTestContentServerConfig());
    }
    
    private static void checkRestCatalogApplications(Server server) throws Exception {
        String rootUrl = "http://localhost:"+server.getConnectors()[0].getLocalPort();
        HttpTestUtils.assertHealthyStatusCode(
                HttpTestUtils.getHttpStatusCode(rootUrl+"/repositories"));
        HttpTestUtils.assertContentContainsText(rootUrl+"/repositories", "test-user");
    }

    @Test
    public void testCatalogTestContentResolution() throws MalformedURLException, IOException {
        CatalogRestApiLauncher.supportTestContentResolution();
        URL u = new URL("catalog-test-content:/repositories.yml");
        InputStream us = u.openStream();
        String content = Streams.readFullyString(us);
        Assert.assertTrue(content.contains("foo"), "Wrong content: "+content);
    }
    
}

