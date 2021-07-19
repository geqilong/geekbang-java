package org.geektimes.configuration.microprofile.config.source.servlet;

import org.geektimes.configuration.microprofile.config.source.MapBasedConfigSource;

import javax.servlet.ServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * ServletRequest ConfigSource
 */
public class ServletRequestConfigSource extends MapBasedConfigSource {
    private final ServletRequest request;

    public ServletRequestConfigSource(ServletRequest request) {
        super("HttpServletRequest Parameters", 50);
        this.request = request;
    }

    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        List<String> paramList = Collections.list(request.getParameterNames());
        for (String param : paramList) {
            configData.put(param, request.getParameter(param));
        }
    }
}
