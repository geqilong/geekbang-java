package org.geektimes.configuration.microprofile.config.source;

import java.util.Enumeration;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class EnumerableConfigSource extends MapBasedConfigSource {

    protected EnumerableConfigSource(String name, int ordinal) {
        super(name, ordinal);
    }

    @Override
    protected final void prepareConfigData(Map configData) throws Throwable {
        prepareConfigData(configData, namesSupplier(), valueResolver());
    }

    private void prepareConfigData(Map configData,
                                   Supplier<Enumeration<String>> namesSupplier,
                                   Function<String, String> valueResolver) {
        Enumeration<String> names = namesSupplier.get();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String value = valueResolver.apply(name);
            configData.put(name, value);
        }
    }

    protected abstract Supplier<Enumeration<String>> namesSupplier();

    protected abstract Function<String, String> valueResolver();
}
