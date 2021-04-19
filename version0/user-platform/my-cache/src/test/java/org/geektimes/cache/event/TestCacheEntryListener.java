package org.geektimes.cache.event;

import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.event.*;

public class TestCacheEntryListener<K, V> implements CacheEntryCreatedListener<K, V>,
                                                     CacheEntryUpdatedListener<K, V>,
                                                     CacheEntryExpiredListener<K, V>,
                                                     CacheEntryRemovedListener<K, V>,
                                                     CacheEntryListenerConfiguration<K, V> {
    private CacheEntryEvent<K,V> cacheEntryEvent;
    private boolean oldValueRequired = true;
    private boolean synchronous = true;

    public TestCacheEntryListener() {
    }

    @Override
    public void onCreated(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
        handleEvents("onCreated", cacheEntryEvents);
    }

    @Override
    public void onExpired(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
        handleEvents("onExpired", cacheEntryEvents);
    }

    @Override
    public void onRemoved(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
        handleEvents("onRemoved", cacheEntryEvents);
    }

    @Override
    public void onUpdated(Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) throws CacheEntryListenerException {
        handleEvents("onUpdated", cacheEntryEvents);
    }

    private void handleEvents(String source, Iterable<CacheEntryEvent<? extends K, ? extends V>> cacheEntryEvents) {
        cacheEntryEvents.forEach(event -> handleEvent(source, event));
    }

    private void handleEvent(String source, CacheEntryEvent<? extends K, ? extends V> event) {
        this.cacheEntryEvent = (CacheEntryEvent<K, V>) event;
        System.out.printf("[Thread : %s] %s - %s\n", Thread.currentThread().getName(), source, event);
    }


    @Override
    public Factory<CacheEntryListener<? super K, ? super V>> getCacheEntryListenerFactory() {
        return new Factory<CacheEntryListener<? super K, ? super V>>() {
            @Override
            public CacheEntryListener<? super K, ? super V> create() {
                TestCacheEntryListener<K, V> testCacheEntryListener = TestCacheEntryListener.this;
                return testCacheEntryListener;
            }
        };
    }

    @Override
    public boolean isOldValueRequired() {
        return oldValueRequired;
    }

    @Override
    public Factory<CacheEntryEventFilter<? super K, ? super V>> getCacheEntryEventFilterFactory() {
        return new Factory<CacheEntryEventFilter<? super K, ? super V>>() {
            @Override
            public CacheEntryEventFilter<? super K, ? super V> create() {
                return new CacheEntryEventFilter<K, V>() {
                    @Override
                    public boolean evaluate(CacheEntryEvent<? extends K, ? extends V> event) throws CacheEntryListenerException {
                        return true;
                    }
                };
            }
        };
    }

    @Override
    public boolean isSynchronous() {
        return synchronous;
    }

    public CacheEntryEvent<K, V> getCacheEntryEvent() {
        CacheEntryEvent<K, V> event = cacheEntryEvent;
        this.cacheEntryEvent = null;
        return event;
    }


    public void setOldValueRequired(boolean oldValueRequired) {
        this.oldValueRequired = oldValueRequired;
    }

    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }
}
