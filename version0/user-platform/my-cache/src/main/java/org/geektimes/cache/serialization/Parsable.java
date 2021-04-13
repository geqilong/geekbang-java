package org.geektimes.cache.serialization;

public interface Parsable<T> {
    byte[] serialize(Object obj);

    T deserialize(byte[] valueBytes, Class<T> clazz);

}
