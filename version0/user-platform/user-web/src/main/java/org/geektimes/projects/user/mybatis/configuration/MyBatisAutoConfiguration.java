package org.geektimes.projects.user.mybatis.configuration;


import org.apache.ibatis.session.SqlSessionFactory;
import org.geektimes.projects.user.mybatis.annotation.EnableMyBatis;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
@EnableMyBatis(dataSource = "dataSource", configLocation = "classpath*:META-INF/mybatis/mybatis-config.xml")
public class MyBatisAutoConfiguration {
    public MyBatisAutoConfiguration() {
    }
}
