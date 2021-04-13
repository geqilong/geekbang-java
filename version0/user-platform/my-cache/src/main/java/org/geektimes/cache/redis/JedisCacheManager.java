package org.geektimes.cache.redis;

import org.geektimes.cache.AbstractCacheManager;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Properties;

public class JedisCacheManager extends AbstractCacheManager {
    private JedisPool jedisPool;

    public JedisCacheManager(CachingProvider cachingProvider, URI uri, ClassLoader classLoader, Properties properties) {
        super(cachingProvider, uri, classLoader, properties);
        this.jedisPool = new JedisPool(uri);
    }

    @Override
    protected void doClose() {
        if (!jedisPool.isClosed()) {
            jedisPool.close();
        }
    }

    @Override
    protected <V, K, C extends Configuration<K, V>> Cache doCreateCache(String cacheName, C configuration) {
        Jedis jedis = jedisPool.getResource();
        return new JedisCache(this, cacheName, configuration, jedis, null);
    }
}
