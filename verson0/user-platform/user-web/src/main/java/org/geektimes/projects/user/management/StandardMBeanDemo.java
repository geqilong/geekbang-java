package org.geektimes.projects.user.management;

import org.geektimes.projects.user.domain.User;

import javax.management.MBeanInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

public class StandardMBeanDemo {

    public static void main(String[] args) throws NotCompliantMBeanException {
        //将静态的MBean接口转化成DynamicMBean
        StandardMBean standardMBean = new StandardMBean(new UserManager(new User()), UserManagerMBean.class);
        MBeanInfo mBeanInfo = standardMBean.getMBeanInfo();
        System.out.println(mBeanInfo);
    }
}
