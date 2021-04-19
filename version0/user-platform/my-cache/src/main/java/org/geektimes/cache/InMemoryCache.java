package org.geektimes.cache;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InMemoryCache<K, V> extends AbstractCache<K, V> {
    private final Map<K, ExpirableEntry<K, V>> cache;

    public InMemoryCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration) {
        super(cacheManager, cacheName, configuration);
        this.cache = new HashMap<>();
    }

    @Override
    protected void putEntry(ExpirableEntry<K, V> newEntry) throws CacheException, ClassCastException {
        K key = newEntry.getKey();
        cache.put(key, newEntry);
    }

    @Override
    protected ExpirableEntry<K, V> getEntry(K key) throws CacheException, ClassCastException {
        return cache.get(key);
    }

    @Override
    protected ExpirableEntry<K, V> removeEntry(K key) throws CacheException, ClassCastException {
        return cache.remove(key);
    }

    @Override
    protected void clearEntries() throws CacheException {
        cache.clear();
    }

    @Override
    protected boolean containsEntry(K key) throws CacheException, ClassCastException {
        return cache.containsKey(key);
    }

    @Override
    protected Set<K> keySet() {
        return cache.keySet();
    }

    @Override
    protected void doClose() {
        cache.clear();
    }

}
