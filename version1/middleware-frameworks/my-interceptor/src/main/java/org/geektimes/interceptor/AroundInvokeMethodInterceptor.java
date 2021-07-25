package org.geektimes.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

public class AroundInvokeMethodInterceptor implements MethodInterceptor {
    private final Object interceptor;
    private final Optional<Method> aroundInvokeMethod;

    public AroundInvokeMethodInterceptor(Object interceptor, Optional<Method> aroundInvokeMethod) {
        this.interceptor = interceptor;
        this.aroundInvokeMethod = findAroundInvokeMethod(interceptor);
    }

    private Optional<Method> findAroundInvokeMethod(Object interceptor) {
        return Stream.of(interceptor.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(AroundInvoke.class) &&
                        method.getParameterCount() == 1 &&
                        InvocationContext.class.isAssignableFrom(method.getParameterTypes()[0]))
                .findFirst();
    }

    @Override
    public Object intercept(Object target, Method method, Object... args) throws Exception {
        if (aroundInvokeMethod.isPresent()) {
            InvocationContext context = new ReflectiveMethodInvocationContext(target, method, args);
            return aroundInvokeMethod.get().invoke(interceptor, context);
        }
        //no method with @AroundInvoke annotation found
        return method.invoke(target, args);
    }
}
