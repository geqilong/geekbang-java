package org.geektimes.configuration.microprofile.config.converter.impl;

import org.geektimes.configuration.microprofile.config.converter.MyConverter;

public class BooleanConverter implements MyConverter {
    @Override
    public Object convert(String s) throws IllegalArgumentException, NullPointerException {
        return Boolean.getBoolean(s);
    }

    @Override
    public String getSourceType() {
        return "Boolean";
    }
}
