package org.geektimes.session;

import javax.servlet.http.HttpSession;

/**
 *  A POJO presents {@link HttpSession}'s info
 */
public class SessionInfo {
    private String id;
    private long creationTime;
    private long lastAccessedTime;
    private int maxInactiveInterval;

    public SessionInfo() {
    }

    public SessionInfo(HttpSession source) {
        setId(source.getId());
        setCreationTime(source.getCreationTime());
        setLastAccessedTime(source.getLastAccessedTime());
        setMaxInactiveInterval(source.getMaxInactiveInterval());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void setLastAccessedTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    public void setMaxInactiveInterval(int maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }
}
