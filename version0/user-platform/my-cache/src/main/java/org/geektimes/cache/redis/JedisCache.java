package org.geektimes.cache.redis;

import org.geektimes.cache.AbstractCache;
import org.geektimes.cache.ExpirableEntry;
import org.geektimes.cache.serialization.FastJsonParser;
import org.geektimes.cache.serialization.Parsable;
import redis.clients.jedis.Jedis;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.io.Serializable;
import java.util.Set;

public class JedisCache<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> implements Parsable<V> {
    private final Jedis jedis;
    protected final Parsable<V> parser;

    public JedisCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration, Jedis jedis, Parsable parser) {
        super(cacheManager, cacheName, configuration);
        this.jedis = jedis;
        this.parser = (parser == null ? new FastJsonParser() : parser);
    }

    @Override
    protected void putEntry(ExpirableEntry<K, V> newEntry) throws CacheException, ClassCastException {
        byte[] keyBytes = serialize(newEntry.getKey());
        byte[] valueBytes = serialize(newEntry.getValue());
        jedis.set(keyBytes, valueBytes);
    }

    @Override
    protected ExpirableEntry<K, V> getEntry(K key) throws CacheException, ClassCastException {
        byte[] keyBytes = serialize(key);
        byte[] valueBytes = jedis.get(keyBytes);
        V value = deserialize(valueBytes, getConfiguration().getValueType());
        return ExpirableEntry.of(key, value);
    }

    @Override
    protected ExpirableEntry<K, V> removeEntry(K key) throws CacheException, ClassCastException {
        byte[] keyBytes = serialize(key);
        ExpirableEntry<K, V> oldEntry = getEntry(key);
        jedis.del(keyBytes);
        return oldEntry;
    }

    @Override
    protected void clearEntries() throws CacheException {

    }

    @Override
    protected boolean containsEntry(K key) throws CacheException, ClassCastException {
        byte[] keyBytes = serialize(key);
        return jedis.exists(keyBytes);
    }

    @Override
    protected Set<K> keySet() {
        return null;
    }

    @Override
    protected void doClose() {
        if (this.jedis.isConnected()) {
            this.jedis.close();
        }
    }

    @Override
    public byte[] serialize(Object obj) {
        return parser.serialize(obj);
    }

    @Override
    public V deserialize(byte[] valueBytes, Class<V> clazz) {
        return parser.deserialize(valueBytes, clazz);
    }

}
