package org.geektimes.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class MapBasedConfigSource implements ConfigSource {
    private final String name;
    private final int ordinal;
    private final Map<String, String> source;

    public MapBasedConfigSource(String name, int ordinal) {
        this.name = name;
        this.ordinal = ordinal;
        this.source = getProperties();
    }

    /**
     * 获取配置数据Map
     *
     * @return 不可变Map类型的配置数据
     */
    public final Map<String, String> getProperties() {
        Map<String, String> configData = new HashMap<>();
        try {
            prepareConfigData(configData);
        } catch (Throwable throwable) {
            throw new IllegalStateException("准备配置数据发生错误", throwable);
        }
        return Collections.unmodifiableMap(configData);
    }

    /**
     * 准备配置数据
     *
     * @param configData
     */
    protected abstract void prepareConfigData(Map configData) throws Throwable;

    @Override
    public Set<String> getPropertyNames() {
        return source.keySet();
    }

    @Override
    public String getValue(String propertyName) {
        return source.get(propertyName);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }
}
