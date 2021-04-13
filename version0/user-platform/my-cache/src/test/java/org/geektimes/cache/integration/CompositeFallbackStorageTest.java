package org.geektimes.cache.integration;

import org.geektimes.cache.EntryAdapter;
import org.junit.After;
import org.junit.Test;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class CompositeFallbackStorageTest {
    private CompositeFallbackStorage cfStorage = new CompositeFallbackStorage();

    @Test
    public void writeAllAndLoadAll() {
        cfStorage.writeAll(asList(EntryAdapter.of("a", 1), EntryAdapter.of("b", 2), EntryAdapter.of("c", 3)));

        Map map = cfStorage.loadAll(asList("a", "b", "c"));
        assertEquals(1, map.get("a"));
        assertEquals(2, map.get("b"));
        assertEquals(3, map.get("c"));
    }

    @After
    public void deleteAll() {
        cfStorage.deleteAll(asList("a", "b", "c"));
    }
}
