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
package org.brooklyncentral.catalog.jsgui;

import org.apache.brooklyn.test.HttpTestUtils;
import org.eclipse.jetty.server.Server;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/** Convenience and demo for launching programmatically. */
public class CatalogJsGuiLauncherTest {

    Server server = null;
    
    @AfterMethod(alwaysRun=true)
    public void stopServer() throws Exception {
        if (server!=null) {
            server.stop();
            server = null;
        }
    }
    
    @Test
    public void testJavascriptWithoutRest() throws Exception {
        server = CatalogJsGuiLauncher.startJavascriptWithoutRest();
        checkUrlContains("/index.html", "Brooklyn");
    }

    protected void checkUrlContains(String path, String text) {
        HttpTestUtils.assertContentContainsText(rootUrl()+path, text);
    }

    protected void checkEventuallyHealthy() {
        HttpTestUtils.assertHttpStatusCodeEventuallyEquals(rootUrl(), 200);
    }

    protected String rootUrl() {
        return "http://localhost:"+server.getConnectors()[0].getLocalPort();
    }

}
