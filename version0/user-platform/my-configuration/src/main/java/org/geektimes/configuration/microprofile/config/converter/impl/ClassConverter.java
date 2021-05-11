package org.geektimes.configuration.microprofile.config.converter.impl;

import org.geektimes.configuration.microprofile.config.converter.AbstractConverter;

public class ClassConverter extends AbstractConverter<Class> {
    private final ClassLoader classLoader;

    public ClassConverter() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public ClassConverter(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }


    @Override
    protected Class doConvert(String value) throws Throwable {
        return classLoader.loadClass(value);
    }
}
