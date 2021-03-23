package org.geektimes.configuration.microprofile.config.converter;;

import org.eclipse.microprofile.config.spi.Converter;

public class PrioritizedConverter <T> implements Converter<T>, Comparable<PrioritizedConverter<T>> {

    private final Converter<T> converter;
    private final int priority;

    public PrioritizedConverter(Converter<T> converter, int priority) {
        this.converter = converter;
        this.priority = priority;
    }

    public Converter<T> getConverter() {
        return converter;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(PrioritizedConverter<T> other) {
        return Integer.compare(other.getPriority(), this.getPriority());
    }

    @Override
    public T convert(String value) throws IllegalArgumentException, NullPointerException {
        return converter.convert(value);
    }
}
