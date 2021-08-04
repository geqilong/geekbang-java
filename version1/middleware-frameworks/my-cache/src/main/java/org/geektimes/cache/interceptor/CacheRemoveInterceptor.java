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
 * Cache Remove Interceptor
 */
@Interceptor
@NewCacheRemove
public class CacheRemoveInterceptor extends AnnotatedInterceptor<NewCacheRemove> {
    private CachingProvider cachingProvider = Caching.getCachingProvider();
    private CacheManager cacheManager = cachingProvider.getCacheManager();

    public CacheRemoveInterceptor() {
        super();
        setPriority(600);
    }

    @Override
    protected Object execute(InvocationContext context, NewCacheRemove cacheRemove) throws Throwable {
        String cacheName = cacheRemove.cacheName();
        Cache cache = getCache(cacheName);
        boolean afterInvocation = cacheRemove.afterInvocation();
        //The result of target method
        Object result = context.proceed();
        if (afterInvocation) {
            Object[] parameters = context.getParameters();
            doCacheOps(cache, parameters);
        }
        return result;
    }

    private void doCacheOps(Cache cache, Object[] parameters) {
        Object key = parameters[0];
        cache.remove(key);
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
