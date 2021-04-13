package org.geektimes.cache;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class KeyValueTypePairTest {

    @Test
    public void testResolve() {
        KeyValueTypePair keyValueTypePair = KeyValueTypePair.resolve(InMemoryCache.class);
        assertEquals(Object.class, keyValueTypePair.getKeyType());
        assertEquals(Object.class, keyValueTypePair.getValueType());
    }
}
