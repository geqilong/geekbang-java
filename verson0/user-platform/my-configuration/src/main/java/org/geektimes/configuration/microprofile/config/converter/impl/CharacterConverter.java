package org.geektimes.configuration.microprofile.config.converter.impl;

import org.geektimes.configuration.microprofile.config.converter.AbstractConverter;

public class CharacterConverter extends AbstractConverter<Character> {
    @Override
    protected Character doConvert(String value) {
        if (value.length() > 1) {
            throw new IllegalArgumentException("String Too Long To Convert To Character");
        }
        return value.charAt(0);
    }

}
