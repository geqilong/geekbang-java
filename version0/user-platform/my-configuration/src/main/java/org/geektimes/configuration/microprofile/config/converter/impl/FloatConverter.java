package org.geektimes.configuration.microprofile.config.converter.impl;

import org.geektimes.configuration.microprofile.config.converter.AbstractConverter;

public class FloatConverter extends AbstractConverter<Float> {

    @Override
    protected Float doConvert(String value) {
        return Float.valueOf(value);
    }
}
