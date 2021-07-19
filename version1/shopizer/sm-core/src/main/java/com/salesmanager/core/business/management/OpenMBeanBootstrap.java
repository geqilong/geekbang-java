package com.salesmanager.core.business.management;

import com.salesmanager.core.business.modules.email.Email;
import com.salesmanager.core.business.modules.email.EmailConfig;
import com.salesmanager.core.business.modules.email.HtmlEmailSender;

import javax.management.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;

public class OpenMBeanBootstrap {

    public static void main(String[] args) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException, IOException {
        HtmlEmailSender htmlEmailSender = new HtmlEmailSender() {
            private EmailConfig emailConfig = new EmailConfig();

            {
                emailConfig.setHost("127.0.0.1");
                emailConfig.setPort("25");
                emailConfig.setProtocol("https");
                emailConfig.setEmailTemplatesPath("/email");
                emailConfig.setSmtpAuth(true);
                emailConfig.setStarttls(true);
                emailConfig.setUsername("anonymous");
                emailConfig.setPassword("password");
            }

            @Override
            public void send(Email email) throws Exception {

            }

            @Override
            public void setEmailConfig(EmailConfig emailConfig) {
                System.out.println(emailConfig.toJSONString());
                this.emailConfig = emailConfig;
            }

            @Override
            public EmailConfig getEmailConfig() {
                return emailConfig;
            }
        };

        //Create a MBean instance
        HtmlEmailSenderOpenMBean htmlEmailSenderOpenMBean = new HtmlEmailSenderOpenMBean(htmlEmailSender);
        //MBean interface
        //Create an ObjectName for MBean
        String packageName = HtmlEmailSenderOpenMBean.class.getPackage().getName();
        String simpleClassName = HtmlEmailSenderOpenMBean.class.getSimpleName();
        ObjectName objectName = new ObjectName(packageName + ":type=" + simpleClassName);
        //Get the MBeanServer
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        //Register MBean
        mBeanServer.registerMBean(htmlEmailSenderOpenMBean, objectName);
        System.out.println("Press any key to exit");
        System.in.read();
    }

}
