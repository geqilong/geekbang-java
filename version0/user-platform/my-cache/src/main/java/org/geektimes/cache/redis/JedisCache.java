package org.geektimes.cache.redis;

import org.geektimes.cache.AbstractCache;
import org.geektimes.cache.serialization.FastJsonParser;
import org.geektimes.cache.serialization.Parsable;
import redis.clients.jedis.Jedis;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.util.Iterator;

public class JedisCache<K, V> extends AbstractCache<K, V> implements Parsable<V> {
    private final Jedis jedis;
    protected final Parsable<V> parser;

    public JedisCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration, Jedis jedis, Parsable parser) {
        super(cacheManager, cacheName, configuration);
        this.jedis = jedis;
        this.parser = (parser == null ? new FastJsonParser() : parser);
    }

    @Override
    protected V doGet(K key) throws CacheException, ClassCastException {
        byte[] keyBytes = serialize(key);
        return doGet(keyBytes);
    }

    private V doGet(byte[] keyBytes) {
        byte[] valueBytes = jedis.get(keyBytes);
        V value = deserialize(valueBytes, configuration.getValueType());
        return value;
    }


    @Override
    protected V doPut(K key, V value) throws CacheException, ClassCastException {
        byte[] keyBytes = serialize(key);
        byte[] valueBytes = serialize(value);
        V oldValue = doGet(keyBytes);
        jedis.set(keyBytes, valueBytes);
        return oldValue;
    }

    @Override
    protected V doRemove(K key) {
        byte[] keyBytes = serialize(key);
        V oldValue = doGet(keyBytes);
        jedis.del(keyBytes);
        return oldValue;
    }

    @Override
    protected void doClear() throws CacheException {

    }

    @Override
    protected void doClose() {
        if (this.jedis.isConnected()) {
            this.jedis.close();
        }
    }

    @Override
    protected Iterator<Entry<K, V>> newIterator() {
        return null;
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
