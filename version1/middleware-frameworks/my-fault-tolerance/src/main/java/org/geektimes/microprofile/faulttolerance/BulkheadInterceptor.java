package org.geektimes.microprofile.faulttolerance;

import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.exceptions.BulkheadException;
import org.geektimes.interceptor.AnnotatedInterceptor;

import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static java.lang.String.format;

@Bulkhead
@Interceptor
public class BulkheadInterceptor extends AnnotatedInterceptor<Bulkhead> {
    private final ConcurrentMap<Bulkhead, ExecutorService> executorsCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Bulkhead, Semaphore> semaphoresCache = new ConcurrentHashMap<>();

    public BulkheadInterceptor() {
        super();
        setPriority(100);
    }

    @Override
    protected Object execute(InvocationContext context, Bulkhead bulkhead) throws Throwable {
        Method method = context.getMethod();
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
                        new ArrayBlockingQueue<>(waitingTaskQueue),
                        new BulkheadThreadFactory(),
                        new BulkheadExceptionRejectedExecutionHandler());
                return executor;
            }
        });
        Future<Object> future = executorService.submit(context::proceed);
        return future.get();
    }

    private Object executeInSemaphoreIsolation(InvocationContext context, Bulkhead bulkhead) throws Exception {
        int maxConcurrentRequests = bulkhead.value();
        Semaphore semaphore = semaphoresCache.computeIfAbsent(bulkhead,
                key -> new Semaphore(maxConcurrentRequests));
        Object result;
        if (!semaphore.tryAcquire()) { // No semaphore awailable
            throw new BulkheadException(
                    format("The concurrent request exceed the threshold[%d] under thread isolation",
                            maxConcurrentRequests)
            );
        }
        try {
            result = context.proceed();
        } finally {
            semaphore.release();
        }
        return result;
    }

    private boolean isThreadIsolation(Method method) {
        return method.isAnnotationPresent(Asynchronous.class);
    }

    private static class BulkheadThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        private BulkheadThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "Bulkhead-pool-" + poolNumber.getAndIncrement() +
                    "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement());
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    private static class BulkheadExceptionRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            int fixedThreadPoolSize = executor.getPoolSize();
            int waitingTaskQueueSize = executor.getQueue().size();
            throw new BulkheadException(
                    format("The concurrent request was rejected by the ThreadPoolExecutor[size: %d, queue: %d] under sempphore isolation",
                            fixedThreadPoolSize, waitingTaskQueueSize)
            );
        }
    }
}
