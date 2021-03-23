package org.geektimes.projects.init;

import org.geektimes.projects.context.ComponentContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComponentContextInitializer implements ServletContextListener {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private ServletContext servletContext;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        this.servletContext = servletContextEvent.getServletContext();
        ComponentContext componentContext = new ComponentContext();
        componentContext.init(this.servletContext);
        logger.log(Level.INFO,"###########################ComponentContext初始化完成###########################");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        /*ComponentContext componentContext = ComponentContext.getInstance();
        componentContext.destroy();*/
    }
}
