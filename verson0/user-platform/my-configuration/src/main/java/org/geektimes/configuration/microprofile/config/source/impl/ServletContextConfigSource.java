package org.geektimes.configuration.microprofile.config.source.impl;

import org.geektimes.configuration.microprofile.config.source.MapBasedConfigSource;

import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.Map;

public class ServletContextConfigSource extends MapBasedConfigSource {
    public static ThreadLocal<ServletContext> SERVLET_CONTEXT_TL = ThreadLocal.withInitial(() -> null);

    public ServletContextConfigSource() {
        super("ServletContext Init Parameters", 500);
    }

    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        ServletContext servletContext = SERVLET_CONTEXT_TL.get();
        if (servletContext != null) {
            Enumeration<String> paramNames = servletContext.getInitParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                configData.put(paramName, servletContext.getInitParameter(paramName));
            }
        }
    }
}
