package org.geektimes.cache.lettuce;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.geektimes.cache.AbstractCache;
import org.geektimes.cache.ExpirableEntry;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.io.Serializable;
import java.util.Set;

public class LettuceCache<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> {
    private final StatefulRedisConnection<K, V> redisConnection;
    private final RedisCommands<K, V> syncCommands;

    public LettuceCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration, StatefulRedisConnection<K, V> redisConnection) {
        super(cacheManager, cacheName, configuration);
        this.redisConnection = redisConnection;
        this.syncCommands = redisConnection.sync();
    }

    @Override
    protected void putEntry(ExpirableEntry<K, V> newEntry) throws CacheException, ClassCastException {
        String result = syncCommands.set(newEntry.getKey(), newEntry.getValue());
        System.out.println("doPut result from Lettuce: " + result);
    }

    @Override
    protected ExpirableEntry<K, V> getEntry(K key) throws CacheException, ClassCastException {
        V value = syncCommands.get(key);
        return ExpirableEntry.of(key, value);
    }

    @Override
    protected ExpirableEntry<K, V> removeEntry(K key) throws CacheException, ClassCastException {
        ExpirableEntry<K, V>  oldEntry = getEntry(key);
        syncCommands.del(key);
        return oldEntry;
    }

    @Override
    protected void clearEntries() throws CacheException {
        syncCommands.flushall();
    }

    @Override
    protected boolean containsEntry(K key) throws CacheException, ClassCastException {
        return syncCommands.exists(key) == 1;
    }

    @Override
    protected Set<K> keySet() {
        return null;
    }

    @Override
    protected void doClose() {
        if (syncCommands.isOpen()) {
            syncCommands.shutdown(true);
        }
        if (redisConnection.isOpen()) {
            redisConnection.close();
        }
    }
}
