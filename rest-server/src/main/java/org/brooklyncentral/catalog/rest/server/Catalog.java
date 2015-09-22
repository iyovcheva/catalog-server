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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.brooklyncentral.catalog.dto.CatalogItem;
import org.brooklyncentral.catalog.dto.Repository;

import java.util.List;
import java.util.Map;

public class Catalog {

    private final Map<String, CatalogItem> catalogItems;

    public Catalog(Map<String, CatalogItem> catalogItems) {
        this.catalogItems = catalogItems;
    }

    public List<CatalogItem> getCatalogItems() {
        return ImmutableList.copyOf(catalogItems.values());
    }

    public List<Repository> getRepositories() {
        return Lists.transform(getCatalogItems(), new Function<CatalogItem, Repository>() {
            @Override
            public Repository apply(CatalogItem input) {
                return input.getRepository();
            }
        });
    }

    public CatalogItem getCatalogItem(String ownerName, String repoName) {
        String token = ownerName + "/" + repoName;
        return catalogItems.get(token);
    }

}
