package org.geektimes.cache;

import javax.cache.Cache;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class ExpirableEntry<K, V> implements Cache.Entry<K, V> {
    private final K key;
    private V value;
    private long timestamp;
    private boolean expired;

    public ExpirableEntry(K key, V value) {
        requireKeyNotNull(key);
        this.key = key;
        setValue(value);
        this.value = value;
        this.timestamp = Long.MAX_VALUE;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setValue(V value) {
        requireValueNotNull(value);
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        T value = null;
        try {
            value = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    public static <K, V> ExpirableEntry<K, V> of(Map.Entry<K, V> entry) {
        return new ExpirableEntry(entry.getKey(), entry.getValue());
    }

    public static <K, V> ExpirableEntry<K, V> of(K key, V value) {
        return new ExpirableEntry(key, value);
    }

    public void requireKeyNotNull(K key) {
        requireNonNull(key, "The key must not be null");
    }

    public void requireValueNotNull(V key) {
        requireNonNull(key, "The value must not be null");
    }

    public void requireOldValueNotNull(V key) {
        requireNonNull(key, "The old value must not be null");
    }

    @Override
    public String toString() {
        return "ExpirableEntry{" +
                "key=" + key +
                ", value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }


    public boolean isExpired() {
        return System.currentTimeMillis() > timestamp;
    }

}
