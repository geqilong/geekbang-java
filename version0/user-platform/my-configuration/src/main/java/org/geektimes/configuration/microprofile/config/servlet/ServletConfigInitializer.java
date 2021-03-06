package org.geektimes.configuration.microprofile.config.servlet;

import org.geektimes.projects.context.ClassicComponentContext;
import org.geektimes.projects.init.ComponentContextInitializer;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServletConfigInitializer implements ServletContainerInitializer {
    Logger logger = Logger.getLogger(getClass().getName());

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext servletContext) throws ServletException {
        logger.log(Level.INFO,"###########################ComponentContext初始化开始###########################");
        ClassicComponentContext componentContext = new ClassicComponentContext();
        componentContext.init(servletContext);
        logger.log(Level.INFO,"###########################ComponentContext初始化完成###########################");

        //增加ServletContextListener
        logger.log(Level.INFO, "^-^-^-^-^-^-^-^-^-^-^-^-^-^^-^-^-^-^-^-^-^增加初始化Listener^-^-^-^-^-^-^-^-^-^-^-^-^-^^-^-^-^-^-^-^-^");
        servletContext.addListener(ServletContextConfigInitializer.class);
        servletContext.addListener(ComponentContextInitializer.class);
        servletContext.addListener(TestingInitializer.class);
    }


}
