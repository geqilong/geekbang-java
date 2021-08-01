package org.geektimes.interceptor;

import org.junit.Test;

import static org.geektimes.interceptor.AnnotatedInterceptor.loadInterceptors;

/**
 * {@link DefaultInterceptorEnhancer} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class DefaultInterceptorEnhancerTest {

    private InterceptorEnhancer interceptorEnhancer = new DefaultInterceptorEnhancer();

    @Test
    public void testInterface() {
        EchoService echoService = new EchoService();
        echoService = interceptorEnhancer.enhance(echoService, loadInterceptors());
        echoService.echo("Hello,World");
    }
}
