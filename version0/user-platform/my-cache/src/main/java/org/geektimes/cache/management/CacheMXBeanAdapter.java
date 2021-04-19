package org.geektimes.cache.management;

import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;
import javax.cache.management.CacheMXBean;

import static java.util.Objects.requireNonNull;

public class CacheMXBeanAdapter implements CacheMXBean {

    private final CompleteConfiguration configuration;

    public CacheMXBeanAdapter(CompleteConfiguration<?, ?> configuration) {
        requireNonNull(configuration, "The argument 'configuration' must not be null");
        this.configuration = configuration;
    }

    @Override
    public boolean isReadThrough() {
        return configuration.isReadThrough();
    }

    @Override
    public boolean isWriteThrough() {
        return configuration.isWriteThrough();
    }

    @Override
    public boolean isStatisticsEnabled() {
        return configuration.isStatisticsEnabled();
    }

    @Override
    public boolean isManagementEnabled() {
        return configuration.isManagementEnabled();
    }

    public Iterable<CacheEntryListenerConfiguration> getCacheEntryListenerConfigurations() {
        return configuration.getCacheEntryListenerConfigurations();
    }

    public Factory<CacheLoader> getCacheLoaderFactory() {
        return configuration.getCacheLoaderFactory();
    }

    public Factory<CacheWriter> getCacheWriterFactory() {
        return configuration.getCacheWriterFactory();
    }

    public Factory<ExpiryPolicy> getExpiryPolicyFactory() {
        return configuration.getExpiryPolicyFactory();
    }

    @Override
    public String getKeyType() {
        return configuration.getKeyType().getName();
    }

    @Override
    public String getValueType() {
        return configuration.getValueType().getName();
    }

    @Override
    public boolean isStoreByValue() {
        return configuration.isStoreByValue();
    }
}