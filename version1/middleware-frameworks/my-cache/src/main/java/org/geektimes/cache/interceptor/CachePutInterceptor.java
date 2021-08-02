package org.geektimes.cache.interceptor;

import org.geektimes.interceptor.AnnotatedInterceptor;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * Cache Interceptor
 */
@Interceptor
@NewCachePut
public class CachePutInterceptor extends AnnotatedInterceptor<NewCachePut> {
    private CachingProvider cachingProvider = Caching.getCachingProvider();
    private CacheManager cacheManager = cachingProvider.getCacheManager();

    @Override
    protected Object execute(InvocationContext context, NewCachePut cachePut) throws Throwable {
        String cacheName = cachePut.cacheName();
        Cache cache = getCache(cacheName);
        boolean afterInvocation = cachePut.afterInvocation();
        //The result of target method
        Object result = context.proceed();
        if (afterInvocation) {
            Object[] parameters = context.getParameters();
            Object key = parameters[0];
            Object value = parameters[1];
            cache.put(key, value);
        }
        return result;
    }

    private Cache getCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (null == cache) {
            cache = cacheManager.createCache(cacheName,
                    new MutableConfiguration<>().setTypes(Object.class, Object.class));
        }
        return cache;
    }
}
