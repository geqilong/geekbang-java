package org.geektimes.projects.user.web.spring.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MyWebSecurityConfig implements ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, WebSecurityConfigurerAdapter> adapterMap = applicationContext.getBeansOfType(WebSecurityConfigurerAdapter.class);
        if (adapterMap.values().size() > 1) {
            throw new RuntimeException("There can be only one implementation of org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter, exiting now...");
        }
        //TODO
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
