package org.geektimes.cache.configuration;

import javax.cache.configuration.*;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;

public class ImmutableCompleteConfiguration<K,V> implements CompleteConfiguration<K,V> {
    private final CompleteConfiguration<K,V> configuration;

    public ImmutableCompleteConfiguration(Configuration configuration) {
        final MutableConfiguration<K,V> completeConfiguration;
        if (configuration instanceof  CompleteConfiguration){
            CompleteConfiguration config = (CompleteConfiguration) configuration;
            completeConfiguration = new MutableConfiguration<>(config);
        } else {
            completeConfiguration = new MutableConfiguration<K,V>().
                    setTypes(configuration.getKeyType(),configuration.getValueType()).
                    setStoreByValue(configuration.isStoreByValue());
        }
        this.configuration = completeConfiguration;
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

    @Override
    public Iterable<CacheEntryListenerConfiguration<K, V>> getCacheEntryListenerConfigurations() {
        return configuration.getCacheEntryListenerConfigurations();
    }

    @Override
    public Factory<CacheLoader<K, V>> getCacheLoaderFactory() {
        return configuration.getCacheLoaderFactory();
    }

    @Override
    public Factory<CacheWriter<? super K, ? super V>> getCacheWriterFactory() {
        return configuration.getCacheWriterFactory();
    }

    @Override
    public Factory<ExpiryPolicy> getExpiryPolicyFactory() {
        return configuration.getExpiryPolicyFactory();
    }

    @Override
    public Class<K> getKeyType() {
        return configuration.getKeyType();
    }

    @Override
    public Class<V> getValueType() {
        return configuration.getValueType();
    }

    @Override
    public boolean isStoreByValue() {
        return configuration.isStoreByValue();
    }
}
