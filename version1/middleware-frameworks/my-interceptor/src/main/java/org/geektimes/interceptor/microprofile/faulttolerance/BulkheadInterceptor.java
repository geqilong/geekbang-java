package org.geektimes.interceptor.microprofile.faulttolerance;

import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Bulkhead;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.concurrent.*;
import java.util.function.Function;

import static org.geektimes.commons.util.AnnotationUtils.findAnnotation;

@Bulkhead
@Interceptor
public class BulkheadInterceptor {
    private final ConcurrentMap<Bulkhead, ExecutorService> executorsCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Bulkhead, Semaphore> semaphoresCache = new ConcurrentHashMap<>();

    @AroundInvoke
    public Object execute(InvocationContext context) throws Exception {
        Method method = context.getMethod();
        Bulkhead bulkhead = findBulkhead(method);
        if (null == bulkhead) {
            return context.proceed();
        }
        if (isThreadIsolation(method)) {
            return executeInThreadIsolation(context, bulkhead);
        } else {
            return executeInSemaphoreIsolation(context, bulkhead);
        }
    }

    private Object executeInThreadIsolation(InvocationContext context, Bulkhead bulkhead) throws ExecutionException, InterruptedException {
        ExecutorService executorService = executorsCache.computeIfAbsent(bulkhead, new Function<Bulkhead, ExecutorService>() {
            @Override
            public ExecutorService apply(Bulkhead key) {
                int fixedSize = bulkhead.value();
                int waitingTaskQueue = bulkhead.waitingTaskQueue();
                ThreadPoolExecutor executor = new ThreadPoolExecutor(fixedSize, fixedSize,
                        0, TimeUnit.MILLISECONDS,
                        new ArrayBlockingQueue<>(waitingTaskQueue));
                return executor;
            }
        });
        Future<Object> future = executorService.submit(context::proceed);
        return future.get();
    }

    private Object executeInSemaphoreIsolation(InvocationContext context, Bulkhead bulkhead) throws Exception {
        Semaphore semaphore = semaphoresCache.computeIfAbsent(bulkhead, key -> {
            int maxConcurrentRequests = bulkhead.value();
            return new Semaphore(maxConcurrentRequests);
        });
        Object result;
        try {
            semaphore.acquire();
            result = context.proceed();
        } finally {
            semaphore.release();
        }
        return result;
    }

    private Bulkhead findBulkhead(Method method) {
        Bulkhead bulkhead = findAnnotation(method, Bulkhead.class);
        if (null == bulkhead) {// try to find in the declaring class
            bulkhead = method.getDeclaringClass().getAnnotation(Bulkhead.class);
        }
        return bulkhead;
    }

    private boolean isThreadIsolation(Method method) {
        return method.isAnnotationPresent(Asynchronous.class);
    }

}
