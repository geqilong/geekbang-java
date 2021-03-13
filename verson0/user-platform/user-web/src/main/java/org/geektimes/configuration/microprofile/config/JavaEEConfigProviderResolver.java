package org.geektimes.configuration.microprofile.config;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import java.util.Iterator;
import java.util.ServiceLoader;

public class JavaEEConfigProviderResolver extends ConfigProviderResolver {

    @Override
    public Config getConfig() {
        return null;
    }

    @Override
    public Config getConfig(ClassLoader classLoader) {
        ClassLoader clazzLoader = classLoader;
        if (null == clazzLoader) {
            clazzLoader = Thread.currentThread().getContextClassLoader();
        }
        ServiceLoader<Config> serviceLoader = ServiceLoader.load(Config.class, clazzLoader);
        Iterator<Config> iterator = serviceLoader.iterator();
        return iterator.hasNext() ? iterator.next() : null;//获取Config SPI第一个实现
    }

    @Override
    public ConfigBuilder getBuilder() {
        return null;
    }

    @Override
    public void registerConfig(Config config, ClassLoader classLoader) {

    }

    @Override
    public void releaseConfig(Config config) {

    }
}
