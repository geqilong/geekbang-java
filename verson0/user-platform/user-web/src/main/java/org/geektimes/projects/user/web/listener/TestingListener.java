package org.geektimes.projects.user.web.listener;

import org.eclipse.microprofile.config.Config;
import org.geektimes.configuration.microprofile.config.JavaEEConfigProviderResolver;
import org.geektimes.projects.user.context.ComponentContext;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.management.UserManager;

import javax.management.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestingListener implements ServletContextListener {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        testPropertyFromServletContext(servletContextEvent.getServletContext());
        testPropertyFromJNDI();
        registerMBean();
        testMicroProfile(servletContextEvent.getServletContext().getClassLoader());
    }

    private void registerMBean() {
        //获取MBean平台Server
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName objectName = null;
        try {
            objectName = new ObjectName("org.geektimes.projects.user.management:type=User");
            mBeanServer.registerMBean(new UserManager(new User()), objectName);
        } catch (MalformedObjectNameException| NotCompliantMBeanException|InstanceAlreadyExistsException | MBeanRegistrationException e) {
            logger.log(Level.SEVERE, "Error registering MBean", e.getCause());
        }
    }

    private void testMicroProfile(ClassLoader classLoader) {
        String propertyName = "application.name";
        JavaEEConfigProviderResolver javaEEConfigProviderResolver = new JavaEEConfigProviderResolver();
        Config config = javaEEConfigProviderResolver.getConfig(classLoader);
        logger.info("^^^^^^org.eclipse.microprofile.config.Config Property[" + propertyName + "]: " + config.getValue(propertyName, String.class));
    }

    private void testPropertyFromJNDI() {
        String propertyName = "maxValue";
        ComponentContext componentContext = ComponentContext.getInstance();
        logger.info("JNDI Property[" + propertyName + "]: " + componentContext.getComponent(propertyName));
    }

    private void testPropertyFromServletContext(ServletContext servletContext) {
        String propertyName = "application.name";
        logger.info("ServletContext Property[" + propertyName + "]: " + servletContext.getInitParameter(propertyName));
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
