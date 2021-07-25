package org.geektimes.interceptor.microprofile.faulttolerance;

import org.eclipse.microprofile.faulttolerance.Retry;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static java.lang.String.format;
import static java.util.Optional.of;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static org.geektimes.commons.util.AnnotationUtils.findAnnotation;
import static org.geektimes.commons.util.TimeUtils.toTimeUnit;

@Retry
@Interceptor
public class RetryInterceptor {
    private final ScheduledExecutorService executorService = newScheduledThreadPool(2);

    @AroundInvoke
    public Object execute(InvocationContext context) throws Exception {
        Method method = context.getMethod();
        Retry retry = findRetry(method);
        if (null == retry) {
            return context.proceed();
        }
        long maxRetries = retry.maxRetries();
        if (maxRetries < 1) {
            return context.proceed();
        }
        //Invoke first
        Object result = null;
        boolean success = false;
        try {
            result = context.proceed();
            success = true;
        } catch (Exception e) {
            if (isAbortOn(retry, e)) {
                throw e;
            } else if (!isRetryOn(retry, e)) {
                throw e;
            }
            success = false;
        }

        if (success) {
            return result;
        }

        Supplier<InvocationResult> retryActionResult = new Supplier<InvocationResult>() {
            @Override
            public InvocationResult get() {
                InvocationResult invocationResult = new InvocationResult();
                try {
                    invocationResult.setResult(context.proceed());
                    invocationResult.setSuccess(true);
                } catch (Throwable e) {
                    invocationResult.setSuccess(false);
                    if (!isAbortOn(retry, e) && isRetryOn(retry, e)) { // retry on, abort not on
                        invocationResult.setException(e);
                    } else if (isAbortOn(retry, e) || !isRetryOn(retry, e)) { //abort on, retry not on
                        invocationResult.setException(e);
                        //TODO more?
                    }
                }
                return invocationResult;
            }
        };

        Optional<Long> delay = getDelay(retry);
        Callable<InvocationResult> maxRetriesActionResult = new Callable<InvocationResult>() {
            @Override
            public InvocationResult call() throws Exception {
                InvocationResult value = null;
                for (int i = 0; i < maxRetries; i++) {
                    if (delay.isPresent()) {//Schedule
                        Optional<Long> jitter = getJitter(retry);
                        long actualDelay = delay.get() + jitter.get();
                        ScheduledFuture<InvocationResult> future =
                                executorService.schedule(() -> retryActionResult.get(), actualDelay, TimeUnit.MILLISECONDS);
                        value = future.get();
                    } else {
                        value = retryActionResult.get();
                    }
                    if (value.isSuccess() || value.getException() == null) {
                        break;
                    }
                }
                return value;
            }
        };

        Optional<Long> maxDuration = getMaxDuration(retry, delay);
        if (maxDuration.isPresent()) {
            Future<InvocationResult> future = executorService.submit(maxRetriesActionResult);
            InvocationResult value = future.get(maxDuration.get(), TimeUnit.NANOSECONDS);
            result = value.getResult();
        } else {
            InvocationResult value = maxRetriesActionResult.call();
            result = value.getResult();
        }
        return result;
    }

    private Optional<Long> getMaxDuration(Retry retry, Optional<Long> delay) {
        long maxDuration = retry.maxDuration();
        if (maxDuration < 1) {
            return Optional.empty();
        }
        TimeUnit timeUnit = toTimeUnit(retry.durationUnit());
        long maxDurationInNanos = timeUnit.toNanos(maxDuration);
        delay.ifPresent(delayInNanos -> {
            if (delayInNanos >= maxDurationInNanos) {
                throw new IllegalArgumentException(
                        format("The max duration[%d ns] must be greater than the delay duration[%d ns] if set.",
                                maxDurationInNanos, delayInNanos));
            }
        });
        return of(maxDurationInNanos);
    }

    /**
     * -n ~ n random???
     *
     * @param retry
     * @return
     */
    private Optional<Long> getJitter(Retry retry) {
        long jitter = retry.jitter();
        if (jitter < 1) {
            return Optional.empty();
        }
        TimeUnit timeUnit = toTimeUnit(retry.jitterDelayUnit());
        long origin = Math.negateExact(jitter);
        long bound = jitter;
        long value = ThreadLocalRandom.current().nextLong(origin, bound);
        return of(timeUnit.toNanos(value));
    }

    private Optional<Long> getDelay(Retry retry) {
        long delay = retry.delay();
        if (delay < 1) {
            return Optional.empty();
        }
        TimeUnit timeUnit = toTimeUnit(retry.delayUnit());
        return of(timeUnit.toNanos(delay));
    }

    private boolean isRetryOn(Retry retry, Throwable e) {
        for (Class<? extends Throwable> retryType : retry.retryOn()) {
            if (retryType.isInstance(e.getCause())) {
                return true;
            }
        }
        return false;
    }

    private boolean isAbortOn(Retry retry, Throwable e) {
        for (Class<? extends Throwable> retryType : retry.abortOn()) {
            if (retryType.isInstance(e.getCause())) {
                return true;
            }
        }
        return false;
    }

    private Retry findRetry(Method method) {
        Retry timeout = findAnnotation(method, Retry.class);
        if (timeout == null) {
            timeout = method.getDeclaringClass().getAnnotation(Retry.class);
        }
        return timeout;
    }

    private static class InvocationResult {
        private Object result;
        private boolean success;
        private Throwable exception;

        public Object getResult() {
            return result;
        }

        public void setResult(Object result) {
            this.result = result;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public Throwable getException() {
            return exception;
        }

        public void setException(Throwable exception) {
            this.exception = exception;
        }
    }
}
