package org.geektimes.interceptor;

import net.sf.cglib.proxy.MethodProxy;

import javax.interceptor.InvocationContext;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CglibMethodInvocationContext implements InvocationContext {
    private final Object target;
    private final Method method;
    private final MethodProxy proxy;
    private Object[] parameters;
    private final Map<String, Object> contextData;

    public CglibMethodInvocationContext(Object target, Method method, MethodProxy proxy, Object[] parameters) {
        this.target = target;
        this.method = method;
        this.proxy = proxy;
        this.parameters = parameters;
        this.contextData = new HashMap<>();
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public Object getTimer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public Constructor<?> getConstructor() {
        throw new UnsupportedOperationException("ReflectiveMethodInvocationContext does not support Constructor!");
    }

    @Override
    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public void setParameters(Object[] params) {
        this.parameters = params;
    }

    @Override
    public Map<String, Object> getContextData() {
        return contextData;
    }

    @Override
    public Object proceed() throws Exception {
        try {
            return proxy.invokeSuper(getTarget(), getParameters());
        } catch (Throwable throwable) {
            throw new Exception(throwable);
        }
    }

}
