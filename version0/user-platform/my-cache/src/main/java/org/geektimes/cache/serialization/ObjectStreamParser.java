package org.geektimes.cache.serialization;

import javax.cache.CacheException;
import java.io.*;

public class ObjectStreamParser<T> implements Parsable<T>{

    @Override
    public byte[] serialize(Object obj) {
        byte[] bytes = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);) {
            //Key -> byte[]
            objectOutputStream.writeObject(obj);
            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            throw new CacheException(e);
        }
        return bytes;
    }

    @Override
    public T  deserialize(byte[] valueBytes, Class<T> clazz) {
        if (null == valueBytes){
            return null;
        }
        T obj = null;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(valueBytes);
             ObjectInputStream objectOutputStream = new ObjectInputStream(inputStream);) {
            //byte[] -> Value
            obj = (T) objectOutputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new CacheException(e);
        }
        return obj;
    }
}
