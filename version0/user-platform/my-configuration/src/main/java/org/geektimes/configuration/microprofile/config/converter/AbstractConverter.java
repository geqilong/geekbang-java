package org.geektimes.configuration.microprofile.config.converter;;

import org.eclipse.microprofile.config.spi.Converter;

public abstract class AbstractConverter<T> implements Converter<T> {

    @Override
    public T convert(String value) throws IllegalArgumentException, NullPointerException {
        if (null == value){
            throw new NullPointerException("The Value Must Not Be Null");
        }
        return doConvert(value);
    }

    protected abstract T doConvert(String value);
}
