package org.geektimes.cache.interceptor;

import javax.interceptor.InterceptorBinding;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@InterceptorBinding
public @interface NewCacheRemove {
    String cacheName() default "";
    boolean afterInvocation() default true;
}
