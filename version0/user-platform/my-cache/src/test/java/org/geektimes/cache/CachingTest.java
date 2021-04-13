package org.geektimes.cache;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.geektimes.cache.event.TestCacheEntryListener;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import java.net.URI;

import static org.geektimes.cache.configuration.ConfigurationUtils.cacheEntryListenerConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CachingTest {

    @Test
    public void testSampleInMemory() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager(URI.create("in-memory://localhost"), null);
        //Configure the cache
        MutableConfiguration<String, Integer> config = new MutableConfiguration<String, Integer>().setTypes(String.class, Integer.class);
        //Create the cache
        Cache<String, Integer> cache = cacheManager.createCache("mycache", config);
        //add listener
        cache.registerCacheEntryListener(cacheEntryListenerConfiguration(new TestCacheEntryListener<>()));

        //caches operation
        String key = "key";
        Integer value1 = 1;
        cache.put(key, value1);

        //update
        value1 = 2;
        cache.put(key, value1);

        Integer value2 = cache.get(key);
        assertEquals(value1, value2);
        cache.remove(key);
        assertNull(cache.get(key));
    }

    @Test
    public void testSampleRedis() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager(URI.create("redis://localhost:6379/2"), null);
        //Configure the cache
        MutableConfiguration<String, Integer> config = new MutableConfiguration<String, Integer>().setTypes(String.class, Integer.class);
        //Create the cache
        Cache<String, Integer> cache = cacheManager.createCache("rediscache", config);
        //add listener
        cache.registerCacheEntryListener(cacheEntryListenerConfiguration(new TestCacheEntryListener<>()));

        //caches operation
        String key = "key";
        Integer value1 = 1;
        cache.put(key, value1);

        //update
        value1 = 2;
        cache.put(key, value1);

        Integer value2 = cache.get(key);
        assertEquals(value1, value2);
        cache.remove(key);
        assertNull(cache.get(key));
    }

    @Test
    public void testLettuce() {
        RedisClient redisClient = RedisClient.create("redis://localhost:6379/0");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommands = connection.sync();
        syncCommands.set("key", "1");
        String value = syncCommands.get("key");
        System.out.println(value);
        assertEquals(value, "1");
        connection.close();
        redisClient.shutdown();
    }

    @Test
    public void testJedis() {
        JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
        try (Jedis jedis = pool.getResource()) {
            jedis.set("foo", "123");
            String foobar = jedis.get("foo");
            System.out.println(foobar);
        }
        pool.close();
    }
}
