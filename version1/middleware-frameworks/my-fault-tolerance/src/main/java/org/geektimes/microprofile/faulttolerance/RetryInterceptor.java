package org.geektimes.microprofile.faulttolerance;

import org.eclipse.microprofile.faulttolerance.Retry;
import org.geektimes.interceptor.AnnotatedInterceptor;

import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.*;

import static java.lang.String.format;
import static java.util.Optional.of;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static org.geektimes.commons.reflect.util.ClassUtils.isDerived;
import static org.geektimes.commons.util.AnnotationUtils.findAnnotation;
import static org.geektimes.commons.util.TimeUtils.toTimeUnit;

@Retry
@Interceptor
public class RetryInterceptor extends AnnotatedInterceptor<Retry> {
    private final ScheduledExecutorService executorService = newScheduledThreadPool(2);

    public RetryInterceptor() {
        super();
        setPriority(300);
    }

    @Override
    public Object execute(InvocationContext context, Retry retry) throws Throwable {
        long maxRetries = retry.maxRetries();
        if (maxRetries < 1) {
            return context.proceed();
        }
        //Invoke first
        InvocationResult result = action(retry, context);
        if (result.isSuccess()) {
            return result.getResult();
        } else if (result.getFailure() != null) { // if the abort or no-retry failure found
            throw result.getFailure();
        }
        Callable<InvocationResult> retryAction = () -> action(retry, context);
        Optional<Long> delay = getDelay(retry);
        Callable<InvocationResult> maxRetriesAction = () -> {
            InvocationResult retryActionResult = null;
            for (int i = 0; i < maxRetries; i++) {
                if (delay.isPresent()) {//Schedule
                    Optional<Long> jitter = getJitter(retry);
                    long actualDelay = delay.get() + jitter.get();
                    ScheduledFuture<InvocationResult> future =
                            executorService.schedule(retryAction, actualDelay, TimeUnit.MILLISECONDS);
                    retryActionResult = future.get();
                } else {
                    retryActionResult = retryAction.call();
                }
                if (retryActionResult.isSuccess()) {
                    break;
                }
            }
            return retryActionResult;
        };

        Optional<Long> maxDuration = getMaxDuration(retry, delay);
        if (maxDuration.isPresent()) {
            Future<InvocationResult> future = executorService.submit(maxRetriesAction);
            result = future.get(maxDuration.get(), TimeUnit.NANOSECONDS);
        } else {
            result = maxRetriesAction.call();
        }
        return result;
    }

    private InvocationResult action(Retry retry, InvocationContext context) {
        InvocationResult invocationResult = new InvocationResult();
        try {
            invocationResult.setResult(context.proceed());
            invocationResult.setSuccess(true);
        } catch (Throwable e) {
            Throwable failure = getFailure(e);
            invocationResult.setSuccess(false);
            if (isAbortOn(retry, failure) || !isRetryOn(retry, failure)) {
                invocationResult.setFailure(failure);
            }
        }
        return invocationResult;
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
        return isDerived(e.getClass(), retry.retryOn());
    }

    private boolean isAbortOn(Retry retry, Throwable e) {
        return isDerived(e.getClass(), retry.abortOn());

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
        /**
         * Holds the abort or no-retry failure
         */
        private Throwable failure;

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

        public Throwable getFailure() {
            return failure;
        }

        public void setFailure(Throwable failure) {
            this.failure = failure;
            this.setSuccess(false);
        }
    }
}
