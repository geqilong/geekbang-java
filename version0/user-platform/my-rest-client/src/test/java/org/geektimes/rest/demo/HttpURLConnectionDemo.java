package org.geektimes.rest.demo;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class HttpURLConnectionDemo {
    public static void main(String[] args) throws IOException, URISyntaxException {
        URI uri = new URI("http://127.0.0.1:8080/hello/world");
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        try(InputStream inputStream = connection.getInputStream()){
            System.out.println(IOUtils.toString(inputStream, "UTF-8"));
        }
        connection.disconnect();
    }
}
