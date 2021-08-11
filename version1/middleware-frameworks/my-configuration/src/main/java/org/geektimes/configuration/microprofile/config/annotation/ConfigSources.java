package org.geektimes.configuration.microprofile.config.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigSources {
    ConfigSource[] value();
}
