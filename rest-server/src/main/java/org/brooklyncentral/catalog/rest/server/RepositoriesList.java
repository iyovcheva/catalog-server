package org.brooklyncentral.catalog.rest.server;

import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.util.collections.MutableList;
import brooklyn.util.collections.MutableMap;
import brooklyn.util.exceptions.Exceptions;
import brooklyn.util.net.Urls;
import brooklyn.util.stream.Streams;
import brooklyn.util.text.Strings;
import brooklyn.util.yaml.Yamls;

public class RepositoriesList {

    private static final Logger log = LoggerFactory.getLogger(RepositoriesList.class);
    
    public List<Map<String,String>> repos = MutableList.of();
    
    private RepositoriesList() {}
    
    public static RepositoriesList newInstance(CatalogServerConfig config) {
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
    public static RepositoriesList newInstance(String content) {
        List<Object> repos = MutableList.copyOf(Yamls.parseAll(content));
        if (repos.size()==1 && repos.get(0) instanceof List)
            repos = (List<Object>) repos.get(0);
        
        RepositoriesList result = new RepositoriesList();
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
        repos.add(parseRepoObject(r));
    }
    
    /**
     * Repo can be of the form:
     * 
     * "github:user/repo"
     * "github:user/repo/path/to/dir"
     * "http://host/path/"
     * { url: <any-of-the-above>, owner: user, name: repo }
     * 
     * In the simple case, owner is the host or first path segmant, repo name is the last segment,
     * excluding segments called e.g. "brooklyn-catalog".
     */
    public Map<String,String> parseRepoObject(Object r) {
        String url, name, owner;
        if (r instanceof String) {
            String s = (String)r;
            if (s.startsWith("github:")) {
                String g = Strings.removeFromStart(s, "github:");
                g = Strings.removeAllFromStart(g, "/");
                url = Urls.mergePaths("https://github.com/", g);
                owner = g.split("/+")[0];
                name = inferName(g, owner);
            } else {
                url = s;
                owner = URI.create(url).getHost();
                if (owner==null) {
                    String ssp = URI.create(url).getSchemeSpecificPart();
                    if (ssp!=null) {
                        String[] paths = ssp.split("/+");
                        if (paths.length>0) owner = paths[0];
                    }
                }
                name = inferName(url, owner);
            }
        } else if (r instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String,String> m = MutableMap.copyOf((Map<String,String>)r);
            url = m.remove("url");
            if (Strings.isBlank(url))
                throw new IllegalStateException("url required if repo supplied as a map");
            owner = m.remove("owner");
            name = m.remove("name");
            if (owner==null || name==null) {
                Map<String, String> inferred = parseRepoObject(url);
                if (owner==null) owner = inferred.get("owner");
                if (name==null) name = inferred.get("name");
            }
            if (!m.isEmpty())
                throw new IllegalStateException("unsupported entries in repo definition "+url+": "+m);
        } else {
            throw new IllegalStateException("unsupported object for repo: "+r);
        }
        
        return MutableMap.of("name", name, "url", url, "owner", owner);
    }

    public static String inferName(String path, String defaultName) {
        List<String> parts = MutableList.copyOf(Arrays.asList(path.split("/+")));
        Collections.reverse(parts);
        while (!parts.isEmpty()) {
            String candidateName = parts.remove(0);
            String filtered = candidateName.toLowerCase();
            filtered = Strings.replaceAll(filtered, MutableMap.of("-", "", "_", "", "brooklyn", "", "central", "", "catalog", ""));
            if (Strings.isNonBlank(filtered)) return candidateName;
        }
        return defaultName;
    }

    public Iterator<Map<String,String>> iterator() {
        return repos.iterator();
    }
    
    public List<Map<String, String>> asList() {
        return MutableList.copyOf(repos);
    }
    
}
