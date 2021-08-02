package org.geektimes.cache;

import org.geektimes.cache.interceptor.NewCachePut;

public interface DataRepository {
    @NewCachePut(cacheName = "simpleCache")
    boolean create(String name, Object value);
    boolean remove(String name);
    Object get(String name);
}
