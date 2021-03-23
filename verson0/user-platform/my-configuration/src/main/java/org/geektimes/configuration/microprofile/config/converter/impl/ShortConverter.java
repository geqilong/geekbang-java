package org.geektimes.configuration.microprofile.config.converter.impl;

import org.geektimes.configuration.microprofile.config.converter.AbstractConverter;

public class ShortConverter extends AbstractConverter<Short> {

    @Override
    protected Short doConvert(String value) {
        return Short.valueOf(value);
    }

}
