package org.geektimes.interceptor.cglib;


import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.geektimes.interceptor.ChainableInvocationContext;

import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;

/**
 * {@link MethodInterceptor} -> @Interceptor chain
 */
class MethodInterceptorAdapter implements MethodInterceptor {
    private final Object target;
    private final Object[] interceptors;

    public MethodInterceptorAdapter(Object target, Object[] interceptors) {
        this.target = target;
        this.interceptors = interceptors;
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        /*InvocationContext delegateContext;
        Class targetClass = obj.getClass();
        if (targetClass.isInterface() || Proxy.isProxyClass(targetClass)) { //判断使用JDK代理
            delegateContext = new DynoxyMethodInvocationContext(obj, method, args);
        } else {
            delegateContext = new CglibMethodInvocationContext(obj, method, proxy, args);
        }
        ChainableInvocationContext context = new ChainableInvocationContext(delegateContext, interceptors);
        return context.proceed();*/
        InvocationContext delegateContext = new CglibMethodInvocationContext(obj, method, proxy, args);
        ChainableInvocationContext context = new ChainableInvocationContext(delegateContext, interceptors);
        return context.proceed();
    }
}
