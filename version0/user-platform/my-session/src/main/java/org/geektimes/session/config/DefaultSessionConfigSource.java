package org.geektimes.session.config;

import org.geektimes.configuration.microprofile.config.source.MapBasedConfigSource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

public class DefaultSessionConfigSource extends MapBasedConfigSource {
    public static final String RESOURCE_NAME = "META-INF/session-defaults.properties";

    private final ClassLoader classLoader;

    public DefaultSessionConfigSource(ClassLoader classLoader) {
        super("Session Defaults", Integer.MIN_VALUE);
        this.classLoader = classLoader;

    }

    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        URL resource = classLoader.getResource(RESOURCE_NAME);
        try (InputStream inputStream = resource.openStream();
             Reader reader = new InputStreamReader(inputStream, "UTF-8")) {
            Properties properties = new Properties();
            properties.load(reader);
            configData.putAll(properties);
            properties.clear();
        }
    }
}
