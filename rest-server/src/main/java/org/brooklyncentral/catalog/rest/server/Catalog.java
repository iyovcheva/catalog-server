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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.brooklyncentral.catalog.dto.CatalogItem;
import org.brooklyncentral.catalog.dto.Repository;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

public class Catalog {

    private final Map<String, CatalogItem> catalogItems;

    public Catalog(Map<String, CatalogItem> catalogItems) {
        this.catalogItems = catalogItems;
    }

    public Collection<CatalogItem> getCatalogItems() {
        return ImmutableList.copyOf(catalogItems.values());
    }

    public Collection<Repository> getRepositories(final String author, final String repoName) {
        Collection<Repository> repositories = Collections2.transform(getCatalogItems(), new Function<CatalogItem, Repository>() {
            @Override
            public Repository apply(CatalogItem input) {
                return input.getRepository();
            }
        });

        List<Predicate<Repository>> predicates = new ArrayList<>();
        if (!StringUtils.isEmpty(author)) {
            predicates.add(new Predicate<Repository>() {
                @Override
                public boolean apply(@Nullable Repository repository) {
                    return repository != null && StringUtils.equals(repository.getAuthor(), author);
                }
            });
        }
        if (!StringUtils.isEmpty(repoName)) {
            predicates.add(new Predicate<Repository>() {
                @Override
                public boolean apply(@Nullable Repository repository) {
                    return repository != null && StringUtils.equals(repository.getRepoName(), repoName);
                }
            });
        }

        return Collections2.filter(repositories, predicates.size() > 0 ? Predicates.and(predicates) : Predicates.<Repository>alwaysTrue());
    }

    public CatalogItem getCatalogItem(String ownerName, String repoName) {
        String token = ownerName + "/" + repoName;
        return catalogItems.get(token);
    }

}
