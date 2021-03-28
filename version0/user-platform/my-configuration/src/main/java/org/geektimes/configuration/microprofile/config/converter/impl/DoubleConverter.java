package org.geektimes.configuration.microprofile.config.converter.impl;

import org.geektimes.configuration.microprofile.config.converter.AbstractConverter;

public class DoubleConverter extends AbstractConverter<Double> {

    @Override
    protected Double doConvert(String value) {
        return Double.valueOf(value);
    }

}
