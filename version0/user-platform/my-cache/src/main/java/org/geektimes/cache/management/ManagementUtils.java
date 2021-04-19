package org.geektimes.cache.management;

import javax.cache.Cache;
import javax.cache.configuration.CompleteConfiguration;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Hashtable;

public class ManagementUtils {

    private static Object createCacheMXBean(CompleteConfiguration configuration) {
        return new CacheMXBeanAdapter(configuration);
    }

    public static void registerCacheMXBeanIfRequired(Cache<?, ?> cache) {
        CompleteConfiguration configuration = cache.getConfiguration(CompleteConfiguration.class);
        if (configuration.isManagementEnabled()) {
            ObjectName objectName = createObjectName(cache, "CacheConfiguration");
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            try {
                if (!mBeanServer.isRegistered(objectName)) {
                    mBeanServer.registerMBean(createCacheMXBean(configuration), objectName);
                }
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    private static ObjectName createObjectName(Cache<?, ?> cache, String type) {
        Hashtable<String, String> props = new Hashtable<>();
        props.put("type", type);
        props.put("name", cache.getName());
        props.put("uri", getUri(cache));
        ObjectName objectName = null;
        try {
            objectName = new ObjectName("javax.cache", props);
        } catch (MalformedObjectNameException e) {
            throw new IllegalArgumentException(e);
        }
        return objectName;
    }

    private static String getUri(Cache<?, ?> cache) {
        URI uri = cache.getCacheManager().getURI();
        try {
            return URLEncoder.encode(uri.toASCIIString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
