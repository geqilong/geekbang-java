package org.geektimes.configuration.microprofile.config.converter.impl;

import org.geektimes.configuration.microprofile.config.converter.MyConverter;

public class DoubleConverter implements MyConverter<Double> {
    @Override
    public Double convert(String s) throws IllegalArgumentException, NullPointerException {
        return Double.valueOf(s);
    }

    @Override
    public String getSourceType() {
        return "Double";
    }
}
