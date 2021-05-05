package org.geektimes.cache.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.net.URI;

@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    public CacheManager getCacheManager() {
        JCacheCacheManager jCacheCacheManager = new JCacheCacheManager();
        CachingProvider cachingProvider = Caching.getCachingProvider();
        javax.cache.CacheManager jCacheManager = cachingProvider.getCacheManager(URI.create("redis://localhost:6379/0"), null);
        jCacheCacheManager.setCacheManager(jCacheManager);
        return jCacheCacheManager;
    }
}
