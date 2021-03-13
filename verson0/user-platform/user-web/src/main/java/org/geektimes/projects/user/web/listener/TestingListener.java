package org.geektimes.projects.user.web.listener;

import org.geektimes.projects.user.context.ComponentContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.logging.Logger;

public class TestingListener implements ServletContextListener {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        testPropertyFromServletContext(servletContextEvent.getServletContext());
        testPropertyFromJNDI();
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
