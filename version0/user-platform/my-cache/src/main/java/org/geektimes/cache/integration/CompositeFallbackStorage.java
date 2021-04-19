package org.geektimes.cache.integration;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Composite multiple {@link FallbackStorage}s that instantiated by {@link ServiceLoader Java SPI}.
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0
 */
public class CompositeFallbackStorage extends AbstractFallbackStorage<Object, Object> {
    private static final ConcurrentHashMap<ClassLoader, List<FallbackStorage>> fallbackStorageCache = new ConcurrentHashMap<>();
    private final List<FallbackStorage> fallbackStorages;

    public CompositeFallbackStorage() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public CompositeFallbackStorage(ClassLoader classLoader) {
        super(Integer.MIN_VALUE);
        this.fallbackStorages = fallbackStorageCache.computeIfAbsent(classLoader, this::loadFallbackStorage);
    }

    private List<FallbackStorage> loadFallbackStorage(ClassLoader classLoader) {
        List<FallbackStorage> fallbackStorageList = new ArrayList<>();
        ServiceLoader<FallbackStorage> instances = ServiceLoader.load(FallbackStorage.class, classLoader);
        instances.forEach(element -> fallbackStorageList.add(element));
        if (fallbackStorageList.isEmpty()){
            throw new RuntimeException("Unable to proceed, for failing to load FallbackStorage implementations");
        }
        Collections.sort(fallbackStorageList, PRIORITY_COMPARATOR);
        return fallbackStorageList;
    }

    @Override
    public Object load(Object key) throws CacheLoaderException {
        Object value = null;
        for (FallbackStorage fallbackStorage : fallbackStorages) {
            value = fallbackStorage.load(key);
            if (value != null) {
                break;
            }
        }
        return value;
    }

    @Override
    public void write(Cache.Entry<?, ?> entry) throws CacheWriterException {
        fallbackStorages.forEach(fallbackStorage -> fallbackStorage.write(entry));
    }

    @Override
    public void delete(Object key) throws CacheWriterException {
        fallbackStorages.forEach(fallbackStorage -> fallbackStorage.delete(key));
    }

    @Override
    public void destroy() {
        fallbackStorages.forEach(FallbackStorage::destroy);
    }
}
