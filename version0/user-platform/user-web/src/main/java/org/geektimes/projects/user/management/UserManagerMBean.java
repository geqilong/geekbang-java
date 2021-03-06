package org.geektimes.projects.user.management;

/**
 * MBean接口描述
 */
public interface UserManagerMBean {

    //MBeanAttributeInfo
    Long getId();

    void setId(Long id);

    String getName();

    void setName(String name);

    String getPassword();

    void setPassword(String password);

    String getEmail();

    void setEmail(String email);

    String getPhoneNumber();

    void setPhoneNumber(String phoneNumber);

    //MBeanOperationInfo
    String toString();
}
