package org.geektimes.interceptor.microprofile.faulttolerance;

import org.eclipse.microprofile.faulttolerance.Timeout;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;

import static java.util.concurrent.Executors.newCachedThreadPool;
import static org.geektimes.commons.util.AnnotationUtils.findAnnotation;
import static org.geektimes.commons.util.TimeUtils.toTimeUnit;

@Timeout
@Interceptor
public class TimeoutInterceptor {

    // TODO ExecutorService fixed size = external Server Thread numbers
    private final ExecutorService executor = newCachedThreadPool();

    @AroundInvoke
    public Object execute(InvocationContext context) throws Exception {
        Method method = context.getMethod();
        Timeout timeout = findTimeout(method);
        if (null != timeout) {
            ChronoUnit chronoUnit = timeout.unit();
            long timeValue = timeout.value();
            TimeUnit timeUnit = toTimeUnit(chronoUnit);
            Future future = executor.submit(() -> {
                try {
                    return context.proceed();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            try {
                return future.get(timeValue, timeUnit);
            } catch (TimeoutException e) {
                future.cancel(true);
            }
        }
        return context.proceed();
    }

    private Timeout findTimeout(Method method) {
        Timeout timeout = findAnnotation(method, Timeout.class);
        if (null == timeout) {
            timeout = method.getDeclaringClass().getAnnotation(Timeout.class);
        }
        return timeout;
    }
}
