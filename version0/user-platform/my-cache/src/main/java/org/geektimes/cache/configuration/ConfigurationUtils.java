package org.geektimes.cache.configuration;

import javax.cache.configuration.*;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryListener;

/**
 * Cache {@link Configuration} utilities class
 */
public abstract class ConfigurationUtils {

    public static <K, V> MutableConfiguration<K, V> mutableConfiguration(Configuration<K, V> configuration) {
        MutableConfiguration mutableConfiguration = null;
        if (configuration instanceof MutableConfiguration) {
            mutableConfiguration = (MutableConfiguration) configuration;
        } else if (configuration instanceof CompleteConfiguration) {
            mutableConfiguration = (MutableConfiguration) configuration;
        } else {
            mutableConfiguration = new MutableConfiguration<K, V>()
                    .setTypes(configuration.getKeyType(), configuration.getValueType())
                    .setStoreByValue(configuration.isStoreByValue());
        }
        return mutableConfiguration;
    }

    public static <K, V> CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration(CacheEntryListener<? super K, ? super V> listener) {
        return cacheEntryListenerConfiguration(listener, null);
    }

    public static <K, V> CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration(CacheEntryListener<? super K, ? super V> listener,
                                                                                               CacheEntryEventFilter<? super K, ? super V> filter) {
        return cacheEntryListenerConfiguration(listener, filter, true);
    }

    public static <K, V> CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration(CacheEntryListener<? super K, ? super V> listener,
                                                                                               CacheEntryEventFilter<? super K, ? super V> filter,
                                                                                               boolean isOldValueRequired) {
        return cacheEntryListenerConfiguration(listener, filter, isOldValueRequired, true);
    }

    public static <K, V> CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration(CacheEntryListener<? super K, ? super V> listener,
                                                                                               CacheEntryEventFilter<? super K, ? super V> filter,
                                                                                               boolean isOldValueRequired,
                                                                                               boolean isSynchronous) {
        return new MutableCacheEntryListenerConfiguration<>(() -> listener, () -> filter, isOldValueRequired, isSynchronous);
    }

    public static <V, K> CompleteConfiguration<K, V> immutableConfiguration(Configuration<K, V> configuration) {
        return new ImmutableCompleteConfiguration(configuration);
    }

}
