package org.geektimes.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

public interface MyConverter<T> extends Converter<T> {
    String getSourceType();
}
