package com.salesmanager.core.business.modules.management;

import com.salesmanager.core.business.modules.email.EmailConfig;
import com.salesmanager.core.business.modules.email.EmailConfigMBean;

import javax.management.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 *   -Dcom.sun.management.jmxremote.port=%my.jmx.port%
 *   -Dcom.sun.management.jmxremote.rmi.port=%my.rmi.port%
 *   -Dcom.sun.management.jmxremote.ssl=false
 *   -Dcom.sun.management.jmxremote.authenticate=false
 */
public class EmailConfigMBeanBootstrap {
    public static void main(String[] args) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException, IOException {
        //Create MBean instance
        EmailConfig emailConfig = new EmailConfig();
        //Register MBean
        registerMBean(EmailConfigMBean.class, emailConfig);
        registerMXBean(EmailConfigMBean.class, emailConfig);
        System.out.println("Done");
        System.in.read();
    }

    private static void registerMBean(Class intrefaceClass, Object instance) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
        String packageName = intrefaceClass.getPackage().getName();
        String simpleClassName = intrefaceClass.getSimpleName();
        //Get MBeanServer
        ObjectName objectName = new ObjectName(packageName + ":type=" + simpleClassName);
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        //Register MBean
        mBeanServer.registerMBean(instance, objectName);
    }

    private static void registerMXBean(Class intrefaceClass, Object instance) {

    }
}
