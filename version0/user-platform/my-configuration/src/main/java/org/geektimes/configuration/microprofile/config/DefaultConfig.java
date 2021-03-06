package org.geektimes.configuration.microprofile.config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;
import org.geektimes.configuration.microprofile.config.converter.Converters;
import org.geektimes.configuration.microprofile.config.source.ConfigSources;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.StreamSupport;

public class DefaultConfig implements Config {

    /**
     * 内部可变的集合，不要直接暴露在外面
     */
    private final ConfigSources configSources;
    private final Converters converters;

    public DefaultConfig(ConfigSources configSources, Converters converters) {
        this.configSources = configSources;
        this.converters = converters;
    }

    @Override
    public <T> T getValue(String propertyName, Class<T> propertyType) {
        ConfigValue confVal = getConfigValue(propertyName);
        if (null == confVal) {
            return null;
        }
        String propertyValue = confVal.getValue();
        //转换为目标类型
        Converter<T> converter = doGetConverter(propertyType);
        return converter == null ? null : converter.convert(propertyValue);
    }

    @Override
    public ConfigValue getConfigValue(String propertyName) {
        String propertyValue = null;
        ConfigSource configSource = null;
        for (ConfigSource cs : configSources) {
            configSource = cs;
            propertyValue = configSource.getValue(propertyName);
            if (propertyValue != null) {
                break;
            }
        }
        if (null == propertyValue) {
            return null;
        }
        return new DefaultConfigValue(propertyName, propertyValue,
                transformPropertyValue(propertyValue),
                configSource.getName(),
                configSource.getOrdinal()
        );
    }

    private String transformPropertyValue(String propertyValue) {
        return propertyValue;
    }

    private <T> Converter<T> doGetConverter(Class<T> propertyType) {
        List<Converter> converters = this.converters.getConverters(propertyType);
        return converters.isEmpty() ? null : converters.get(0);
    }

    @Override
    public <T> Optional<T> getOptionalValue(String s, Class<T> aClass) {
        T value = getValue(s, aClass);
        return Optional.ofNullable(value);
    }

    @Override
    public Iterable<String> getPropertyNames() {
        return StreamSupport.stream(configSources.spliterator(), false).
                map(ConfigSource::getPropertyNames).
                collect(LinkedHashSet::new, Set::addAll, Set::addAll);
    }

    @Override
    public Iterable<ConfigSource> getConfigSources() {
        return configSources;
    }

    @Override
    public <T> Optional<Converter<T>> getConverter(Class<T> forType) {
        Converter converter = doGetConverter(forType);
        return converter == null ? Optional.empty() : Optional.of(converter);
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        return null;
    }
}
