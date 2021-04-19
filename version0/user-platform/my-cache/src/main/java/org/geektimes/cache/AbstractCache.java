package org.geektimes.cache;

import org.geektimes.cache.configuration.ConfigurationUtils;
import org.geektimes.cache.event.CacheEntryEventPublisher;
import org.geektimes.cache.event.GenericCacheEntryEvent;
import org.geektimes.cache.integration.CompositeFallbackStorage;
import org.geektimes.cache.integration.FallbackStorage;
import org.geektimes.cache.management.CacheStatistics;
import org.geektimes.cache.management.DummyCacheStatistics;
import org.geektimes.cache.management.ManagementUtils;
import org.geektimes.cache.management.SimpleCacheStatistics;
import org.geektimes.cache.processor.MutableEntryAdapter;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.Factory;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.EventType;
import javax.cache.expiry.Duration;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import javax.cache.processor.MutableEntry;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.logging.Logger;


public abstract class AbstractCache<K, V> implements Cache<K, V> {
    protected final Logger logger = Logger.getLogger(getClass().getName());
    private final CacheManager cacheManager;
    private final String cacheName;
    private final CompleteConfiguration<K, V> configuration;
    private final ExpiryPolicy expiryPolicy;
    private final CacheWriter<K, V> cacheWriter;
    private final CacheLoader<K, V> cacheLoader;
    private final FallbackStorage fallbackStorage;
    private final CacheEntryEventPublisher entryEventPublisher;
    private final CacheStatistics cacheStatistics;
    private volatile boolean closed = false;
    private final Executor executor;

    protected AbstractCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration) {
        this.cacheManager = cacheManager;
        this.cacheName = cacheName;
        this.configuration = ConfigurationUtils.mutableConfiguration(configuration);
        this.expiryPolicy = resolveExpiryPolicy(this.configuration);
        this.fallbackStorage = new CompositeFallbackStorage(cacheManager.getClassLoader());
        this.cacheWriter = resolveCacheWriter(this.configuration);
        this.cacheLoader = resolveCacheLoader(this.configuration);
        this.entryEventPublisher = new CacheEntryEventPublisher();
        this.cacheStatistics = resolveCacheStatistics();
        this.executor = ForkJoinPool.commonPool();
        registerCacheEntryListenersFromConfiguration();
        ManagementUtils.registerCacheMXBeanIfRequired(this);
    }

    /**
     * Gets an entry from the cache.
     * <p>
     * If the cache is configured to use read-through, and get would return null
     * because the entry is missing from the cache, the Cache's {@link CacheLoader}
     * is called in an attempt to load the entry.
     * <p>
     * Current method calls the methods of {@link ExpiryPolicy}:
     * <ul>
     *     <li>No {@link ExpiryPolicy#getExpiryForCreation} ({@link #loadValue(Object, boolean) unless read-though caused a load)}</li>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForAccess}</li>
     *     <li>No {@link ExpiryPolicy#getExpiryForUpdate}</li>
     * </ul>
     *
     * @param key the key whose associated value is to be returned
     * @return the element, or null, if it does not exist.
     * @throws IllegalStateException if the cache is {@link #isClosed()}
     * @throws NullPointerException  if the key is null
     * @throws CacheException        if there is a problem fetching the value
     * @throws ClassCastException    if the implementation is configured to perform
     *                               runtime-type-checking, and the key or value
     *                               types are incompatible with those that have been
     *                               configured for the {@link Cache}
     */
    @Override
    public V get(K key) {
        assertNotClosed();
        assertNotNull(key, "key");
        ExpirableEntry<K, V> entry = null;
        long startTime = System.currentTimeMillis();
        try {
            entry = getEntry(key);
            if (handleExpiryPolicyForAccess(entry)) {
                return null;
            }
        } catch (Throwable e) {
            logger.severe(e.getMessage());
        } finally {
            cacheStatistics.cacheGets();
            cacheStatistics.cacheGetsTime(System.currentTimeMillis() - startTime);
        }
        if (entry == null && isReadThrough()) {
            return loadValue(key, true);
        }
        return getValue(entry);
    }


    private V loadValue(K key, boolean storeEntry) {
        V value = loadValue(key);
        if (storeEntry && value != null) {
            put(key, value);
        }
        return value;
    }

    private V loadValue(K key) {
        return getCacheLoader().load(key);
    }


    /**
     * Put the specified {@link javax.cache.Cache.Entry} into cache.
     *
     * @param entry The new instance of {@link Entry<K,V>} is created by {@link Cache}
     * @throws CacheException     if there is a problem doing the put
     * @throws ClassCastException if the implementation is configured to perform
     *                            runtime-type-checking, and the key or value
     *                            types are incompatible with those that have been
     *                            configured for the {@link Cache}
     */
    protected abstract void putEntry(ExpirableEntry<K, V> entry) throws CacheException, ClassCastException;

    /**
     * Get the {@link Cache.Entry} by the specified key
     *
     * @param key the key of {@link Entry}
     * @return the existed {@link Cache.Entry} associated with the given key
     * @throws CacheException     if there is a problem fetching the value
     * @throws ClassCastException if the implementation is configured to perform
     *                            runtime-type-checking, and the key or value
     *                            types are incompatible with those that have been
     *                            configured for the {@link Cache}
     */
    protected abstract ExpirableEntry<K, V> getEntry(K key) throws CacheException, ClassCastException;

    /**
     * Remove the specified {@link Cache.Entry} from cache.
     *
     * @param key the key of {@link Entry}
     * @return the removed {@link Cache.Entry} associated with the given key
     * @throws CacheException     if there is a problem doing the remove
     * @throws ClassCastException if the implementation is configured to perform
     *                            runtime-type-checking, and the key or value
     *                            types are incompatible with those that have been
     *                            configured for the {@link Cache}
     */
    protected abstract ExpirableEntry<K, V> removeEntry(K key) throws CacheException, ClassCastException;

    /**
     * Clear all {@link Cache.Entry enties} from cache.
     *
     * @throws CacheException if there is a problem doing the clear
     */
    protected abstract void clearEntries() throws CacheException;

    /**
     * Contains the {@link Cache.Entry} by the specified key or not.
     *
     * @param key the key of {@link Cache.Entry}
     * @return <code>true</code> if contains, or <code>false</code>
     * @throws CacheException     it there is a problem checking the mapping
     * @throws ClassCastException if the implementation is configured to perform
     *                            runtime-type-checking, and the key or value
     *                            types are incompatible with those that have been
     *                            configured for the {@link Cache}
     */
    protected abstract boolean containsEntry(K key) throws CacheException, ClassCastException;

    /**
     * Get all keys of {@link Cache.Entry} in the {@link Cache}
     *
     * @return the non-null read-only {@link Set}
     */
    protected abstract Set<K> keySet();


    /**
     * Gets a collection of entries from the {@link Cache}, returning them as
     * {@link Map} of the values associated with the set of keys requested.
     * <p>
     * If the cache is configured read-through, and a get for a key would
     * return null because an entry is missing from the cache, the Cache's
     * {@link CacheLoader} is called in an attempt to load the entry. If an
     * entry cannot be loaded for a given key, the key will not be present in
     * the returned Map.
     * <p>
     * Current method calls the methods of {@link ExpiryPolicy}:
     * <ul>
     *     <li>No {@link ExpiryPolicy#getExpiryForCreation} ({@link #loadValue(Object, boolean) unless read-though caused a load)}</li>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForAccess}</li>
     *     <li>No {@link ExpiryPolicy#getExpiryForUpdate}</li>
     * </ul>
     *
     * @param keys The keys whose associated values are to be returned.
     * @return A map of entries that were found for the given keys. Keys not found
     * in the cache are not in the returned map.
     * @throws NullPointerException  if keys is null or if keys contains a null
     * @throws IllegalStateException if the cache is {@link #isClosed()}
     * @throws CacheException        if there is a problem fetching the values
     * @throws ClassCastException    if the implementation is configured to perform
     *                               runtime-type-checking, and the key or value
     *                               types are incompatible with those that have been
     *                               configured for the {@link Cache}
     */
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
        return containsEntry(key);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Current method calls the methods of {@link ExpiryPolicy}:
     * <ul>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForCreation} (when the key is not associated with an existing value)</li>
     *     <li>No {@link ExpiryPolicy#getExpiryForAccess}</li>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForUpdate} (when the key is associated with a loaded value and the value
     *     should be replaced)</li>
     * </ul>
     */
    @Override
    public void loadAll(Set<? extends K> keys,
                        boolean replaceExistingValues,
                        CompletionListener completionListener) {
        assertNotClosed();
        //If no loader is configured for the cache, no objects will be loaded
        if (!configuration.isReadThrough()) {
            // FIXME: The specification does not mention that
            // CompletionListener#onCompletion() method should be invoked or not.
            completionListener.onCompletion();
            return;
        }

        //Asynchronously loads the specified entries into the cache using the configured CacheLoader for the given keys
        CompletableFuture.runAsync(() -> {
            //Implementations may choose to load multiple keys from the provided Set in parallel.
            //Iteration however must not occur in parallel, thus allow for non-thread-safe Sets to be used
            for (K key : keys) {
                // If an entry for a key already exists in the Cache, a value will be loaded
                // if and only if replaceExistingValues is true
                V value = loadValue(key, false);
                if (replaceExistingValues) {
                    replace(key, value);
                } else {
                    put(key, value);
                }
            }
        }, executor).whenComplete(new BiConsumer<Void, Throwable>() {
            @Override
            public void accept(Void unused, Throwable throwable) {
                // the CompletionListener may be null
                if (completionListener != null) {
                    // completed exceptionally
                    if (throwable instanceof Exception && throwable.getCause() instanceof Exception) {
                        completionListener.onException((Exception) throwable.getCause());
                    } else {
                        completionListener.onCompletion();
                    }
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     * <p>
     * Current method calls the methods of {@link ExpiryPolicy}:
     * <ul>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForCreation} (when the key is not associated with an existing value)</li>
     *     <li>No {@link ExpiryPolicy#getExpiryForAccess}</li>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForUpdate} (when the key is associated with an existing value)</li>
     * </ul>
     */
    @Override
    public void put(K key, V value) {
        assertNotClosed();
        Entry<K, V> entry = null;
        long startTime = System.currentTimeMillis();
        try {
            if (!containsKey(key)) {
                entry = createAndPutEntry(key, value);
            } else {
                entry = updateEntry(key, value);
            }
        } finally {
            cacheStatistics.cachePuts();
            cacheStatistics.cachePutsTime(System.currentTimeMillis() - startTime);
            writeEntryIfWriteThrough(entry);
        }
    }

    private ExpirableEntry<K, V> createAndPutEntry(K key, V value) {
        ExpirableEntry<K, V> newEntry = createEntry(key, value);
        if (handleExpiryPolicyForCreation(newEntry)) {
            // The new Cache.Entry is already expired and will not be added to the Cache.
            return null;
        }
        putEntry(newEntry);
        publishCreatedEvent(key, value);
        return newEntry;
    }

    private Entry<K, V> updateEntry(K key, V value) {
        ExpirableEntry<K, V> oldEntry = getEntry(key);
        V oldValue = getValue(oldEntry);
        oldEntry.setValue(value);
        //rewrite entry
        putEntry(oldEntry);
        publishUpdatedEvent(key, oldValue, value);
        if (handleExpiryPolicyForUpdate(oldEntry)) {
            return null;
        }
        return oldEntry;
    }

    private boolean handleExpiryPolicyForCreation(ExpirableEntry<K, V> newEntry) {
        return handleExpiryPolicy(newEntry, getExpiryForCreation(), false);
    }

    private boolean handleExpiryPolicyForAccess(ExpirableEntry<K, V> entry) {
        return handleExpiryPolicy(entry, getExpiryForAccess(), true);
    }

    private boolean handleExpiryPolicyForUpdate(ExpirableEntry<K, V> oldEntry) {
        return handleExpiryPolicy(oldEntry, getExpiryForUpdate(), true);
    }

    /**
     * Handle {@link ExpiryPolicy}
     *
     * @param entry               {@link ExpirableEntry}
     * @param duration            Creation : If a {@link Duration#ZERO} is returned the new Cache.Entry is considered
     *                            to be already expired and will not be added to the Cache.
     *                            Access or Update : If a {@link Duration#ZERO} is returned a Cache.Entry will be considered
     *                            immediately expired.
     *                            <code>null</code> will result in no change to the previously understood expiry
     *                            {@link Duration}.
     * @param removedExpiredEntry the expired {@link Cache.Entry} is removed or not.
     *                            If <code>true</code>, the {@link Cache.Entry} will be removed and publish an
     *                            {@link EventType#EXPIRED} of {@link CacheEntryEvent}.
     * @return <code>true</code> indicates the specified {@link Cache.Entry} should be expired.
     */
    private boolean handleExpiryPolicy(ExpirableEntry<K, V> entry, Duration duration, boolean removedExpiredEntry) {
        if (null == entry) {
            return false;
        }
        boolean expired = false;
        if (entry.isExpired()) {
            expired = true;
        } else if (duration != null) {
            if (duration.isZero()) {
                expired = true;
            } else {
                long timestamp = duration.getAdjustedTime(System.currentTimeMillis());
                // Update the timestamp
                entry.setTimestamp(timestamp);
            }
        }
        if (removedExpiredEntry && expired) {
            // remove Cache.Entry
            K key = entry.getKey();
            V value = entry.getValue();
            removeEntry(key);
            publishExpiredEvent(key, value);
        }
        return expired;
    }


    private ExpirableEntry<K, V> createEntry(K key, V value) {
        return ExpirableEntry.of(key, value);
    }

    /**
     * Associates the specified value with the specified key in this cache,
     * returning an existing value if one existed.
     * <p>
     * If the cache previously contained a mapping for
     * the key, the old value is replaced by the specified value.  (A cache
     * <tt>c</tt> is said to contain a mapping for a key <tt>k</tt> if and only
     * if {@link #containsKey(Object) c.containsKey(k)} would return
     * <tt>true</tt>.)
     * <p>
     * The previous value is returned, or null if there was no value associated
     * with the key previously.</p>
     * <p>
     * If the cache is configured write-through the associated
     * {@link CacheWriter#write(Cache.Entry)} method will be called.
     * </p>
     * <p>
     * Current method calls the methods of {@link ExpiryPolicy}:
     * <ul>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForCreation} (when the key is not associated with an existing value)</li>
     *     <li>No {@link ExpiryPolicy#getExpiryForAccess}</li>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForUpdate} (when the key is associated with an existing value)</li>
     * </ul>
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the value associated with the key at the start of the operation or
     * null if none was associated
     * @throws NullPointerException  if key is null or if value is null
     * @throws IllegalStateException if the cache is {@link #isClosed()}
     * @throws CacheException        if there is a problem doing the put
     * @throws ClassCastException    if the implementation is configured to perform
     *                               runtime-type-checking, and the key or value
     *                               types are incompatible with those that have been
     *                               configured for the {@link Cache}
     * @see #put(Object, Object)
     * @see #getAndReplace(Object, Object)
     * @see CacheWriter#write(Cache.Entry)
     */
    @Override
    public V getAndPut(K key, V value) {
        Entry<K, V> oldEntry = getEntry(key);
        put(key, value);
        return getValue(oldEntry);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Current method calls the methods of {@link ExpiryPolicy}:
     * <ul>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForCreation} (when the key is not associated with an existing value)</li>
     *     <li>No {@link ExpiryPolicy#getExpiryForAccess}</li>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForUpdate} (when the key is associated with an existing value)</li>
     * </ul>
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        map.entrySet().forEach(entry -> put(entry.getKey(), entry.getValue()));
    }

    /**
     * {@inheritDoc}
     * <p>
     * Current method calls the methods of {@link ExpiryPolicy}:
     * <ul>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForCreation} (when the key is not associated with an existing value)</li>
     *     <li>No {@link ExpiryPolicy#getExpiryForAccess}</li>
     *     <li>No {@link ExpiryPolicy#getExpiryForUpdate}</li>
     * </ul>
     */
    @Override
    public boolean putIfAbsent(K key, V value) {
        if (!containsKey(key)) {
            put(key, value);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Current method calls the methods of {@link ExpiryPolicy}:
     * <ul>
     *     <li>No {@link ExpiryPolicy#getExpiryForCreation}</li>
     *     <li>No {@link ExpiryPolicy#getExpiryForAccess}</li>
     *     <li>No {@link ExpiryPolicy#getExpiryForUpdate}</li>
     * </ul>
     */
    @Override
    public boolean remove(K key) {
        assertNotClosed();
        assertNotNull(key, "key");
        boolean removed;
        long startTime = System.currentTimeMillis();
        try {
            ExpirableEntry<K, V> oldEntry = removeEntry(key);
            removed = (oldEntry != null);
            if (removed) {
                publishRemovedEvent(key, oldEntry.getValue());
            }
        } finally {
            cacheStatistics.cacheRemovals();
            cacheStatistics.cacheRemovesTime(System.currentTimeMillis() - startTime);
            deleteIfWriteThrough(key);
        }
        return removed;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Current method calls the methods of {@link ExpiryPolicy}:
     * <ul>
     *     <li>No {@link ExpiryPolicy#getExpiryForCreation}</li>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForAccess} ((when the old value does not match the existing value)</li>
     *     <li>No {@link ExpiryPolicy#getExpiryForUpdate}</li>
     * </ul>
     */
    @Override
    public boolean remove(K key, V oldValue) {
        if (containsKey(key) && Objects.equals(get(key), oldValue)) {
            remove(key);
            return true;
        }
        return false;
    }

    /**
     * Atomically removes the entry for a key only if currently mapped to some
     * value.
     * <p>
     * This is equivalent to:
     * <pre><code>
     * if (cache.containsKey(key)) {
     *   V oldValue = cache.get(key);
     *   cache.remove(key);
     *   return oldValue;
     * } else {
     *   return null;
     * }
     * </code></pre>
     * except that the action is performed atomically.
     * <p>
     * If the cache is configured write-through the associated
     * {@link CacheWriter#delete(Object)} method will be called.
     * </p>
     *
     * <p>
     * Current method calls the methods of {@link ExpiryPolicy}:
     * <ul>
     *     <li>No {@link ExpiryPolicy#getExpiryForCreation}</li>
     *     <li>No {@link ExpiryPolicy#getExpiryForAccess}</li>
     *     <li>No {@link ExpiryPolicy#getExpiryForUpdate}</li>
     * </ul>
     *
     * @param key key with which the specified value is associated
     * @return the value if one existed or null if no mapping existed for this key
     * @throws NullPointerException  if the specified key or value is null.
     * @throws IllegalStateException if the cache is {@link #isClosed()}
     * @throws CacheException        if there is a problem during the remove
     * @throws ClassCastException    if the implementation is configured to perform
     *                               runtime-type-checking, and the key or value
     *                               types are incompatible with those that have been
     *                               configured for the {@link Cache}
     * @see CacheWriter#delete
     */
    @Override
    public V getAndRemove(K key) {
        Entry<K, V> oldEntry = getEntry(key);
        V oldValue = getValue(oldEntry);
        remove(key);
        return oldValue;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Current method calls the methods of {@link ExpiryPolicy}:
     * <ul>
     *     <li>No {@link ExpiryPolicy#getExpiryForCreation}</li>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForAccess} (when the old value does not match the existing value)</li>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForUpdate} (when value is replaced)</li>
     * </ul>
     */
    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        assertNotNull(oldValue, "oldValue");
        if (containsKey(key) && Objects.equals(get(key), oldValue)) {
            put(key, newValue);
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Current method calls the methods of {@link ExpiryPolicy}:
     * <ul>
     *     <li>No {@link ExpiryPolicy#getExpiryForCreation}</li>
     *     <li>No {@link ExpiryPolicy#getExpiryForAccess}</li>
     *     <li>No {@link ExpiryPolicy#getExpiryForUpdate} (when the key is associated with an existing value)</li>
     * </ul>
     */
    @Override
    public boolean replace(K key, V value) {
        if (containsKey(key)) {
            put(key, value);
            return true;
        }
        return false;
    }

    /**
     * Atomically replaces the value for a given key if and only if there is a
     * value currently mapped by the key.
     * <p>
     * This is equivalent to
     * <pre><code>
     * if (cache.containsKey(key)) {
     *   V oldValue = cache.get(key);
     *   cache.put(key, value);
     *   return oldValue;
     * } else {
     *   return null;
     * }
     * </code></pre>
     * except that the action is performed atomically.
     * <p>
     * If the cache is configured write-through, and this method returns true,
     * the associated {@link CacheWriter#write(Cache.Entry)} method will be called.
     * </p>
     *
     * <p>
     * Current method calls the methods of {@link ExpiryPolicy}:
     * <ul>
     *     <li>No {@link ExpiryPolicy#getExpiryForCreation}</li>
     *     <li>No {@link ExpiryPolicy#getExpiryForAccess}</li>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForUpdate} (when the key is associated with an existing value)</li>
     * </ul>
     *
     * @param key   key with which the specified value is associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or
     * <tt>null</tt> if there was no mapping for the key.
     * @throws NullPointerException  if key is null or if value is null
     * @throws IllegalStateException if the cache is {@link #isClosed()}
     * @throws CacheException        if there is a problem during the replace
     * @throws ClassCastException    if the implementation is configured to perform
     *                               runtime-type-checking, and the key or value
     *                               types are incompatible with those that have been
     *                               configured for the {@link Cache}
     * @see java.util.concurrent.ConcurrentMap#replace(Object, Object)
     * @see CacheWriter#write
     */
    @Override
    public V getAndReplace(K key, V value) {
        V oldValue = get(key);
        if (oldValue != null) {
            put(key, value);
        }
        return oldValue;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Current method calls the methods of {@link ExpiryPolicy}:
     * <ul>
     *     <li>No {@link ExpiryPolicy#getExpiryForCreation}</li>
     *     <li>No {@link ExpiryPolicy#getExpiryForAccess}</li>
     *     <li>No {@link ExpiryPolicy#getExpiryForUpdate}</li>
     * </ul>
     */
    @Override
    public void removeAll(Set<? extends K> keys) {
        for (K key : keys) {
            remove(key);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Current method calls the methods of {@link ExpiryPolicy}:
     * <ul>
     *     <li>No {@link ExpiryPolicy#getExpiryForCreation}</li>
     *     <li>No {@link ExpiryPolicy#getExpiryForAccess}</li>
     *     <li>No {@link ExpiryPolicy#getExpiryForUpdate}</li>
     * </ul>
     */
    @Override
    public void removeAll() {
        removeAll(keySet());
    }

    @Override
    public void clear() {
        assertNotClosed();
        clearEntries();
        fallbackStorage.destroy();
    }

    protected final CompleteConfiguration<K, V> getConfiguration() {
        return this.configuration;
    }

    @Override
    public <C extends Configuration<K, V>> C getConfiguration(Class<C> clazz) {
        if (!Configuration.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("The class must be inherited of " + Configuration.class.getName());
        }
        return (C) ConfigurationUtils.immutableConfiguration(configuration);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Current method calls the methods of {@link ExpiryPolicy}:
     * <ul>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForCreation} (for the following cases:
     *     (1) setValue called and an entry did not exist for key before invoke was called.
     *     (2) if read-through enabled and getValue() is called and causes a new entry to be loaded for key)
     *     </li>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForAccess} (when getValue was called and no other mutations
     *     occurred during entry processor execution. note: Create, modify or remove take precedence over Access)
     *     </li>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForUpdate} (when setValue was called and the entry already existed
     *     before entry processor was called)</li>
     * </ul>
     */
    @Override
    public <T> T invoke(K key, EntryProcessor<K, V, T> entryProcessor, Object... arguments) throws EntryProcessorException {
        MutableEntry<K, V> mutableEntry = MutableEntryAdapter.of(key, this);
        return entryProcessor.process(mutableEntry, arguments);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Current method calls the methods of {@link ExpiryPolicy}:
     * <ul>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForCreation} (for the following cases:
     *     (1) setValue called and an entry did not exist for key before invoke was called.
     *     (2) if read-through enabled and getValue() is called and causes a new entry to be loaded for key)
     *     </li>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForAccess} (when getValue was called and no other mutations
     *     occurred during entry processor execution. note: Create, modify or remove take precedence over Access)
     *     </li>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForUpdate} (when setValue was called and the entry already existed
     *     before entry processor was called)</li>
     * </ul>
     */
    @Override
    public <T> Map<K, EntryProcessorResult<T>> invokeAll(Set<? extends K> keys, EntryProcessor<K, V, T> entryProcessor, Object... arguments) {
        // "resultMap" keeps the order of keys
        Map<K, EntryProcessorResult<T>> resultMap = new LinkedHashMap<>();
        for (K key : keys) {
            resultMap.put(key, new EntryProcessorResult<T>() {
                @Override
                public T get() throws EntryProcessorException {
                    return invoke(key, entryProcessor, arguments);
                }
            });
        }
        return resultMap;
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
    public final void close() {
        // Closing a Cache signals to the CacheManager that produced or owns the Cache
        // that it should no longer be managed.
        if (isClosed()) {
            return;
        }
        doClose();
        closed = true;
    }

    /**
     * Subclass could override this method.
     */
    protected void doClose() {
    }

    protected Duration getExpiryForCreation() {
        return getDuration(expiryPolicy::getExpiryForCreation);
    }

    protected Duration getExpiryForAccess() {
        return getDuration(expiryPolicy::getExpiryForAccess);
    }

    protected Duration getExpiryForUpdate() {
        return getDuration(expiryPolicy::getExpiryForUpdate);
    }

    private Duration getDuration(Supplier<Duration> durationSupplier) {
        Duration duration = null;
        try {
            duration = durationSupplier.get();
        } catch (Throwable ignored) {
            duration = Duration.ETERNAL;
        }
        return duration;
    }

    @Override
    public boolean isClosed() {
        return closed;
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

    @Override
    public void registerCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        entryEventPublisher.registerCacheEntryListener(cacheEntryListenerConfiguration);
    }

    @Override
    public void deregisterCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        entryEventPublisher.deregisterCacheEntryListener(cacheEntryListenerConfiguration);
    }

    /**
     * {@inheritDoc}
     * <p>
     * The ordering of iteration over entries is undefined.
     * <p>
     * During iteration, any entries that are removed will have their appropriate
     * CacheEntryRemovedListeners notified.
     * <p>
     * When iterating over a cache it must be assumed that the underlying
     * cache may be changing, with entries being added, removed, evicted
     * and expiring. {@link java.util.Iterator#next()} may therefore return
     * null.
     *
     * <p>
     * Current method calls the methods of {@link ExpiryPolicy}:
     * <ul>
     *     <li>No {@link ExpiryPolicy#getExpiryForCreation}</li>
     *     <li>Yes {@link ExpiryPolicy#getExpiryForAccess} (when an entry is visited by an iterator)</li>
     *     <li>No {@link ExpiryPolicy#getExpiryForUpdate}</li>
     * </ul>
     *
     * @throws IllegalStateException if the cache is {@link #isClosed()}
     */
    @Override
    public Iterator<Entry<K, V>> iterator() {
        assertNotClosed();
        List<Entry<K, V>> entries = new LinkedList<>();
        for (K key : keySet()) {
            V value = get(key);
            entries.add(ExpirableEntry.of(key, value));
        }
        return entries.iterator();
    }

    public void registerCacheEntryListenersFromConfiguration() {
        this.configuration.getCacheEntryListenerConfigurations().forEach(this::registerCacheEntryListener);
    }

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

    private void writeEntryIfWriteThrough(Entry<K, V> entry) {
        if (entry != null && isWriteThrough()) {
            getCacheWriter().write(entry);
        }
    }

    private void deleteIfWriteThrough(K key) {
        if (isWriteThrough()) {
            getCacheWriter().delete(key);
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

    private ExpiryPolicy resolveExpiryPolicy(CompleteConfiguration<K, V> configuration) {
        Factory<ExpiryPolicy> expiryPolicyFactory = configuration.getExpiryPolicyFactory();
        if (expiryPolicyFactory == null) {
            expiryPolicyFactory = EternalExpiryPolicy::new;
        }
        return expiryPolicyFactory.create();
    }


    // Operations of CompleteConfiguration

    protected final boolean isReadThrough() {
        return configuration.isReadThrough();
    }

    protected final boolean isWriteThrough() {
        return configuration.isWriteThrough();
    }

    private CacheStatistics resolveCacheStatistics() {
        return isStatisticsEnabled() ? new SimpleCacheStatistics() : DummyCacheStatistics.INSTANCE;
    }

    protected final boolean isStatisticsEnabled() {
        return configuration.isStatisticsEnabled();
    }

    protected final boolean isManagementEnabled() {
        return configuration.isManagementEnabled();
    }

    public CacheWriter<K, V> getCacheWriter() {
        return cacheWriter;
    }

    public CacheLoader<K, V> getCacheLoader() {
        return cacheLoader;
    }

    private CacheWriter<K, V> resolveCacheWriter(CompleteConfiguration<K, V> configuration) {
        Factory<CacheWriter<? super K, ? super V>> cacheWriterFactory = configuration.getCacheWriterFactory();
        CacheWriter<K, V> cacheWriter = null;
        if (cacheWriterFactory != null) {
            cacheWriter = (CacheWriter<K, V>) cacheWriterFactory.create();
        }
        if (cacheWriter == null) {
            cacheWriter = this.fallbackStorage;
        }
        return cacheWriter;
    }

    private CacheLoader<K, V> resolveCacheLoader(CompleteConfiguration<K, V> configuration) {
        Factory<CacheLoader<K, V>> cacheLoaderFactory = configuration.getCacheLoaderFactory();
        CacheLoader<K, V> cacheLoader = null;
        if (cacheLoaderFactory != null) {
            cacheLoader = cacheLoaderFactory.create();
        }
        if (cacheLoader == null) {
            cacheLoader = this.fallbackStorage;
        }
        return cacheLoader;
    }

    private static <K, V> V getValue(Entry<K, V> entry) {
        return entry == null ? null : entry.getValue();
    }
}
