package org.geektimes.configuration.microprofile.config.converter.impl;

import org.geektimes.configuration.microprofile.config.converter.MyConverter;

public class ShortConverter implements MyConverter<Short> {
    @Override
    public Short convert(String s) throws IllegalArgumentException, NullPointerException {
        return Short.valueOf(s);
    }

    @Override
    public String getSourceType() {
        return "Short";
    }
}
