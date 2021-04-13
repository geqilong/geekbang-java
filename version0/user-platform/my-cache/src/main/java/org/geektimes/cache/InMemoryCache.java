package org.geektimes.cache;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class InMemoryCache<K, V> extends AbstractCache<K, V> {
    private final Map<K, V> cache;

    public InMemoryCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration) {
        super(cacheManager, cacheName, configuration);
        this.cache = new HashMap<>();
    }

    @Override
    protected V doGet(K key) throws CacheException, ClassCastException {
        return cache.get(key);
    }

    @Override
    protected V doPut(K key, V value) throws CacheException, ClassCastException {
        return cache.put(key, value);
    }

    @Override
    protected V doRemove(K key) {
        return cache.remove(key);
    }

    @Override
    protected void doClear() throws CacheException {
        cache.clear();
    }

    @Override
    protected void doClose() {
        //DO nothing?
    }

    @Override
    protected Iterator<Entry<K, V>> newIterator() {
        return cache.entrySet().stream().map(EntryAdapter::of).iterator();
    }
}
