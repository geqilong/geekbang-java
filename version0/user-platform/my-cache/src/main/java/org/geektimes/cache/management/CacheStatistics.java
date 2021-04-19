package org.geektimes.cache.management;

/**
 * Cache Statistics
 */
public interface CacheStatistics {
    CacheStatistics reset();

    CacheStatistics cacheHits();

    CacheStatistics cacheGets();

    CacheStatistics cachePuts();

    CacheStatistics cacheRemovals();

    CacheStatistics cacheEvictions();

    CacheStatistics cacheGetsTime(long costTime);

    CacheStatistics cachePutsTime(long costTime);

    CacheStatistics cacheRemovesTime(long costTime);
}
