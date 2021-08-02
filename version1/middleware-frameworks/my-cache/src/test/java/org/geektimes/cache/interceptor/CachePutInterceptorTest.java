package org.geektimes.cache.interceptor;

import org.geektimes.cache.DataRepository;
import org.geektimes.cache.InMemoryDataRepository;
import org.geektimes.interceptor.DefaultInterceptorEnhancer;
import org.geektimes.interceptor.InterceptorEnhancer;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CachePutInterceptorTest {
    private DataRepository dataRepository = new InMemoryDataRepository();
    private InterceptorEnhancer enhancer = new DefaultInterceptorEnhancer();
    private CachingProvider cachingProvider = Caching.getCachingProvider();
    private CacheManager cacheManager = cachingProvider.getCacheManager();

    @Test
    public void test() {
        DataRepository repository = enhancer.enhance(dataRepository, DataRepository.class, new CachePutInterceptor());
        assertTrue(repository.create("A", 1));
        Cache cache = cacheManager.getCache("simpleCache");
        assertEquals(repository.get("A"), cache.get("A"));
    }
}
