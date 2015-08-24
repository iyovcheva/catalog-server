package org.brooklyncentral.catalog.model;

import java.net.URL;
import java.util.Map;

import org.apache.brooklyn.util.stream.Streams;
import org.brooklyncentral.catalog.rest.server.CatalogRestApiLauncher;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RepositoriesListTest {

    @Test
    public void testUrl() {
        check("[ 'http://foo.com/bar/baz' ]", "foo.com", "baz");
    }

    @Test
    public void testUrlWithBrooklynCentral() {
        check("[ 'http://foo.com/bar/baz/brooklyn-central-catalog' ]", "foo.com", "baz");
    }

    @Test
    public void testGitHub() {
        check("[ 'github:user/repo' ]", "user", "repo");
    }

    @Test
    public void testGitHubWithBrooklynCentral() {
        RepositoryLinkList rl = check("[ 'github:user/repo/baz/brooklyn-central-catalog/' ]", "user", "baz");
        Assert.assertEquals(rl.asList().get(0).get("url"), "https://github.com/user/repo/baz/brooklyn-central-catalog/");
    }

    @Test
    public void testTestContentUrlProtocol() {
        check("[ 'catalog-test-content:foo/bar/baz/' ]", "foo", "baz");
    }
    
    @Test
    public void testTestContentSampleRepositoriesYaml() throws Exception {
        CatalogRestApiLauncher.supportTestContentResolution();
        check(Streams.readFullyString(new URL("catalog-test-content:repositories.yml").openStream()), "test-user", "foo");
    }
    
    private RepositoryLinkList check(String content, String owner1, String name1) {
        RepositoryLinkList rl = RepositoryLinkList.newInstance(content);
        Map<String, String> r1 = rl.iterator().next();
        if (owner1!=null) Assert.assertEquals(r1.get("owner"), owner1);
        if (name1!=null) Assert.assertEquals(r1.get("name"), name1);
        return rl;
    }
}
