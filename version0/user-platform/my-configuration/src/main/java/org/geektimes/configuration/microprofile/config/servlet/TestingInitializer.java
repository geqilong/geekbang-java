package org.geektimes.configuration.microprofile.config.servlet;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.geektimes.projects.context.ClassicComponentContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.logging.Logger;

public class TestingInitializer implements ServletContextListener {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ClassicComponentContext context = ClassicComponentContext.getInstance();
        testPropertyFromServletContext(servletContextEvent.getServletContext());
        testPropertyFromJNDI();
        testMicroProfile(servletContextEvent.getServletContext().getClassLoader());
    }

    private void testMicroProfile(ClassLoader classLoader) {
        String propertyName = "application.name";
        ConfigProviderResolver configProviderResolver = ConfigProviderResolver.instance();
        Config config = configProviderResolver.getConfig(classLoader);
        logger.info("Config Object: " + config);
        logger.info("^^^^^^org.eclipse.microprofile.config.Config Property[" + propertyName + "]: " + config.getValue(propertyName, String.class));
    }

    private void testPropertyFromJNDI() {
        String propertyName = "maxValue";
        ClassicComponentContext classicComponentContext = ClassicComponentContext.getInstance();
        logger.info("JNDI Property[" + propertyName + "]: " + classicComponentContext.getComponent(propertyName));
    }

    private void testPropertyFromServletContext(ServletContext servletContext) {
        String propertyName = "application.name";
        logger.info("ServletContext Property[" + propertyName + "]: " + servletContext.getInitParameter(propertyName));
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
