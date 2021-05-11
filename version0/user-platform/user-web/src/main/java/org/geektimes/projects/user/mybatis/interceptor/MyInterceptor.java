package org.geektimes.projects.user.mybatis.interceptor;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.geektimes.projects.user.repository.UserRepository;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class MyInterceptor implements Interceptor {
    @Resource
    private UserRepository userRepository;


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        return null;
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.refresh();
        MyInterceptor myInterceptor = new MyInterceptor();
        AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
        beanFactory.autowireBean(myInterceptor);
        context.close();
    }
}
