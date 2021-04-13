package org.geektimes.cache.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JacksonParser<T> implements Parsable<T> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object obj) {
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T deserialize(byte[] valueBytes, Class<T> clazz) {
        if (null == valueBytes){
            return null;
        }
        try {
            return mapper.readValue(valueBytes, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
