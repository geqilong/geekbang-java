package org.geektimes.cache.redis;

import org.geektimes.cache.AbstractCache;
import org.geektimes.cache.ExpirableEntry;
import org.geektimes.cache.serialization.FastJsonParser;
import org.geektimes.cache.serialization.Parsable;
import redis.clients.jedis.Jedis;

import javax.cache.CacheException;
import javax.cache.configuration.Configuration;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class JedisCache<K extends Serializable, V extends Serializable> extends AbstractCache<K, V> implements Parsable<V> {
    private final Jedis jedis;
    protected final Parsable<V> parser;
    private final byte[] keyPrefixBytes;
    private final int keyPrefixBytesLength;
    private final Set<K> ALL_MY_KEYS = new HashSet<>(); // For clear
    private final Object LOCK = new Object();

    public JedisCache(JedisCacheManager jedisCacheManager, String cacheName, Configuration<K, V> configuration, Jedis jedis, Parsable parser) {
        super(jedisCacheManager, cacheName, configuration);
        this.jedis = jedis;
        this.parser = (parser == null ? new FastJsonParser() : parser);
        this.keyPrefixBytes = buildKeyPrefixBytes(cacheName);
        this.keyPrefixBytesLength = keyPrefixBytes.length;
    }

    @Override
    protected void putEntry(ExpirableEntry<K, V> newEntry) throws CacheException, ClassCastException {
        byte[] keyBytes = serialize(newEntry.getKey());
        byte[] valueBytes = serialize(newEntry.getValue());
        jedis.set(keyBytes, valueBytes);
        synchronized (LOCK) {
            ALL_MY_KEYS.add(newEntry.getKey());
        }
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
        synchronized (LOCK) {
            ALL_MY_KEYS.remove(key);
        }
        byte[] keyBytes = serialize(key);
        ExpirableEntry<K, V> oldEntry = getEntry(key);
        jedis.del(keyBytes);
        return oldEntry;
    }

    @Override
    protected void clearEntries() throws CacheException {
        synchronized (LOCK) {
            ALL_MY_KEYS.stream().forEach(key -> removeEntry(key));
        }
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

    private byte[] buildKeyPrefixBytes(String cacheName) {
        StringBuilder keyPrefixBuilder = new StringBuilder("JedisCache-").append(cacheName).append(":");
        return keyPrefixBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }

    /*private byte[] getKeyBytes(Object key) {
        byte[] suffixBytes = serialize(key);
        int suffixBytesLength = suffixBytes.length;
        byte[] bytes = new byte[keyPrefixBytesLength + suffixBytesLength];
        System.arraycopy(keyPrefixBytes, 0, bytes, 0, keyPrefixBytesLength);
        System.arraycopy(suffixBytes, 0, bytes, keyPrefixBytesLength, suffixBytesLength);
        return bytes;
    }*/

    @Override
    public byte[] serialize(Object obj) {
        return parser.serialize(obj);
    }

    @Override
    public V deserialize(byte[] valueBytes, Class<V> clazz) {
        return parser.deserialize(valueBytes, clazz);
    }

}
