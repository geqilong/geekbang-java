package org.geektimes.interceptor.jdk;


import org.geektimes.commons.reflect.util.ClassUtils;
import org.geektimes.interceptor.InterceptorEnhancer;

import javax.interceptor.Interceptor;
import java.lang.reflect.Proxy;

/**
 * * {@link Interceptor @Interceptor} enhancer by CGLIB
 */
public class DynamicProxyInterceptorEnhancer implements InterceptorEnhancer {

    @Override
    public <T> T enhance(T source, Class<? super T> type, Object... interceptors) {
        return (T) Proxy.newProxyInstance(ClassUtils.getClassLoader(),
                new Class[]{type},
                new InvocationHandlerAdapter(source, interceptors));
    }
}
