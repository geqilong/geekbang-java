package org.geektimes.interceptor.jdk;

import org.apache.commons.lang.ClassUtils;

import javax.interceptor.Interceptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * * {@link Interceptor @Interceptor} enhancer by CGLIB
 */
public class DynamicProxyEnhancer {

    public Object enhance(Object target, Object... interceptors) {
        return Proxy.newProxyInstance(target.getClass().getClassLoader(),
                (Class<?>[]) ClassUtils.getAllInterfaces(target.getClass()).toArray(new Class[0]),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return method.invoke(target, args);
                    }
                });
    }
}
