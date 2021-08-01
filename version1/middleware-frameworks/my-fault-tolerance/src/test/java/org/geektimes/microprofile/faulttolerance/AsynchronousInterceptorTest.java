package org.geektimes.microprofile.faulttolerance;

import org.geektimes.interceptor.ReflectiveMethodInvocationContext;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.concurrent.Future;

public class AsynchronousInterceptorTest {
    private AsynchronousInterceptor interceptor = new AsynchronousInterceptor();

    @Test
    public void testFuture() throws Throwable {
        EchoService echoService = new org.geektimes.microprofile.faulttolerance.EchoService();
        Method method = EchoService.class.getMethod("echo", Object.class);
        ReflectiveMethodInvocationContext context = new ReflectiveMethodInvocationContext
                (echoService, method, "Hello,World");
        Future<?> future = (Future<?>) interceptor.execute(context);
        future.get();
    }
}
