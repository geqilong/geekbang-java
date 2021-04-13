package org.geektimes.cache.event;

import javax.cache.Cache;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.EventType;

import static java.util.Objects.requireNonNull;

public class GenericCacheEntryEvent<K, V> extends CacheEntryEvent<K, V> {
    private final K key;
    private final V oldValue;
    private final V value;

    /**
     * @param source
     * @param eventType
     * @param key
     * @param oldValue
     * @param value
     */
    public GenericCacheEntryEvent(Cache source, EventType eventType, K key, V oldValue, V value) {
        super(source, eventType);
        requireNonNull(key, "The key must not be null");
        requireNonNull(value, "The value must not be null");
        this.key = key;
        this.oldValue = oldValue;
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
        return getSource().getCacheManager().unwrap(clazz);
    }

    @Override
    public V getOldValue() {
        return oldValue;
    }

    @Override
    public boolean isOldValueAvailable() {
        return oldValue != null;
    }

    public static <K, V> CacheEntryEvent createdEvent(Cache cache, K key, V value) {
        return of(cache, EventType.CREATED, key, null, value);
    }

    public static <K, V> CacheEntryEvent updatedEvent(Cache cache, K key, V oldValue, V value) {
        return of(cache, EventType.UPDATED, key, oldValue, value);
    }

    public static <K, V> CacheEntryEvent removedEvent(Cache cache, K key, V oldValue) {
        return of(cache, EventType.REMOVED, key, oldValue, oldValue);
    }

    public static <K, V> CacheEntryEvent expiredEvent(Cache cache, K key, V oldValue) {
        return of(cache, EventType.EXPIRED, key, oldValue, oldValue);
    }

    public static <K, V> CacheEntryEvent<K, V> of(Cache source, EventType eventType, K key, V oldValue, V value) {
        return new GenericCacheEntryEvent<>(source, eventType, key, oldValue, value);
    }

    @Override
    public String toString() {
        return "GenericCacheEntryEvent{" +
                "key=" + key +
                ", oldValue=" + oldValue +
                ", value=" + value +
                ", eventType=" + getEventType() +
                ", source=" + getSource().getName() +
                '}';
    }
}
