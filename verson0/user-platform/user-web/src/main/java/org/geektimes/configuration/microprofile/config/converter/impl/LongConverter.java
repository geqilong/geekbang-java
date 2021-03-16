package org.geektimes.configuration.microprofile.config.converter.impl;

import org.geektimes.configuration.microprofile.config.converter.MyConverter;

public class LongConverter implements MyConverter<Long> {
    @Override
    public Long convert(String s) throws IllegalArgumentException, NullPointerException {
        return Long.getLong(s);
    }

    @Override
    public String getSourceType() {
        return "Long";
    }
}
