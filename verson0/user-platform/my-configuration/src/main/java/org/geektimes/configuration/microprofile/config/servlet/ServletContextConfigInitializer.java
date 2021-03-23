package org.geektimes.configuration.microprofile.config.servlet;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.geektimes.configuration.microprofile.config.source.impl.ServletContextConfigSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ServletContextConfigInitializer implements ServletContextListener {
    private static ThreadLocal<Config> CONFIG_TL = new ThreadLocal<Config>() {
        @Override
        protected Config initialValue() {
            return null;
        }
    };

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        ServletContextConfigSource.SERVLET_CONTEXT_TL.set(servletContext);
        ServletContextConfigSource servletContextConfigSource = new ServletContextConfigSource();
        //获取当前ClassLoader
        ClassLoader classLoader = servletContext.getClassLoader();
        ConfigProviderResolver configProviderResolver = ConfigProviderResolver.instance();
        ConfigBuilder configBuilder = configProviderResolver.getBuilder();
        //配置ClassLoader
        configBuilder.forClassLoader(classLoader);
        //默认配置源（内建，静态）
        configBuilder.addDefaultSources();
        //通过发现配置源（动态的）
        configBuilder.addDiscoveredSources();
        //配置转换器（动态）
        configBuilder.addDiscoveredConverters();//内含配置默认转换器（内建，默认）
        //增加扩展配置源（基于Servlet引擎）
        configBuilder.withSources(servletContextConfigSource);
        //获取Config
        Config config = configBuilder.build();
        //注册Config关联到当前ClassLoader
        configProviderResolver.registerConfig(config, classLoader);
        servletContext.setAttribute("USER_PLATFORM_CONFIG", config);
        CONFIG_TL.set(config);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //TODO
    }

    public static Config getConfig() {
        return CONFIG_TL.get();
    }
}
