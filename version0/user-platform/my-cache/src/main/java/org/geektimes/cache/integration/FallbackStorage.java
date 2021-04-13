package org.geektimes.cache.integration;

import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;
import java.util.Comparator;

public interface FallbackStorage<K, V> extends CacheLoader<K, V>, CacheWriter<K, V> {
    Comparator<FallbackStorage> PRIORITY_COMPARATOR = new PriorityComparator();

    /**
     * Get the priority of current {@link FallbackStorage}.
     *
     * @return the value less, the priority higher
     */
    int getPriority();

    class PriorityComparator implements Comparator<FallbackStorage> {

        @Override
        public int compare(FallbackStorage o1, FallbackStorage o2) {
            return Integer.compare(o2.getPriority(), o1.getPriority());
        }
    }
}
