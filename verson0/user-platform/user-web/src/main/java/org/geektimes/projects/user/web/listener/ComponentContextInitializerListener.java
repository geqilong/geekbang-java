package org.geektimes.projects.user.web.listener;

import org.geektimes.projects.user.context.ComponentContext;
import org.geektimes.projects.user.sql.DBConnectionManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class ComponentContextInitializerListener implements ServletContextListener {
    private ServletContext servletContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.servletContext = sce.getServletContext();
        ComponentContext componentContext = new ComponentContext();
        componentContext.init(this.servletContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ComponentContext componentContext = ComponentContext.getInstance();
        componentContext.destroy();
    }
}
