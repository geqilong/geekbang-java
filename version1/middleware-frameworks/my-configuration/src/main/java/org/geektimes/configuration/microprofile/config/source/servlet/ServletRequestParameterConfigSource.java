package org.geektimes.configuration.microprofile.config.source.servlet;

import org.geektimes.configuration.microprofile.config.source.EnumerableConfigSource;
import org.geektimes.configuration.microprofile.config.source.servlet.initializer.ServletRequestThreadLocalListener;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Collections.emptyEnumeration;

public class ServletRequestParameterConfigSource extends EnumerableConfigSource {

    public ServletRequestParameterConfigSource() {
        super("Request Parameters", 1000);
    }

    @Override
    protected Supplier<Enumeration<String>> namesSupplier() {
        return new Supplier<Enumeration<String>>() {
            @Override
            public Enumeration<String> get() {
                HttpServletRequest httpServletRequest = request();
                return httpServletRequest == null ? emptyEnumeration() : httpServletRequest.getParameterNames();
            }
        };
    }

    @Override
    protected Function<String, String> valueResolver() {
        return this::getParameterValue;
    }

    private String getParameterValue(String parameterName) {
        String[] parameterValues = request().getParameterValues(parameterName);
        return String.join(",", parameterValues);
    }

    private HttpServletRequest request() {
        return ServletRequestThreadLocalListener.getRequest();
    }
}
