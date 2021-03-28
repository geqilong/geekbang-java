package org.geektimes.configuration.microprofile.config.converter.impl;

import org.geektimes.configuration.microprofile.config.converter.AbstractConverter;

public class StringConverter extends AbstractConverter<String> {

    @Override
    protected String doConvert(String value) {
        return value;
    }
}
