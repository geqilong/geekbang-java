package org.geektimes.configuration.microprofile.config.converter.impl;


import org.geektimes.configuration.microprofile.config.converter.AbstractConverter;

public class BooleanConverter extends AbstractConverter<Boolean> {
    @Override
    protected Boolean doConvert(String value) {
        return Boolean.getBoolean(value);
    }

}
