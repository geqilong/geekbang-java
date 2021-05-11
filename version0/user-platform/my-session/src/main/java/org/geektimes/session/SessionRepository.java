package org.geektimes.session;

import java.util.Set;

/**
 * Session Repository
 */
public interface SessionRepository {
    // SessionInfo manipulation methods

    SessionRepository saveSessionInfo(SessionInfo sessionInfo);

    SessionInfo getSessionInfo(String sessionId);

    SessionRepository removeSessionInfo(String sessionId);

    // Attribute manipulation methods

    SessionRepository setAttribute(String sessionId, String name, Object value);

    SessionRepository removeAttribute(String sessionId, String name);

    Object getAttribute(String sessionId, String name);

    Set<String> getAttributeNames(String sessionId);

    // Lifecycle methods

    void initialize();

    void destroy();
}
