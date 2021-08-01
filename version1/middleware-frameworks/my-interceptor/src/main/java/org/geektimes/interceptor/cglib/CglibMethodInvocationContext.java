package org.geektimes.interceptor.cglib;

import net.sf.cglib.proxy.MethodProxy;
import org.geektimes.interceptor.ReflectiveMethodInvocationContext;

import java.lang.reflect.Method;


public class CglibMethodInvocationContext extends ReflectiveMethodInvocationContext {
    private final MethodProxy methodProxy;

    public CglibMethodInvocationContext(Object target, Method method, MethodProxy methodProxy, Object... parameters) {
        super(target, method, parameters);
        this.methodProxy = methodProxy;
    }

    @Override
    public Object proceed() throws Exception {
        try {
            return methodProxy.invokeSuper(getTarget(), getParameters());
        } catch (Throwable throwable) {
            throw new Exception(throwable);
        }
    }

}
