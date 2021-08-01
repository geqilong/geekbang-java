package org.geektimes.microprofile.faulttolerance;

import org.eclipse.microprofile.faulttolerance.Timeout;
import org.geektimes.interceptor.AnnotatedInterceptor;

import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;

import static org.geektimes.commons.util.TimeUtils.toTimeUnit;

@Timeout
@Interceptor
public class TimeoutInterceptor extends AnnotatedInterceptor<Timeout> {
    // TODO ExecutorService fixed size = external Server Thread numbers
    private final ExecutorService executor = ForkJoinPool.commonPool();

    public TimeoutInterceptor() {
        super();
        setPriority(400);
    }

    @Override
    protected Object execute(InvocationContext context, Timeout timeout) throws Throwable {
        ChronoUnit chronoUnit = timeout.unit();
        long timeValue = timeout.value();
        TimeUnit timeUnit = toTimeUnit(chronoUnit);
        Future future = executor.submit(context::proceed);
        try{
            return future.get(timeValue, timeUnit);
        } catch(TimeoutException e){
            throw new org.eclipse.microprofile.faulttolerance.exceptions.TimeoutException(e);
        }
    }


}
