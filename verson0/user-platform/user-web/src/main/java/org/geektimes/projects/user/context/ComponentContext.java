package org.geektimes.projects.user.context;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import java.rmi.NoSuchObjectException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ComponentContext {
    public static final String CONTEXT_NAME = ComponentContext.class.getName();
    private static ServletContext servletContext;
    private Context context;
    private final Logger logger = Logger.getLogger(getClass().getName());

    public static ComponentContext getInstance() {
        return (ComponentContext) servletContext.getAttribute(CONTEXT_NAME);
    }

    public void init(ServletContext servletContext) throws RuntimeException {
        try {
            this.context = (Context) new InitialContext().lookup("java:comp/env");
        } catch (NamingException e) {
            throw new RuntimeException();
        }
        servletContext.setAttribute(CONTEXT_NAME, this);
        ComponentContext.servletContext = servletContext;
        logger.log(Level.INFO, "ComponentContext初始化成功");
    }

    public <C> C getComponent(String name) throws NoSuchObjectException {
        C component = null;
        try {
            component = (C) context.lookup(name);
        } catch (NamingException e) {
            throw new NoSuchObjectException(name);
        }
        return component;
    }

    public void destroy() throws RuntimeException {
        if (this.context != null) {
            try {
                context.close();
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
