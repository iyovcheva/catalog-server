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

import org.apache.brooklyn.util.exceptions.Exceptions;
import org.eclipse.jetty.server.Server;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;

public abstract class CatalogRestApiLauncherTestFixture {

    Server server = null;
    
    @AfterMethod(alwaysRun=true)
    public void stopServer() throws Exception {
        if (server!=null) {
            server.stop();
            server = null;
        }
    }
    
    protected Server newServer() {
        try {
            Server server = CatalogRestApiLauncher.launcher()
                    .useServerConfig(CatalogRestApiLauncher.newTestContentServerConfig())
                    .start();
            return server;
        } catch (Exception e) {
            throw Exceptions.propagate(e);
        }
    }
    
    protected Server useServerForTest(Server server) {
        if (this.server!=null) {
            Assert.fail("Test only meant for single server; already have "+this.server+" when checking "+server);
        } else {
            this.server = server;
        }
        return server;
    }
    
    protected String getBaseUri() {
        return getBaseUri(server);
    }
    public static String getBaseUri(Server server) {
        return "http://localhost:"+server.getConnectors()[0].getLocalPort();
    }
    
}
