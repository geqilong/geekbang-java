package org.geektimes.configuration.microprofile.config.source.servlet;

import org.geektimes.configuration.microprofile.config.source.EnumerableConfigSource;
import org.geektimes.configuration.microprofile.config.source.servlet.initializer.ServletRequestThreadLocalListener;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Collections.emptyEnumeration;
import static java.util.Collections.list;

public class ServletRequestHeaderConfigSource extends EnumerableConfigSource {

    public ServletRequestHeaderConfigSource() {
        super("Request Headers", 1100);
    }

    @Override
    protected Supplier<Enumeration<String>> namesSupplier() {
        return new Supplier<Enumeration<String>>() {
            @Override
            public Enumeration<String> get() {
                HttpServletRequest request = request();
                return request == null ? emptyEnumeration() : request.getHeaderNames();
            }
        };
    }

    @Override
    protected Function<String, String> valueResolver() {
        return this::getHeaderValue;
    }

    private String getHeaderValue(String parameterName) {
        Enumeration<String> headerValues = request().getHeaders(parameterName);
        return String.join(",", list(headerValues).toArray(new String[0]));
    }

    private HttpServletRequest request() {
        return ServletRequestThreadLocalListener.getRequest();
    }
}
