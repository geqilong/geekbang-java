package org.geektimes.projects.user.enums;

public enum UserType { // 底层实际 public final class UserType extends java.lang.Enum
    NORMAL,
    VIP;

    UserType(){ //private
    }

    public static void main(String[] args) {
        System.out.println(UserType.NORMAL.ordinal());
        System.out.println(UserType.VIP.ordinal());
    }
}
