package com.salesmanager.core.business.management;

import org.json.simple.JSONAware;

public interface ManageableEmailConfig extends JSONAware {
    boolean isSmtpAuth();

    void setSmtpAuth(boolean smtpAuth);

    boolean isStarttls();

    void setStarttls(boolean starttls);

    void setEmailTemplatesPath(String emailTemplatesPath);

    String getEmailTemplatesPath();

    String getHost();

    void setHost(String host);

    String getPort();

    void setPort(String port);

    String getProtocol();

    void setProtocol(String protocol);

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);
}
