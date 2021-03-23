package org.geektimes.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.geektimes.configuration.microprofile.config.source.impl.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ConfigSources implements Iterable<ConfigSource> {

    private boolean addedDefaultConfigSources;
    private boolean addedDiscoveredConfigSources;
    private List<ConfigSource> configSources = new LinkedList<>();
    private ClassLoader classLoader;

    public ConfigSources(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public boolean isAddedDefaultConfigSources() {
        return addedDefaultConfigSources;
    }

    public boolean isAddedDiscoveredConfigSources() {
        return addedDiscoveredConfigSources;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void addDefaultSources() {
        if (addedDefaultConfigSources) {
            return;
        }
        addConfigSources(JavaSystemPropertiesConfigSource.class,
                DefaultResourceConfigResource.class,
                OSPropertiesConfigSource.class);
        this.addedDefaultConfigSources = true;
    }

    public void addConfigSources(Class<? extends ConfigSource>... configSourceClasses) {
        addConfigSources(Stream.of(configSourceClasses).map(this::newInstance).toArray(ConfigSource[]::new));
    }

    public void addConfigSources(ConfigSource... configSources) {
        addConfigSources(Arrays.asList(configSources));
    }

    private void addConfigSources(Iterable<ConfigSource> configSources) {
        configSources.forEach(this.configSources::add);
        this.configSources.sort(ConfigSourceOrdinalComparator.INSTANCE);
    }

    private ConfigSource newInstance(Class<? extends ConfigSource> configSourceClass) {
        ConfigSource instance;
        try {
            instance = configSourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        return instance;
    }

    public void addDiscoveredSources() {
        if (addedDiscoveredConfigSources) {
            return;
        }
        addConfigSources(ServiceLoader.load(ConfigSource.class, classLoader));
        addedDiscoveredConfigSources = true;
    }


    @Override
    public Iterator<ConfigSource> iterator() {
        return this.configSources.iterator();
    }

    @Override
    public void forEach(Consumer<? super ConfigSource> action) {
        this.configSources.forEach(new Consumer<ConfigSource>() {
            @Override
            public void accept(ConfigSource configSource) {
                action.accept(configSource);
            }
        });
    }

}
