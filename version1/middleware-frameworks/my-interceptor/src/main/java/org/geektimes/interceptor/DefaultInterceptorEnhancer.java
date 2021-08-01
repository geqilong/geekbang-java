package org.geektimes.interceptor;

import org.geektimes.interceptor.cglib.CglibInterceptorEnhancer;
import org.geektimes.interceptor.jdk.DynamicProxyInterceptorEnhancer;

/**
 * Default {@link InterceptorEnhancer}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class DefaultInterceptorEnhancer implements InterceptorEnhancer {

    private final InterceptorEnhancer jdkProxyInterceptorEnhancer = new DynamicProxyInterceptorEnhancer();

    private final InterceptorEnhancer cglibInterceptorEnhancer = new CglibInterceptorEnhancer();

    @Override
    public <T> T enhance(T source, Class<? super T> type, Object... interceptors) {
        assertType(type);
        if (type.isInterface()) {
            return jdkProxyInterceptorEnhancer.enhance(source, type, interceptors);
        } else {
            return cglibInterceptorEnhancer.enhance(source, type, interceptors);
        }
    }

    private <T> void assertType(Class<? super T> type) {
        if (type.isAnnotation() || type.isEnum() || type.isPrimitive() || type.isArray()) {
            throw new IllegalArgumentException("The type must be an interface or a class!");
        }
    }
}