package org.geektimes.configuration.microprofile.config.source.impl;

import org.geektimes.configuration.microprofile.config.source.MapBasedConfigSource;

import java.util.Map;

public class DynamicConfigSource extends MapBasedConfigSource {
    private Map configData;

    public DynamicConfigSource() {
        super("Dynamic ConfigSource", 500);
    }

    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        this.configData = configData;
    }

    public void onUpdate(String data) {
        //异步更新
    }
}
