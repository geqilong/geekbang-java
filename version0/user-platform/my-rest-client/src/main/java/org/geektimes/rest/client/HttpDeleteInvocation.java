package org.geektimes.rest.client;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.concurrent.Future;

public class HttpDeleteInvocation implements Invocation {

    public HttpDeleteInvocation(URI build, MultivaluedMap<String, Object> headers) {

    }

    @Override
    public Invocation property(String name, Object value) {
        return null;
    }

    @Override
    public Response invoke() {
        return null;
    }

    @Override
    public <T> T invoke(Class<T> responseType) {
        return null;
    }

    @Override
    public <T> T invoke(GenericType<T> responseType) {
        return null;
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
