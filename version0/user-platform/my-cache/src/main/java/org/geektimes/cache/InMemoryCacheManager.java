package org.geektimes.cache;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Properties;

public class InMemoryCacheManager extends AbstractCacheManager {

    public InMemoryCacheManager(CachingProvider cachingProvider, URI uri, ClassLoader classLoader, Properties properties) {
        super(cachingProvider, uri, classLoader, properties);
    }

    @Override
    protected <V, K, C extends Configuration<K, V>> Cache doCreateCache(String cacheName, C configuration) {
        return new InMemoryCache(this, cacheName, configuration);
    }

    @Override
    protected void doClose() {
        //Do nothing?
    }
}
