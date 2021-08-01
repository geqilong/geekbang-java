package org.geektimes.microprofile.faulttolerance;

import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceDefinitionException;
import org.geektimes.commons.function.ThrowableSupplier;
import org.geektimes.interceptor.AnnotatedInterceptor;

import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.concurrent.*;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 *  * The interceptor implementation for the annotation {@link Asynchronous} of
 *  * MicroProfile Fault Tolerance
 */
@Asynchronous
@Interceptor
public class AsynchronousInterceptor extends AnnotatedInterceptor<Asynchronous> {
    //TODO ExecutorService fixed size = external Server Thread numbers
    private final ExecutorService executor = ForkJoinPool.commonPool();

    public AsynchronousInterceptor() {
        super();
        setPriority(500);
    }

    @Override
    protected Object execute(InvocationContext context, Asynchronous bindingAnnotation) throws Throwable {
        Method method = context.getMethod();
        if (validateMethod(method, Future.class)) {
            return executeFuture(context);
        } else if (validateMethod(method, CompletionStage.class)) {
            return executeCompletableFuture(context);
        } else {
            throw new FaultToleranceDefinitionException("The return type of @Asynchronous method must be " +
                    "java.util.concurrent.Future or java.util.concurrent.CompletableFuture!");
        }
    }

    private boolean validateMethod(Method method, Class<?> expectedReturnType)
            throws FaultToleranceDefinitionException {
        Class<?> returnType = method.getReturnType();
        return expectedReturnType.isAssignableFrom(returnType);
    }

    private Future<?> executeFuture(InvocationContext context) {
        return executor.submit(context::proceed);
    }

    private CompletableFuture<?> executeCompletableFuture(InvocationContext context) {
        return supplyAsync(() -> ThrowableSupplier.execute(context::proceed), executor);
    }
}
