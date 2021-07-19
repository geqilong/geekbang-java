package com.salesmanager.core.business.management;

import com.salesmanager.core.business.modules.email.EmailConfig;
import com.salesmanager.core.business.modules.email.EmailConfigMBean;

import javax.management.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class StandardMBeanBootstrap {
    public static void main(String[] args) throws IOException, NotCompliantMBeanException, InstanceAlreadyExistsException, MalformedObjectNameException, MBeanRegistrationException {
        //Create a MBean instance
        EmailConfig emailConfig = new EmailConfig();
        //MBean interface
        registerMBean(EmailConfigMBean.class, emailConfig);
        // MXBean interface
        registerMBean(EmailConfigMXBean.class, emailConfig);
        // @MXBean annotated interface
        registerMBean(MXBeanAnnotatedEmailConfig.class, emailConfig);
        System.out.println("Press any key to exit");
        System.in.read();
    }

    private static void registerMBean(Class mBeanInterface, Object mBeanInstance)
            throws NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException, MalformedObjectNameException {
        //Create an ObjectName for MBean
        String packageName = mBeanInterface.getPackage().getName();
        String simpleClassName = mBeanInterface.getSimpleName();
        ObjectName objectName = new ObjectName(packageName + ":type=" + simpleClassName);
        //Get the MBeanServer
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        //Register MBean
        mBeanServer.registerMBean(mBeanInstance, objectName);
    }
}
