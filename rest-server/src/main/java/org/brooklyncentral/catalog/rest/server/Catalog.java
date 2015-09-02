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
