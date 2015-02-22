package org.brooklyncentral.catalog.rest.server;

import java.util.ArrayList;
import java.util.List;

import org.brooklyncentral.catalog.rest.resources.MainResource;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

public class CatalogRestApi {

    public static Iterable<Object> getAllResources() {
        List<Object> resources = new ArrayList<Object>();
        resources.add(new MainResource());
        resources.add(new JacksonJsonProvider());
        return resources;
    }

}
