package org.geektimes.cache.event;

import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.event.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import static java.util.Arrays.asList;

/**
 * The adapter of {@link ConditionalCacheEntryEventListener} based on {@link CacheEntryListenerConfiguration}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see CacheEntryListenerConfiguration
 * @since 1.0
 */
public class CacheEntryEventListenerAdapter<K, V> implements ConditionalCacheEntryEventListener<K, V> {

    private static List<Object> eventTypesAndHandleMethodNames = asList(
            EventType.CREATED, "onCreated",
            EventType.UPDATED, "onUpdated",
            EventType.EXPIRED, "onExpired",
            EventType.REMOVED, "onRemoved"
    );

    private final CacheEntryListenerConfiguration<K, V> configuration;
    private final CacheEntryListener<? super K, ? super V> cacheEntryListener;
    private final CacheEntryEventFilter<? super K, ? super V> cacheEntryEventFilter;
    private final Map<EventType, Method> eventTypeMethods;
    private final Executor executor;

    public CacheEntryEventListenerAdapter(CacheEntryListenerConfiguration<K, V> configuration) {
        this.configuration = configuration;
        this.cacheEntryEventFilter = getCacheEntryEventFilter(configuration);
        this.cacheEntryListener = configuration.getCacheEntryListenerFactory().create();
        this.eventTypeMethods = determineEventTypeMethods(cacheEntryListener);
        this.executor = getExecutor(configuration);
    }

    private Map<EventType, Method> determineEventTypeMethods(CacheEntryListener<? super K, ? super V> cacheEntryListener) {
        Map<EventType, Method> eventTypeMethods = new HashMap<>(EventType.values().length);
        Class<?> cacheEntryListenerClass = cacheEntryListener.getClass();
        for (int i = 0; i < eventTypesAndHandleMethodNames.size(); ) {
            EventType eventType = (EventType) eventTypesAndHandleMethodNames.get(i++);
            String handleMethodName = (String) eventTypesAndHandleMethodNames.get(i++);
            try {
                Method handleMethod = cacheEntryListenerClass.getMethod(handleMethodName, Iterable.class);
                if (handleMethod != null) {
                    eventTypeMethods.put(eventType, handleMethod);
                }
            } catch (NoSuchMethodException ignored) {
            }
        }
        return Collections.unmodifiableMap(eventTypeMethods);
    }

    private CacheEntryEventFilter<? super K, ? super V> getCacheEntryEventFilter(CacheEntryListenerConfiguration<K, V> configuration) {
        Factory<CacheEntryEventFilter<? super K, ? super V>> factory = configuration.getCacheEntryEventFilterFactory();
        CacheEntryEventFilter<? super K, ? super V> filter = null;
        if (factory != null) {
            filter = factory.create();
        }
        if (filter == null) {
            filter = new CacheEntryEventFilter<K, V>() {
                @Override
                public boolean evaluate(CacheEntryEvent<? extends K, ? extends V> event) throws CacheEntryListenerException {
                    return true;
                }
            };
        }
        return filter;
    }


    private Executor getExecutor(CacheEntryListenerConfiguration<K, V> configuration) {
        Executor executor = null;
        if (configuration.isSynchronous()) {
            executor = new Executor() {
                @Override
                public void execute(Runnable command) {
                    command.run();
                }
            };
        } else {
            executor = ForkJoinPool.commonPool();
        }
        return executor;
    }

    @Override
    public int hashCode() {
        return configuration.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CacheEntryEventListenerAdapter)) {
            return false;
        }
        CacheEntryEventListenerAdapter another = (CacheEntryEventListenerAdapter) obj;
        return this.configuration.equals(another.configuration);
    }

    @Override
    public void onEvent(CacheEntryEvent<? extends K, ? extends V> event) {
        if (!supports(event)) {
            return;
        }
        EventType eventType = event.getEventType();
        Method handleMethod = eventTypeMethods.get(eventType);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    handleMethod.invoke(cacheEntryListener, Collections.singleton(event));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new CacheEntryListenerException(e);
                }
            }
        });
    }

    private boolean supports(CacheEntryEvent<? extends K, ? extends V> event) {
        return supportsEventType(event) && cacheEntryEventFilter.evaluate(event);
    }

    private boolean supportsEventType(CacheEntryEvent<? extends K, ? extends V> event) {
        return getSupportedEventTypes().contains(event.getEventType());
    }

    private Set<EventType> getSupportedEventTypes() {
        return eventTypeMethods.keySet();
    }
}
