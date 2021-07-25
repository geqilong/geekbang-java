package org.geektimes.interceptor;

import java.lang.reflect.Method;

public interface MethodInterceptor {
    Object intercept(Object target, Method method, Object... args) throws Exception;
}
