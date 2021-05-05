package org.geektimes.session.servlet;

public interface Session {
    Session id(String id);

    String getId();

    Session createTime(long creationTime);

    long getCreationTime();

    void lastAccessedTime(long lastAccessedTime);
}
