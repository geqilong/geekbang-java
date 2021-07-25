package org.geektimes.interceptor.cglib;

import net.sf.cglib.proxy.Enhancer;

import javax.interceptor.Interceptor;

/**
 * {@link Interceptor @Interceptor} enhancer by CGLIB
 */
public class InterceptorEnhancer {

    public Object enhance(Object target, Object... interceptors) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(new MethodInterceptorAdapter(target, interceptors));
        return enhancer.create();
    }
}
