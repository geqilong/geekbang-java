package com.salesmanager.core.business.management;

import javax.management.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class DynamicMBeanBootstrap {
    public static void main(String[] args) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException, IOException {
        //Create a MBean instance
        DynamicEmailConfigMBean dynamicEmailConfigMBean = new DynamicEmailConfigMBean();
        //MBean interface
        //Create an ObjectName for MBean
        String packageName = DynamicEmailConfigMBean.class.getPackage().getName();
        String simpleClassName = DynamicEmailConfigMBean.class.getSimpleName();
        ObjectName objectName = new ObjectName(packageName + ":type=" + simpleClassName);
        //Get the MBeanServer
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        //Register MBean
        mBeanServer.registerMBean(dynamicEmailConfigMBean, objectName);
        System.out.println("Press any key to exit");
        System.in.read();
    }
}
