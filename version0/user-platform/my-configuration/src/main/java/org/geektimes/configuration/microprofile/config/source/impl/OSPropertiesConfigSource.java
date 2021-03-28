package org.geektimes.configuration.microprofile.config.source.impl;

import org.geektimes.configuration.microprofile.config.source.MapBasedConfigSource;

import java.util.Map;

public class OSPropertiesConfigSource extends MapBasedConfigSource {

    public OSPropertiesConfigSource() {
        super("OS Properties", 300);
    }

    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        configData.putAll(System.getenv());
    }

}
