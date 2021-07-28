package org.geektimes.interceptor.jdk;

import org.geektimes.interceptor.microprofile.faulttolerance.BulkheadInterceptor;
import org.geektimes.interceptor.microprofile.faulttolerance.TimeoutInterceptor;
import org.junit.Test;

public class DynamicProxyEnhancerTest {

    @Test
    public void testJdkEnhancer() {
        DynamicProxyEnhancer enhancer = new DynamicProxyEnhancer();
        GreetingService greetingService = new MyGreetingService();
        Object[] interceptors = new Object[]{
                new TimeoutInterceptor(),
                new BulkheadInterceptor()
        };
        Object proxy = enhancer.enhance(greetingService, interceptors);
        GreetingService greetingServiceProxy = (GreetingService) proxy;
        greetingServiceProxy.greet("Brothers!");

    }

}
