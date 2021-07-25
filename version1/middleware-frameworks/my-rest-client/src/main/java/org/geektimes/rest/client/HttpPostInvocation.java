package org.geektimes.rest.client;

import org.geektimes.rest.core.DefaultResponse;
import org.geektimes.rest.util.HttpBodyConverter;
import org.geektimes.rest.util.HttpBodyConverters;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class HttpPostInvocation implements Invocation {
    private final HttpBodyConverters converters = new HttpBodyConverters();
    private final URI uri;
    private final URL url;
    private final MultivaluedMap<String, Object> headers;
    private final Entity<?> entity;

    HttpPostInvocation(URI uri, MultivaluedMap<String, Object> headers, Entity<Map> entity) {
        this.uri = uri;
        this.headers = headers;
        this.entity = entity;
        try {
            this.url = uri.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException("Illegal uri: " + this.uri);
        }
    }

    @Override
    public Invocation property(String name, Object value) {
        return this;
    }

    @Override
    public Response invoke() {
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(HttpMethod.POST);
            setRequestHeaders(connection);
            setEntityValues(connection);
            int statusCode = connection.getResponseCode();
            DefaultResponse response = new DefaultResponse();
            response.setConnection(connection);
            response.setStatus(statusCode);
            return response;
        } catch (IOException e) {
            throw new RuntimeException("Error sending post request..." + e.getCause());
        }
    }

    private void setEntityValues(HttpURLConnection connection) throws IOException {
        if (entity != null && entity.getEntity() != null) {
            Class<?> clazz = entity.getClass();
            Type type = clazz.getGenericSuperclass();
            Annotation[] annotations = entity.getAnnotations();
            MediaType mediaType = entity.getMediaType();
            HttpBodyConverter converter = converters.getWriteableConverter(clazz, type, annotations, mediaType);
            if (converter.isWriteable(clazz, type, annotations, mediaType)) {
                OutputStream outputStream = connection.getOutputStream();
                long length = converter.getSize(entity.getEntity(), clazz, type, annotations, mediaType);
                connection.setRequestProperty(HttpHeaders.CONTENT_LENGTH, Long.toString(length));
                converter.writeTo(entity.getEntity(), clazz, type, annotations, mediaType, headers, outputStream);
            }
        }
    }

    private void setRequestHeaders(HttpURLConnection connection) {
        for (Map.Entry<String, List<Object>> entry : headers.entrySet()) {
            String headerName = entry.getKey();
            for (Object headerValue : entry.getValue()) {
                connection.setRequestProperty(headerName, headerValue.toString());
            }
        }
    }

    @Override
    public <T> T invoke(Class<T> responseType) {
        Response response = invoke();
        return response.readEntity(responseType);
    }

    @Override
    public <T> T invoke(GenericType<T> responseType) {
        Response response = invoke();
        return response.readEntity(responseType);
    }

    @Override
    public Future<Response> submit() {
        return null;
    }

    @Override
    public <T> Future<T> submit(Class<T> responseType) {
        return null;
    }

    @Override
    public <T> Future<T> submit(GenericType<T> responseType) {
        return null;
    }

    @Override
    public <T> Future<T> submit(InvocationCallback<T> callback) {
        return null;
    }
}
