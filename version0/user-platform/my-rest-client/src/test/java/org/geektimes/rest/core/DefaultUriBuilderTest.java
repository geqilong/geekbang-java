package org.geektimes.rest.core;

import org.geektimes.rest.util.Maps;
import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static org.junit.Assert.assertEquals;

public class DefaultUriBuilderTest {

    @Test
    public void testBuildFromMap() {
        UriBuilder builder = new DefaultUriBuilder()
                .scheme("http")
                .host("127.0.0.1")
                .port(8080)
                .path("/{a}/{b}/{c}")
                .queryParam("x", "a")
                .queryParam("y", "b", "c")
                .fragment("{d}")
                .resolveTemplates(Maps.of("a", 1, "b", 2, "c", 3, "d", 4));
        URI uri = builder.build();
        assertEquals("http", uri.getScheme());
        assertEquals("127.0.0.1", uri.getHost());
        assertEquals(8080, uri.getPort());
        assertEquals("x=a&y=b&y=c", uri.getRawQuery());
        assertEquals("/1/2/3", uri.getRawPath());
        assertEquals("4", uri.getFragment());

        assertEquals("http://127.0.0.1:8080/1/2/3?x=a&y=b&y=c#4", uri.toString());
    }

    @Test
    public void testBuild() {
        UriBuilder builder = new DefaultUriBuilder()
                .uri("http://127.0.0.1:8080/{a}/{b}/{c}?x=a&y=b&y=c#{d}");

        URI uri = builder.build(1, 2, 3, 4);

        assertEquals("http", uri.getScheme());
        assertEquals("127.0.0.1", uri.getHost());
        assertEquals(8080, uri.getPort());
        assertEquals("x=a&y=b&y=c", uri.getRawQuery());
        assertEquals("/1/2/3", uri.getRawPath());
        assertEquals("4", uri.getFragment());

        assertEquals("http://127.0.0.1:8080/1/2/3?x=a&y=b&y=c#4", uri.toString());
    }
}
