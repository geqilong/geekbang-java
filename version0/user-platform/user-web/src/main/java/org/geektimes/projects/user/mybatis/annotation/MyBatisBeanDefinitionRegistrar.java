package org.geektimes.projects.user.mybatis.annotation;

import org.apache.ibatis.plugin.Plugin;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class MyBatisBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, BeanFactoryAware {
    private Environment environment;
    private BeanFactory beanFactory;

    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder beanDefinitionBuilder = genericBeanDefinition(SqlSessionFactoryBean.class);
        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableMyBatis.class.getName());
        /**
         *  <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
         *   <property name="dataSource" ref="dataSource" />
         *   <property name="mapperLocations" value="classpath*:" />
         *  </bean >
         */
        beanDefinitionBuilder.addPropertyReference("dataSource", (String) attributes.get("dataSource"));
        //Spring String类型可以自动转化Spring Resource
        beanDefinitionBuilder.addPropertyValue("configLocation", attributes.get("configLocation"));
        beanDefinitionBuilder.addPropertyValue("mapperLocations", attributes.get("mapperLocations"));
        beanDefinitionBuilder.addPropertyValue("environment", resolvePlaceHolder(attributes.get("environment")));
        //其他属性
        beanDefinitionBuilder.addPropertyValue("transactionFactory", new MyTransactionFactory());
        beanDefinitionBuilder.addPropertyValue("configurationProperties", resolveConfigurationProperties(attributes.get("configurationProperties")));
        beanDefinitionBuilder.addPropertyValue("failFast", Boolean.valueOf((Boolean) attributes.get("failFast")));
        beanDefinitionBuilder.addPropertyValue("plugins", retrieveAllPlugins(attributes.get("plugins")));
        //SqlSessionFactoryBean的BeanDefinition
        BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        String beanName = (String) attributes.get("value");
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    private Object resolveConfigurationProperties(Object confProps) {
        if (null == confProps || !StringUtils.hasText((String) confProps))
            return null;
        String confFile = (String) confProps;
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(confFile));
        } catch (IOException e) {
            //TODO Provide logging
            properties = null;
        }
        return properties;
    }

    /**
     * Get all plugin beans
     * @param plugins
     * @return
     */
    private Object retrieveAllPlugins(Object plugins) {
        if (null == plugins)
            return null;
        String[] pluginNames = (String[]) plugins;
        List<Plugin> pluginList = new ArrayList<>();
        for (int i = 0; i < pluginNames.length; i++) {
            if (StringUtils.hasText(pluginNames[i])) {
                Plugin plugin = getPluginByName(pluginNames[i]);
                if (null != plugin) {
                    pluginList.add(plugin);
                }
            }
        }
        return pluginList.size() > 0 ? pluginList.toArray(new Plugin[0]) : null;
    }

    private Plugin getPluginByName(String pluginName) {
        Plugin plugin;
        try {
            plugin = beanFactory.getBean(pluginName, Plugin.class);
            return plugin;
        } catch (BeansException ignore) {
            //TODO Provide logging
            return null;
        }
    }

    private Object resolvePlaceHolder(Object value) {
        if (value instanceof String) {
            return environment.resolvePlaceholders((String) value);
        }
        return value;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
