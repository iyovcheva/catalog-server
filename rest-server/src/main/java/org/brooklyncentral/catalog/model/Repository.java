package org.brooklyncentral.catalog.model;

import java.net.URL;
import java.util.Map;

import org.apache.brooklyn.util.exceptions.Exceptions;
import org.apache.brooklyn.util.net.Urls;
import org.apache.brooklyn.util.stream.Streams;
import org.apache.brooklyn.util.text.Strings;
import org.apache.brooklyn.util.yaml.Yamls;

public class Repository {

    private RepositoryLink link;
    private String baseUrl;
    private String catalogBomString;
    private Map<String, Object> catalogBomYaml;
    
    public static Repository newInstance(RepositoryLink link) {
        Repository r = new Repository();
        r.link = link;
        // baseUrl is the path; for now, assume it is a path unless it is a known specific file
        // TODO in future might check, to allow specifying a different path to catalog.bom or index.yml 
        r.baseUrl = Strings.removeAllFromEnd(link.getUrl(), "catalog.bom", "index.yml");
        
        // TODO on most errors, we should still return the repo, just note that there are errors
        r.catalogBomString = r.readRepoFile("catalog.bom");
        r.catalogBomYaml = r.parseBom();
        return r;
    }

    private String readRepoFile(String subPath) {
        try {
            return Streams.readFullyString(new URL(Urls.mergePaths(baseUrl, subPath)).openStream());
        } catch (Exception e) {
            Exceptions.propagateIfFatal(e);
            throw Exceptions.propagate(new IllegalStateException("Unable to read "+link+": "+e, e));
        }
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseBom() {
        // TODO tie in with logic elsewhere
        return (Map<String,Object>)Yamls.parseAll(catalogBomString).iterator().next();
    }
    
    public RepositoryLink getLink() {
        return link;
    }

    public String getOwnerSlashName() {
        return link.getOwnerSlashName();
    }
    
    public String getCatalogBomString() {
        return catalogBomString;
    }

    public Map<String, Object> getCatalogBomYaml() {
        return catalogBomYaml;
    }

}
