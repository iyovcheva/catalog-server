package org.brooklyncentral.catalog.model;

import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.brooklyn.util.collections.MutableList;
import org.apache.brooklyn.util.exceptions.Exceptions;
import org.apache.brooklyn.util.stream.Streams;
import org.apache.brooklyn.util.yaml.Yamls;
import org.brooklyncentral.catalog.rest.server.CatalogServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains a list of repository link objects.
 *  
 * @author alex
 *
 */
public class RepositoryLinkList {

    private static final Logger log = LoggerFactory.getLogger(RepositoryLinkList.class);
    
    public List<Map<String,String>> repos = MutableList.of();
    
    private RepositoryLinkList() {}
    
    public static RepositoryLinkList newInstance(CatalogServerConfig config) {
        String content;
        String repoUrl = config.getRepositoriesUrl();
        try {
            if (repoUrl==null) throw new IllegalStateException("No URL defined");
            content = Streams.readFullyString(new URL(repoUrl).openStream());
        } catch (Exception e) {
            Exceptions.propagateIfFatal(e);
            throw new IllegalStateException("Invalid URL for repository: "+repoUrl, e);
        }
        
        try {
            return newInstance(content);
        } catch (Exception e) {
            Exceptions.propagateIfFatal(e);
            throw new IllegalStateException("Unparseable content for repository: "+repoUrl, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static RepositoryLinkList newInstance(String content) {
        List<Object> repos = MutableList.copyOf(Yamls.parseAll(content));
        if (repos.size()==1 && repos.get(0) instanceof List)
            repos = (List<Object>) repos.get(0);
        
        RepositoryLinkList result = new RepositoryLinkList();
        for (Object r: repos) {
            try {
                result.addRepo(r);
            } catch (Exception e) {
                Exceptions.propagateIfFatal(e);
                log.warn("Skipping invalid repo definition ("+r+"): "+e);
            }
        }
        return result;
    }

    public void addRepo(Object r) {
        repos.add(RepositoryLink.newInstanceParsed(r).asMap());
    }
    
    public Iterator<Map<String,String>> iterator() {
        return repos.iterator();
    }
    
    public List<Map<String, String>> asList() {
        return MutableList.copyOf(repos);
    }
    
}
