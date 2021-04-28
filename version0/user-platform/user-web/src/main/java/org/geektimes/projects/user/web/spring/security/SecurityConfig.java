package org.geektimes.projects.user.web.spring.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        //开启
//        httpSecurity.csrf();
        httpSecurity.authorizeRequests();
    }

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
//        webSecurity.securityInterceptor();
    }
}
