package org.brooklyncentral.catalog.model;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import brooklyn.util.collections.MutableList;
import brooklyn.util.collections.MutableMap;
import brooklyn.util.net.Urls;
import brooklyn.util.text.Strings;

import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * Contains a list of repository link objects.
 *  
 * @author alex
 *
 */
public class RepositoryLink {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(RepositoryLink.class);
    
    private final Map<String,String> repoLink;
    
    public static final String KEY_URL = "url";
    public static final String KEY_OWNER = "owner";
    public static final String KEY_NAME = "name";
    
    public static Set<String> IGNORED_SEGMENTS = ImmutableSet.of("-", "_", "brooklyn", "central", "catalog");
    
    private RepositoryLink(Map<String,String> repoLink) { 
        this.repoLink = repoLink;
        Preconditions.checkNotNull(repoLink);
        Preconditions.checkNotNull(getUrl());
        Preconditions.checkNotNull(getOwner());
        Preconditions.checkNotNull(getShortName());
    }
    
    public static RepositoryLink newInstance(Map<String,String> repoLink) {
        return new RepositoryLink(repoLink);
    }

    public String getUrl() { return repoLink.get(KEY_URL); }
    public String getOwner() { return repoLink.get(KEY_OWNER); }
    public String getShortName() { return repoLink.get(KEY_NAME); }
    public String getOwnerSlashName() { return getOwner()+"/"+getShortName(); }
    
    public Map<String,String> asMap() {
        return ImmutableMap.copyOf(repoLink);
    }
    
    /**
     * Parses an object, filling in details, to give a RepositoryLink.
     * Parameter can be a string or map, of the form:
     * 
     * <code>
     * "http://host/path/"
     * "github:user/repo"
     * "github:user/repo/path/to/dir"
     * { url: <any-of-the-above>, owner: user, name: repo }
     * </code>
     * 
     * In the simple case, owner is the host or first path segment and repo name is the last segment
     * (excluding segments called e.g. "brooklyn-catalog").
     * 
     * Other metadata, e.g. descriptions, icons, etc, are read from the site itself.
     */
    public static RepositoryLink newInstanceParsed(Object r) {
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
            url = m.remove(KEY_URL);
            if (Strings.isBlank(url))
                throw new IllegalStateException("url required if repo supplied as a map");
            owner = m.remove(KEY_OWNER);
            name = m.remove(KEY_NAME);
            if (owner==null || name==null) {
                RepositoryLink inferred = newInstanceParsed(url);
                if (owner==null) owner = inferred.getOwner();
                if (name==null) name = inferred.getShortName();
            }
            if (!m.isEmpty())
                throw new IllegalStateException("unsupported entries in repo definition "+url+": "+m);
        } else {
            throw new IllegalStateException("unsupported object for repo: "+r);
        }
        
        return RepositoryLink.newInstance(MutableMap.of(KEY_URL, url, KEY_OWNER, owner, KEY_NAME, name));
    }

    public static String inferName(String path, String defaultName) {
        List<String> parts = MutableList.copyOf(Arrays.asList(path.split("/+")));
        Collections.reverse(parts);
        while (!parts.isEmpty()) {
            String candidateName = parts.remove(0);
            String filtered = candidateName.toLowerCase();
            
            filtered = Strings.replaceAll(filtered, Maps.asMap(IGNORED_SEGMENTS, Functions.constant("")));
            if (Strings.isNonBlank(filtered)) return candidateName;
        }
        return defaultName;
    }

}
