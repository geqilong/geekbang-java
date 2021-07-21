package com.salesmanager.test.shop.rest.service;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

public class MicroProfilePostRestClientTest {
    @Test
    public void testPost() throws URISyntaxException {
        URI uri = new URI("http://127.0.0.1:8080");
        MyClient myClient = RestClientBuilder.newBuilder().baseUri(uri).build(MyClient.class);
        String result = myClient.shutdown();
        System.out.println("Shutdown result: " + result);
    }
}
