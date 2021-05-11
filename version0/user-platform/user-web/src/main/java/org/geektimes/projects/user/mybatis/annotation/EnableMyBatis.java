package org.geektimes.projects.user.mybatis.annotation;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 激活MyBatis
 * 2021年5月11日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
@Import(MyBatisBeanDefinitionRegistrar.class)
public @interface EnableMyBatis {

    /**
     * @return the bean name of {@link SqlSessionFactoryBean}
     */
    String value() default "sqlSessionFactoryBean";

    /**
     * @return DataSource bean name
     */
    String dataSource();

    /**
     * @return the location of {@link Configuration}
     */
    String configLocation();

    /**
     * @return the location of {@link Mapper}
     * @see MapperScan
     */
    String[] mapperLocations() default {};

    String environment() default "SqlSessionFactoryBean";
}
