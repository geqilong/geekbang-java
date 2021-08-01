package org.geektimes.microprofile.faulttolerance;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;
import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceDefinitionException;
import org.geektimes.interceptor.AnnotatedInterceptor;

import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;

import static java.lang.String.format;
import static org.geektimes.commons.reflect.util.ClassUtils.getTypes;
import static org.geektimes.commons.reflect.util.ClassUtils.isDerived;

/**
 * The interceptor implementation for the annotation {@link Fallback} of
 * MicroProfile Fault Tolerance
 */
@Interceptor
public class FallbackInterceptor extends AnnotatedInterceptor<Fallback> {

    public FallbackInterceptor() {
        super();
        setPriority(200);
    }

    @Override
    protected Object execute(InvocationContext context, Fallback fallback) throws Throwable {
        Object result = null;
        try {
            result = context.proceed();
        } catch (Throwable e) {
            Throwable failure = getFailure(e);
            if (!isApplyOn(fallback, failure) || isSkipOn(fallback, failure)) {
                throw failure;
            }
            result = handleFallback(context, fallback, failure);
        }
        return result;
    }

    private Object handleFallback(InvocationContext context, Fallback fallback, Throwable failure)
            throws Exception {
        Object result;
        String methodName = fallback.fallbackMethod();
        if (!"".equals(methodName)) {
            Method fallbackMethod = findFallbackMethod(context, methodName);
            result = fallbackMethod.invoke(context.getTarget(), context.getParameters());
        } else {
            Class<? extends FallbackHandler<?>> fallbackHandlerType = fallback.value();
            FallbackHandler fallbackHandler = fallbackHandlerType.newInstance();
            result = fallbackHandler.handle(new ExecutionContextAdapter(context, failure));
        }
        return result;
    }

    private Method findFallbackMethod(InvocationContext context, String methodName) {
        Class<?>[] parameterTypes = getTypes(context.getParameters());
        Class<?> type = context.getMethod().getDeclaringClass();
        Method fallbackMethod = null;
        try {
            fallbackMethod = type.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException ignored) {
            // try to find the fallback method in the target class
            type = context.getTarget().getClass();
            try {
                fallbackMethod = type.getMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                throw new FaultToleranceDefinitionException(
                        format("The fallbackMethod[%s] that configured at @Fallback can't be found!", methodName), e);
            }
        }
        return fallbackMethod;
    }

    private boolean isSkipOn(Fallback fallback, Throwable failure) {
        return isDerived(failure.getClass(), fallback.skipOn());
    }

    private boolean isApplyOn(Fallback fallback, Throwable failure) {
        return isDerived(failure.getClass(), fallback.applyOn());
    }

    private static class ExecutionContextAdapter implements ExecutionContext {
        private final InvocationContext context;
        private final Throwable e;

        public ExecutionContextAdapter(InvocationContext context, Throwable failure) {
            this.context = context;
            this.e = failure;
        }

        @Override
        public Method getMethod() {
            return context.getMethod();
        }

        @Override
        public Object[] getParameters() {
            return context.getParameters();
        }

        @Override
        public Throwable getFailure() {
            return e;
        }
    }
}
