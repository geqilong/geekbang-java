package org.geektimes.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.*;

public class JavaSystemPropertiesConfigSource implements ConfigSource {

    /**
     * Java 系统属性最好通过本地变量保存，使用 Map 保存，尽可能运行期不去调整
     * -Dapplication.name=user-web
     */
    private final Map<String, String> properties;

    public JavaSystemPropertiesConfigSource() {
        this.properties = new HashMap<>();
        Properties javaSysProperties=  System.getProperties();
        for (String propertyName : javaSysProperties.stringPropertyNames()) {
            this.properties.put(propertyName, javaSysProperties.getProperty(propertyName));
        }
    }

    @Override
    public Set<String> getPropertyNames() {
        return this.properties.keySet();
    }

    @Override
    public String getValue(String propertyName) {
        return this.properties.get(propertyName);
    }

    @Override
    public String getName() {
        return "JavaSystemProperties";
    }
}
