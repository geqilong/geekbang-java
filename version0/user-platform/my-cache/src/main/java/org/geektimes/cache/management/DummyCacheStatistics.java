package org.geektimes.cache.management;

/**
 * Dummy {@link CacheStatistics} that does nothing
 */
public class DummyCacheStatistics implements CacheStatistics {
    public static final CacheStatistics INSTANCE = new DummyCacheStatistics();

    @Override
    public CacheStatistics reset() {
        return this;
    }

    @Override
    public CacheStatistics cacheHits() {
        return this;
    }

    @Override
    public CacheStatistics cacheGets() {
        return this;
    }

    @Override
    public CacheStatistics cachePuts() {
        return this;
    }

    @Override
    public CacheStatistics cacheRemovals() {
        return this;
    }

    @Override
    public CacheStatistics cacheEvictions() {
        return this;
    }

    @Override
    public CacheStatistics cacheGetsTime(long costTime) {
        return this;
    }

    @Override
    public CacheStatistics cachePutsTime(long costTime) {
        return this;
    }

    @Override
    public CacheStatistics cacheRemovesTime(long costTime) {
        return this;
    }
}
