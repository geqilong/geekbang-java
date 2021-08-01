package org.geektimes.microprofile.faulttolerance;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;
import org.eclipse.microprofile.faulttolerance.exceptions.FaultToleranceDefinitionException;
import org.geektimes.interceptor.ReflectiveMethodInvocationContext;
import org.junit.Before;
import org.junit.Test;

import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;

import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertEquals;

public class FallbackInterceptorTest {
    private FallbackInterceptor interceptor;

    @Before
    public void init() {
        this.interceptor = new FallbackInterceptor();
    }

    @Test
    public void testFallbackMethod() throws Throwable {
        EchoService echoService = new EchoService();
        Method method = EchoService.class.getMethod("echo", Long.class);
        InvocationContext context = new ReflectiveMethodInvocationContext(echoService, method, new Long(1L));
        assertEquals("1", interceptor.execute(context));
    }

    @Test
    @Fallback(value = DefaultFallbackHandler.class)
    public void testFallbackHandler() throws Throwable {
        Method method = getClass().getMethod("handlerException");
        InvocationContext context = new ReflectiveMethodInvocationContext(this, method);
        assertEquals("Fallback", interceptor.execute(context));
    }

    @Test(expected = FaultToleranceDefinitionException.class)
    @Fallback(fallbackMethod = "noSuchMethod")
    public void testFallbackMethodNotFound() throws Throwable {
        Method method = getClass().getMethod(currentThread().getStackTrace()[1].getMethodName());
        InvocationContext context = new ReflectiveMethodInvocationContext(this, method, new Long(1));
        interceptor.execute(context);
    }

    @Test(expected = FaultToleranceDefinitionException.class)
    @Fallback(fallbackMethod = "fallback")
    public void testFallbackMethodNotMatch() throws Throwable {
        Method method = getClass().getMethod(currentThread().getStackTrace()[1].getMethodName());
        InvocationContext context = new ReflectiveMethodInvocationContext(this, method, new Long(1));
        interceptor.execute(context);
    }

    @Test(expected = IllegalStateException.class)
    public void testSkipOn() throws Throwable {
        Method method = getClass().getMethod("skip");
        InvocationContext context = new ReflectiveMethodInvocationContext(this, method);
        interceptor.execute(context);
    }

    public String fallback() {
        return "";
    }

    @Fallback(DefaultFallbackHandler.class)
    public String handlerException() {
        throw new RuntimeException();
    }

    @Fallback(value = DefaultFallbackHandler.class, skipOn = Throwable.class)
    public String skip() {
        throw new IllegalStateException();
    }

    static class DefaultFallbackHandler implements FallbackHandler<String> {

        @Override
        public String handle(ExecutionContext context) {
            return "Fallback";
        }
    }
}
