package org.geektimes.cache.event;

import javax.cache.event.CacheEntryEvent;
import java.util.EventListener;

public interface ConditionalCacheEntryEventListener<K, V> extends EventListener {
    void onEvent(CacheEntryEvent<? extends K, ? extends V> event);
}
