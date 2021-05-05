package org.geektimes.configuration.microprofile.config.source.impl;

import org.geektimes.configuration.microprofile.config.source.MapBasedConfigSource;

import javax.servlet.FilterConfig;
import java.util.Enumeration;
import java.util.Map;

import static java.lang.String.format;

public class FilterConfigSource extends MapBasedConfigSource {
    private final FilterConfig filterConfig;

    public FilterConfigSource(FilterConfig filterConfig) {
        super(format("Filter[name:%s] Init Parameters", filterConfig.getFilterName()), 550);
        this.filterConfig = filterConfig;
    }

    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        Enumeration<String> parameterNames = filterConfig.getInitParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            configData.put(parameterName, filterConfig.getInitParameter(parameterName));
        }
    }
}
