package org.geektimes.projects.user.mybatis.annotation;

import org.springframework.context.annotation.ImportResource;

@EnableMyBatis(dataSource = "dataSource",
        configLocation = "classpath*:META-INF/mybatis/mybatis-config.xml",
        mapperLocations = {"classpath:sample/config/mappers/**/*.xml"},
        environment = "development",
        transactionFactory = "myTransactionFactory")
@ImportResource(locations = "classpath*:sample/spring-context.xml") //SqlSessionFactoryBean
public class EnableMyBatisExample {
}
