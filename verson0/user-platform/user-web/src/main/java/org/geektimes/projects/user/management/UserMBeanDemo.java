package org.geektimes.projects.user.management;

import org.geektimes.projects.user.domain.User;

import javax.management.*;
import java.lang.management.ManagementFactory;

public class UserMBeanDemo {
    public static void main(String[] args) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException, InterruptedException {
        //获取MBean平台Server
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName objectName = new ObjectName("org.geektimes.projects.user.management:type=User");
        User user = new User();
        mBeanServer.registerMBean(createUserMBean(user), objectName);
        while (true) {
            Thread.sleep(2000);
            System.out.println(user);
        }
    }

    private static Object createUserMBean(User user) {
        return new UserManager(user);
    }
}
