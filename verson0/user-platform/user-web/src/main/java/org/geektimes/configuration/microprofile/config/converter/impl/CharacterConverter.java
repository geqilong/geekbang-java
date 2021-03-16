package org.geektimes.configuration.microprofile.config.converter.impl;

import org.apache.commons.lang.StringUtils;
import org.geektimes.configuration.microprofile.config.converter.MyConverter;

public class CharacterConverter implements MyConverter {
    @Override
    public Object convert(String s) throws IllegalArgumentException, NullPointerException {
        if (StringUtils.isEmpty(s)) {
            throw new IllegalArgumentException("Empty String");
        }
        if (s.length() > 1) {
            throw new IllegalArgumentException("String Too Long To Convert To Character");
        }
        return s.charAt(0);
    }

    @Override
    public String getSourceType() {
        return "Character";
    }
}
