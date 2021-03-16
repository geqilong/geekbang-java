package org.geektimes.configuration.microprofile.config.converter.impl;

import org.geektimes.configuration.microprofile.config.converter.MyConverter;

public class ByteConverter implements MyConverter<Byte> {
    @Override
    public Byte convert(String s) throws IllegalArgumentException, NullPointerException {
        return Byte.valueOf(s);
    }

    @Override
    public String getSourceType() {
        return "Byte";
    }
}
