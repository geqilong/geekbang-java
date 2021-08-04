package org.geektimes.cache;

import org.geektimes.cache.interceptor.NewCachePut;
import org.geektimes.cache.interceptor.NewCacheRemove;

public interface DataRepository {
    @NewCachePut(cacheName = "simpleCache")
    boolean create(String name, Object value);

    @NewCacheRemove(cacheName = "simpleCache")
    boolean remove(String name);

    Object get(String name);
}
