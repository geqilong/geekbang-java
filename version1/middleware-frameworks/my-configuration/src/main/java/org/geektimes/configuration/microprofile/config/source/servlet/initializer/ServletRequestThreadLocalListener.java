package org.geektimes.configuration.microprofile.config.source.servlet.initializer;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

/**
 * The {@link ServletRequestListener} implementation for the {@link ThreadLocal} of {@link ServletRequest}
 * TODO
 * @ServletComponentScan(basePackages = “org.geektimes.configuration.microprofile.config.source.servlet.initializer”)
 */
@WebListener
public class ServletRequestThreadLocalListener implements ServletRequestListener {

    private static ThreadLocal<HttpServletRequest> requestThreadLocal = new ThreadLocal<>();

    public static HttpServletRequest getRequest() {
        return requestThreadLocal.get();
    }

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        ServletRequest servletRequest = sre.getServletRequest();
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        requestThreadLocal.set(httpServletRequest);
    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        requestThreadLocal.remove();
    }
}
