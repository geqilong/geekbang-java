package org.geektimes.cache;

import java.util.HashMap;
import java.util.Map;

public class InMemoryDataRepository implements DataRepository {
    private final Map<String, Object> storage = new HashMap<>();

    @Override
    public boolean create(String name, Object value) {
        return storage.put(name, value) == null;
    }

    @Override
    public boolean remove(String name) {
        return storage.remove(name) != null;
    }

    @Override
    public Object get(String name) {
        return storage.get(name);
    }
}
