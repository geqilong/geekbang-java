package org.geektimes.configuration.microprofile.config.converter.impl;

import org.geektimes.configuration.microprofile.config.converter.MyConverter;

public class FloatConverter implements MyConverter {
    @Override
    public Object convert(String s) throws IllegalArgumentException, NullPointerException {
        return Float.valueOf(s);
    }

    @Override
    public String getSourceType() {
        return "Float";
    }
}
