package org.geektimes.cache.serialization;

import com.alibaba.fastjson.JSON;

public class FastJsonParser<T> implements Parsable<T>{

    @Override
    public byte[] serialize(Object obj) {
        return JSON.toJSONString(obj).getBytes();
    }

    @Override
    public T deserialize(byte[] valueBytes, Class<T> clazz) {
        if (null == valueBytes){
            return null;
        }
        return JSON.parseObject(new String(valueBytes), clazz);
    }
}
