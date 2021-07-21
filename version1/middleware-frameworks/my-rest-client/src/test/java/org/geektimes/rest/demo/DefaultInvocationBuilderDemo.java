package org.geektimes.rest.demo;

import org.geektimes.rest.client.DefaultInvocationBuilder;
import org.geektimes.rest.core.DefaultResponse;
import org.geektimes.rest.core.DefaultUriBuilder;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class DefaultInvocationBuilderDemo {
    @Test
    public void testGet() {

    }

    @Test
    public void testPost() {
        try {
            URI uri = new URI("http://127.0.0.1:8080/actuator/shutdown");
            DefaultUriBuilder uriBuilder = new DefaultUriBuilder();
            uriBuilder.uri(uri);
            DefaultInvocationBuilder builder = new DefaultInvocationBuilder(uriBuilder);
            Invocation postInvocation = builder.buildPost(Entity.entity(new HashMap(), MediaType.TEXT_HTML_TYPE));
            DefaultResponse response = (DefaultResponse) postInvocation.invoke();
            System.out.println(response.readEntity(String.class));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }
}
