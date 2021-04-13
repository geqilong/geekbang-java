package org.geektimes.cache;

import org.geektimes.cache.configuration.ConfigurationUtils;
import org.geektimes.cache.event.CacheEntryEventPublisher;
import org.geektimes.cache.event.GenericCacheEntryEvent;
import org.geektimes.cache.integration.CompositeFallbackStorage;
import org.geektimes.cache.integration.FallbackStorage;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import java.util.*;
import java.util.logging.Logger;

public abstract class AbstractCache<K, V> implements Cache<K, V> {
    protected final Logger logger = Logger.getLogger(getClass().getName());
    protected final CacheManager cacheManager;
    protected final String cacheName;
    protected final CompleteConfiguration<K, V> configuration;
    protected final FallbackStorage fallbackStorage;
    private final CacheEntryEventPublisher entryEventPublisher;
    private volatile boolean closed = false;

    protected AbstractCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration) {
        this.cacheManager = cacheManager;
        this.cacheName = cacheName;
        this.configuration = ConfigurationUtils.completeConfiguration(configuration);
        this.fallbackStorage = new CompositeFallbackStorage(cacheManager.getClassLoader());
        this.entryEventPublisher = new CacheEntryEventPublisher();
    }


    @Override
    public V get(K key) {
        assertNotClosed();
        assertNotNull(key, "key");
        V value = null;
        try {
            value = doGet(key);
        } catch (Throwable e) {
            logger.severe(e.getMessage());
        }
        return value;
    }

    protected abstract V doGet(K key) throws CacheException, ClassCastException;

    @Override
    public Map<K, V> getAll(Set<? extends K> keys) {
        // Keep the order of keys
        Map<K, V> result = new LinkedHashMap<>();
        for (K key : keys) {
            result.put(key, get(key));
        }
        return result;
    }

    @Override
    public boolean containsKey(K key) {
        assertNotClosed();
        assertNotNull(key, "key");
        return get(key) != null;
    }

    @Override
    public void loadAll(Set<? extends K> keys, boolean replaceExistingValues, CompletionListener completionListener) {
        assertNotClosed();
        // TODO
        throw new UnsupportedOperationException("This feature will be supported in the future");
    }

    @Override
    public void put(K key, V value) {
        assertNotClosed();
        assertNotNull(key, "key");
        assertNotNull(value, "value");
        try {
            V oldValue = doPut(key, value);
            if (null == oldValue) {
                publishCreatedEvent(key, value);
            } else {
                publishUpdatedEvent(key, oldValue, value);
            }
        } finally {
            writeIfWriteThrough(key, value);
        }
    }

    /**
     * Associates the specified value with the specified key in this Cache
     * (optional operation).  If the map previously contained a mapping for
     * the key, the old value is replaced by the specified value.  (A map
     * <tt>m</tt> is said to contain a mapping for a key <tt>k</tt> if and only
     * if {@link #containsKey(Object) m.containsKey(k)} would return
     * <tt>true</tt>.)
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     * <tt>null</tt> if there was no mapping for <tt>key</tt>.
     * (A <tt>null</tt> return can also indicate that the map
     * previously associated <tt>null</tt> with <tt>key</tt>,
     * if the implementation supports <tt>null</tt> values.)
     */
    protected abstract V doPut(K key, V value) throws CacheException, ClassCastException;

    @Override
    public V getAndPut(K key, V value) {
        V oldValue = get(key);
        put(key, value);
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        map.entrySet().forEach(entry -> put(entry.getKey(), entry.getValue()));
    }

    @Override
    public boolean putIfAbsent(K key, V value) {
        if (!containsKey(key)) {
            put(key, value);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(K key) {
        assertNotClosed();
        assertNotNull(key, "key");
        boolean removed = false;
        try {
            V oldValue = doRemove(key);
            removed = (oldValue != null);
            if (removed) {
                publishRemovedEvent(key, oldValue);
            }
        } finally {
            deleteIfWriteThrough(key);
        }
        return removed;
    }

    protected abstract V doRemove(K key);

    @Override
    public boolean remove(K key, V oldValue) {
        if (containsKey(key) && Objects.equals(get(key), oldValue)) {
            return true;
        }
        return false;
    }

    @Override
    public V getAndRemove(K key) {
        V oldValue = get(key);
        remove(key);
        return oldValue;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        assertNotNull(key, "oldValue");
        if (Objects.equals(get(key), oldValue)) {
            put(key, newValue);
            return true;
        }
        return false;
    }

    @Override
    public boolean replace(K key, V value) {
        if (containsKey(key)) {
            put(key, value);
            return true;
        }
        return false;
    }

    @Override
    public V getAndReplace(K key, V value) {
        V oldValue = get(key);
        if (oldValue != null) {
            put(key, value);
        }
        return oldValue;
    }

    @Override
    public void removeAll(Set<? extends K> keys) {
        keys.forEach(key -> remove(key));
    }

    @Override
    public void removeAll() {
        Iterator<Entry<K, V>> iterator = iterator();
        while (iterator.hasNext()) {
            Entry<K, V> entry = iterator.next();
            remove(entry.getKey());
        }
    }

    @Override
    public void clear() {
        assertNotClosed();
        doClear();
    }

    protected abstract void doClear() throws CacheException;


    @Override
    public <C extends Configuration<K, V>> C getConfiguration(Class<C> clazz) {
        Configuration<K, V> configuration = unwrap(clazz);
        return (C) ConfigurationUtils.completeConfiguration(configuration);
    }

    @Override
    public <T> T invoke(K key, EntryProcessor<K, V, T> entryProcessor, Object... arguments) throws EntryProcessorException {
        // TODO
        throw new UnsupportedOperationException("This feature will be supported in the future");
    }

    @Override
    public <T> Map<K, EntryProcessorResult<T>> invokeAll(Set<? extends K> keys, EntryProcessor<K, V, T> entryProcessor, Object... arguments) {
        // TODO
        throw new UnsupportedOperationException("This feature will be supported in the future");
    }

    @Override
    public String getName() {
        return cacheName;
    }

    @Override
    public CacheManager getCacheManager() {
        return cacheManager;
    }

    @Override
    public void close() {
        if (isClosed()) {
            return;
        }
        doClose();
        closed = true;
    }

    protected abstract void doClose();


    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        return null;
    }

    @Override
    public void registerCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        entryEventPublisher.registerCacheEntryListener(cacheEntryListenerConfiguration);
    }

    @Override
    public void deregisterCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        entryEventPublisher.deregisterCacheEntryListener(cacheEntryListenerConfiguration);
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        assertNotClosed();
        return newIterator();
    }

    protected abstract Iterator<Entry<K, V>> newIterator();


    private void publishCreatedEvent(K key, V value) {
        entryEventPublisher.publish(GenericCacheEntryEvent.createdEvent(this, key, value));
    }

    private void publishUpdatedEvent(K key, V oldValue, V value) {
        entryEventPublisher.publish(GenericCacheEntryEvent.updatedEvent(this, key, oldValue, value));
    }

    private void publishRemovedEvent(K key, V oldValue) {
        entryEventPublisher.publish(GenericCacheEntryEvent.removedEvent(this, key, oldValue));
    }

    private void publishExpiredEvent(K key, V oldValue) {
        entryEventPublisher.publish(GenericCacheEntryEvent.expiredEvent(this, key, oldValue));
    }

    private void writeIfWriteThrough(K key, V value) {
        if (configuration.isWriteThrough()) {
            fallbackStorage.write(EntryAdapter.of(key, value));
        }
    }

    private void deleteIfWriteThrough(K key) {
        if (configuration.isWriteThrough()) {
            fallbackStorage.delete(key);
        }
    }

    private void assertNotNull(Object object, String source) {
        if (object == null) {
            throw new NullPointerException(source + " must not be null!");
        }
    }

    private void assertNotClosed() {
        if (isClosed()) {
            throw new IllegalStateException("Current cache has been closed! No operation should be executed.");
        }
    }
}
