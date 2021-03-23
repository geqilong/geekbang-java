package org.geektimes.projects.user.web.listener;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.management.UserManager;

import javax.management.*;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestingListener implements ServletContextListener {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        registerMBean();
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

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
