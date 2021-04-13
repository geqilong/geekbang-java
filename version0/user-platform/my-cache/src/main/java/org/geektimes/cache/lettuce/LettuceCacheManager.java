package org.geektimes.cache.lettuce;

import com.alibaba.fastjson.JSON;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.geektimes.cache.AbstractCacheManager;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Properties;


public class LettuceCacheManager extends AbstractCacheManager {
    private RedisClient redisClient;

    public LettuceCacheManager(CachingProvider cachingProvider, URI uri, ClassLoader classLoader, Properties properties) {
        super(cachingProvider, uri, classLoader, properties);
        this.redisClient = RedisClient.create(uri.toString());
    }

    @Override
    protected void doClose() {
        if (redisClient != null) {
            redisClient.shutdownAsync();
        }
    }

    @Override
    protected <V, K, C extends Configuration<K, V>> Cache doCreateCache(String cacheName, C configuration) {
        StatefulRedisConnection<K, V> connection = redisClient.connect(new LettuceCodec(configuration.getKeyType(), configuration.getValueType()));
        return new LettuceCache(this, cacheName, configuration, connection);
    }

    static class LettuceCodec<K, V> implements RedisCodec<K, V> {
        private static final StringCodec STRING_CODEC = new StringCodec();
        private final Class<K> keyClass;
        private final Class<V> valueClass;

        LettuceCodec(Class<K> keyClass, Class<V> valueClass) {
            this.keyClass = keyClass;
            this.valueClass = valueClass;
        }

        @Override
        public K decodeKey(ByteBuffer bytes) {
            String decoded = STRING_CODEC.decodeKey(bytes);
            return JSON.parseObject(decoded, keyClass);
        }

        @Override
        public V decodeValue(ByteBuffer bytes) {
            String decoded = STRING_CODEC.decodeKey(bytes);
            return JSON.parseObject(decoded, valueClass);
        }

        @Override
        public ByteBuffer encodeKey(K key) {
            return STRING_CODEC.encodeKey(JSON.toJSONString(key));
        }

        @Override
        public ByteBuffer encodeValue(V value) {
            return STRING_CODEC.encodeKey(JSON.toJSONString(value));
        }
    }
}
