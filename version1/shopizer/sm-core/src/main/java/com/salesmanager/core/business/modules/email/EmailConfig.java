package com.salesmanager.core.business.modules.email;

import org.json.simple.JSONObject;

import javax.management.openmbean.CompositeData;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EmailConfig implements EmailConfigMBean {

    private String host;
    private String port;
    private String protocol;
    private String username;
    private String password;
    private boolean smtpAuth = false;
    private boolean starttls = false;

    private String emailTemplatesPath = null;

    public static EmailConfig from(CompositeData compositeData) throws InvocationTargetException, IllegalAccessException, IntrospectionException {
        EmailConfig emailConfig = new EmailConfig();
        BeanInfo beanInfo = Introspector.getBeanInfo(emailConfig.getClass(), Object.class);
        for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
            String propertyName = propertyDescriptor.getName();
            Object propertyValue = compositeData.get(propertyName);
            Method method = propertyDescriptor.getWriteMethod();
            method.invoke(emailConfig, propertyValue);
        }
        return emailConfig;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String toJSONString() {
        JSONObject data = new JSONObject();
        data.put("host", this.getHost());
        data.put("port", this.getPort());
        data.put("protocol", this.getProtocol());
        data.put("username", this.getUsername());
        data.put("smtpAuth", this.isSmtpAuth());
        data.put("starttls", this.isStarttls());
        data.put("password", this.getPassword());
        return data.toJSONString();
    }

    public boolean isSmtpAuth() {
        return smtpAuth;
    }

    public void setSmtpAuth(boolean smtpAuth) {
        this.smtpAuth = smtpAuth;
    }

    public boolean isStarttls() {
        return starttls;
    }

    public void setStarttls(boolean starttls) {
        this.starttls = starttls;
    }

    public void setEmailTemplatesPath(String emailTemplatesPath) {
        this.emailTemplatesPath = emailTemplatesPath;
    }

    public String getEmailTemplatesPath() {
        return emailTemplatesPath;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
