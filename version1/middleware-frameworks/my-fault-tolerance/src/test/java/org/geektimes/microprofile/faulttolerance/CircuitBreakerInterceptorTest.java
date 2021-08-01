package org.geektimes.microprofile.faulttolerance;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.exceptions.CircuitBreakerOpenException;
import org.geektimes.interceptor.ReflectiveMethodInvocationContext;
import org.junit.Assert;
import org.junit.Test;

import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;

public class CircuitBreakerInterceptorTest {
    private CircuitBreakerInterceptor interceptor = new CircuitBreakerInterceptor();

    @Test(expected = CircuitBreakerOpenException.class)
    public void testFailOn() throws Throwable {
        Method method = getClass().getMethod("failOn");
        InvocationContext context = new ReflectiveMethodInvocationContext(this, method);
        try {
            interceptor.execute(context);
        } catch (Throwable e) {
        }

        CircuitBreaker circuitBreaker = method.getAnnotation(CircuitBreaker.class);
        CircuitBreakerInterceptor.CountableSlidingWindow slidingWindow = interceptor.getSlidingWindow(circuitBreaker);
        Assert.assertTrue(slidingWindow.shouldReset());
        Assert.assertTrue(slidingWindow.isOpen());
        Assert.assertFalse(slidingWindow.isClosed());
        Assert.assertFalse(slidingWindow.isHalfOpen());
        interceptor.execute(context);
    }

    @CircuitBreaker(failOn = RuntimeException.class, requestVolumeThreshold = 1)
    public void failOn() {
        throw new RuntimeException();
    }
}
