package org.geektimes.interceptor;

import static org.geektimes.commons.util.ServiceLoaders.loadAsArray;

public interface InterceptorEnhancer {
    default <T> T enhance(T source) {
        return enhance(source, (Class<? super T>) source.getClass());
    }

    default <T> T enhance(T source, Class<? super T> type) {
        return enhance(source, type, loadAsArray(AnnotatedInterceptor.class));
    }

    default <T> T enhance(T source, Object... interceptors) {
        return enhance(source, (Class<? super T>) source.getClass(), interceptors);
    }

    <T> T enhance(T source, Class<? super T> type, Object... interceptors);
}
