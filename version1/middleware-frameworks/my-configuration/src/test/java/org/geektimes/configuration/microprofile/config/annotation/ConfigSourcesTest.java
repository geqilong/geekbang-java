package org.geektimes.configuration.microprofile.config.annotation;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


@ConfigSource(ordinal = 100, resource = "classpath:/META-INF/custom.properties")
@ConfigSource(ordinal = 200, resource = "classpath:/META-INF/default.properties")
@ConfigSource(ordinal = 300, resource = "class:org.geektimes.configuration.microprofile.config.source.JavaSystemPropertiesConfigSource")
@ConfigSource(ordinal = 400, resource = "class:org.geektimes.configuration.microprofile.config.source.OperationSystemEnvironmentVariablesConfigSource")
// Or using @ConfigSources(value={...})
public class ConfigSourcesTest {
    private Logger logger = Logger.getLogger(getClass().getName());
    private static int compare(ConfigSource o1, ConfigSource o2) {
        return o2.ordinal() - o1.ordinal();
    }

    @Test
    public void testConfigSources() {
        ConfigSources configSources = getClass().getAnnotation(ConfigSources.class);
        ConfigSource[] configArr = configSources.value();
        List<ConfigSource> configSourceList = Arrays.asList(configArr.clone());
        configSourceList.sort(ConfigSourcesTest::compare);
        Map props = new HashMap<>();
        for (ConfigSource configSource : configSourceList) {
            props.putAll(getProperties(configSource));
        }
        System.out.println(props);
    }

    private Map getProperties(ConfigSource configSource) {
        String name = configSource.name();
        int ordinal = configSource.ordinal();
        String encoding = configSource.encoding();
        String resource = configSource.resource();

        if (StringUtils.isEmpty(resource))
            return new HashMap();

        if (resource.startsWith("classpath")) {
            URL resourceURL = null;
            ConfigSourceFactory configSourceFactory = null;
            try {
                resourceURL = new URL(resource);
                Class<? extends ConfigSourceFactory> configSourceFactoryClass = configSource.factory();
                if (ConfigSourceFactory.class.equals(configSourceFactoryClass)) {
                    configSourceFactoryClass = DefaultConfigSourceFactory.class;
                }

                configSourceFactory = configSourceFactoryClass.newInstance();
            } catch (MalformedURLException | InstantiationException | IllegalAccessException e) {
                logger.severe(e.getMessage());
                return new HashMap();
            }
            org.eclipse.microprofile.config.spi.ConfigSource source =
                    configSourceFactory.createConfigSource(name, ordinal, resourceURL, encoding);
            return source.getProperties();
        } else if (resource.startsWith("class")) {
            int index = resource.indexOf(":");
            String className = resource.substring(index + 1);
            Class clazz = null;
            try {
                clazz = getClass().getClassLoader().loadClass(className);
            } catch (ClassNotFoundException e) {
                logger.severe(e.getMessage());
                return new HashMap();
            }
            //org.eclipse.microprofile.config.spi.ConfigSource
            if (org.eclipse.microprofile.config.spi.ConfigSource.class.isAssignableFrom(clazz)) {
                try {
                    org.eclipse.microprofile.config.spi.ConfigSource source = (org.eclipse.microprofile.config.spi.ConfigSource) clazz.newInstance();
                    return source.getProperties();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.severe(e.getMessage());
                    return new HashMap();
                }
            } else {
                logger.severe("Not an instance of org.eclipse.microprofile.config.spi.ConfigSource.class");
            }
        }
        return new HashMap();
    }
}
