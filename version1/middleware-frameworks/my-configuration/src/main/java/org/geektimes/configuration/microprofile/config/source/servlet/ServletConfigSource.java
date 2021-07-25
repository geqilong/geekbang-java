package org.geektimes.configuration.microprofile.config.source.servlet;

import org.geektimes.configuration.microprofile.config.source.EnumerableConfigSource;

import javax.servlet.ServletConfig;
import java.util.Enumeration;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;

public class ServletConfigSource extends EnumerableConfigSource {

    private final ServletConfig servletConfig;

    public ServletConfigSource(ServletConfig servletConfig) {
        super(format("Servlet[name:%s] Init Parameters", servletConfig.getServletName()), 600);
        this.servletConfig = servletConfig;
    }

    @Override
    protected Supplier<Enumeration<String>> namesSupplier() {
        return new Supplier<Enumeration<String>>() {
            @Override
            public Enumeration<String> get() {
                return servletConfig.getInitParameterNames();
            }
        };
    }

    @Override
    protected Function<String, String> valueResolver() {
        return new Function<String, String>() {
            @Override
            public String apply(String name) {
                return servletConfig.getInitParameter(name);
            }
        };
    }
}
