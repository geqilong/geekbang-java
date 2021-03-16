package org.geektimes.configuration.microprofile.config.converter.impl;

import org.geektimes.configuration.microprofile.config.converter.MyConverter;

public class IntegerConverter implements MyConverter {

    @Override
    public Object convert(String s) throws IllegalArgumentException, NullPointerException {
        return Integer.getInteger(s);
    }

    @Override
    public String getSourceType() {
        return "Integer";
    }
}
