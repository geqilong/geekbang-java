package org.geektimes.rest.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.geektimes.rest.core.DefaultResponse;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpPostInvocation implements Invocation {
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final URI uri;
    private final URL url;
    private final MultivaluedMap<String, Object> headers;
    private final Entity<?> entity;

    public HttpPostInvocation(URI uri, MultivaluedMap<String, Object> headers, Entity<?> entity) {
        this.uri = uri;
        this.headers = headers;
        this.entity = entity;
        try {
            this.url = uri.toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Invocation property(String name, Object value) {
        return this;
    }

    @Override
    public Response invoke() {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(HttpMethod.POST);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");
            setRequestHeaders(conn);
            setFormData(conn);//设置表单数据
            int status = conn.getResponseCode();
            DefaultResponse response = new DefaultResponse();
            response.setConnection(conn);
            response.setStatus(status);
            return response;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error invoking, cause:", e);
        }
        return null;
    }

    private void setFormData(HttpURLConnection conn) {
        conn.setDoOutput(true);
        OutputStream outputStream;
        try {
            outputStream = conn.getOutputStream();
            ObjectMapper objectMapper = new ObjectMapper();
            outputStream.write(objectMapper.writeValueAsBytes(entity.getEntity()));
            outputStream.flush();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error setting form data:", e);
        }
    }

    private void setRequestHeaders(HttpURLConnection conn) {
        for (Map.Entry<String, List<Object>> entry : headers.entrySet()) {
            String headerName = entry.getKey();
            for (Object headerValue : entry.getValue()) {
                conn.setRequestProperty(headerName, headerValue.toString());
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
