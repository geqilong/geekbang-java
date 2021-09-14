package org.geektimes.configuration.autoconf;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnNotWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ConditionalOnNotWebApplication
public class HelloWorldAutoConfiguration {

    @Bean
    public ApplicationRunner runner() {
        return args -> System.out.println("Hello World from ApplicationRunner");
    }
}
