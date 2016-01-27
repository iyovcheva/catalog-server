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

package org.brooklyncentral.catalog.rest.resources;

import java.util.Collection;

import javax.servlet.ServletContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.brooklyncentral.catalog.ServerServletContextInitializer;
import org.brooklyncentral.catalog.dto.CatalogItem;
import org.brooklyncentral.catalog.dto.Repository;
import org.brooklyncentral.catalog.rest.api.MainApi;
import org.brooklyncentral.catalog.rest.server.Catalog;

public class MainResource implements MainApi {

    @Context ServletContext servletContext;

    @Override
    public Collection<Repository> listRepositories() {

        Catalog catalog = (Catalog) servletContext.getAttribute(ServerServletContextInitializer.CATALOG);
        if (catalog == null) {
            throw new WebApplicationException(Response.status(404).build());
        }

        return catalog.getRepositories(null, null);
    }

    @Override
    public Collection<Repository> listRepositoriesByAuthor(String author) {
        Catalog catalog = (Catalog) servletContext.getAttribute(ServerServletContextInitializer.CATALOG);
        if (catalog == null) {
            throw new WebApplicationException(Response.status(404).build());
        }

        return catalog.getRepositories(author, null);
    }

    @Override
    public CatalogItem getCatalogItem(final String author, final String repoName) {
        Catalog catalog = (Catalog) servletContext.getAttribute(ServerServletContextInitializer.CATALOG);
        if (catalog == null) {
            throw new WebApplicationException(Response.status(404).build());
        }

        return catalog.getCatalogItem(author, repoName);
    }

}
