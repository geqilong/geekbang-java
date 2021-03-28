package org.geektimes.configuration.microprofile.config.source.impl;

import org.geektimes.configuration.microprofile.config.source.MapBasedConfigSource;

import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.Map;

import static java.lang.String.format;

public class ServletContextConfigSource extends MapBasedConfigSource {
    private final ServletContext servletContext;

    public ServletContextConfigSource(ServletContext servletContext) {
        super(format("ServletContext[path:%s] Init Parameters", servletContext.getContextPath()), 500);
        this.servletContext = servletContext;
    }

    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        if (servletContext != null) {
            Enumeration<String> paramNames = servletContext.getInitParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                configData.put(paramName, servletContext.getInitParameter(paramName));
            }
        }
    }
}
