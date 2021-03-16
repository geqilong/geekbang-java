package org.geektimes.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OSPropertiesConfigSource implements ConfigSource {

    private final Map<String, String> properties;

    public OSPropertiesConfigSource() throws Exception {
        Map<String, String> envMap = System.getenv();
        this.properties = new HashMap<>();
        for (String propertyName : envMap.keySet()) {
            this.properties.put(propertyName, envMap.get(propertyName));
        }
    }

    @Override
    public Set<String> getPropertyNames() {
        return this.properties.keySet();
    }

    @Override
    public String getValue(String s) {
        return this.properties.get(s);
    }

    @Override
    public String getName() {
        return "OSProperties";
    }

}
