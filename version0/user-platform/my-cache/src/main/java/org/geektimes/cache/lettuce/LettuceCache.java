package org.geektimes.cache.lettuce;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.geektimes.cache.AbstractCache;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.util.Iterator;

public class LettuceCache<K, V> extends AbstractCache<K, V> {
    private final StatefulRedisConnection<K, V> redisConnection;
    private final RedisCommands<K, V> syncCommands;

    public LettuceCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration, StatefulRedisConnection<K, V> redisConnection) {
        super(cacheManager, cacheName, configuration);
        this.redisConnection = redisConnection;
        this.syncCommands = redisConnection.sync();
    }

    @Override
    protected V doGet(K key) throws CacheException, ClassCastException {
        redisConnection.sync();
        return syncCommands.get(key);
    }

    @Override
    protected V doPut(K key, V value) throws CacheException, ClassCastException {
        redisConnection.sync();
        String result = syncCommands.set(key, value);
        System.out.println("doPut result from Lettuce: " + result);
        return value;
    }

    @Override
    protected V doRemove(K key) {
        V value = doGet(key);
        if (value != null) {
            syncCommands.del(key);
        }
        return value;
    }

    @Override
    protected void doClear() throws CacheException {
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

    @Override
    protected Iterator<Entry<K, V>> newIterator() {
        return null;
    }

}
