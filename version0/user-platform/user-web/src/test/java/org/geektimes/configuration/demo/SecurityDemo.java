package org.geektimes.configuration.demo;

public class SecurityDemo {

    public static void main(String[] args) {
        System.out.println(System.getProperty("java.version"));
        System.out.println(System.getProperty("java.home"));
        System.setSecurityManager(new SecurityManager());
        System.setProperty("java.version", "1.7.0_1");
        System.out.println(System.getProperty("java.version"));
    }
}
