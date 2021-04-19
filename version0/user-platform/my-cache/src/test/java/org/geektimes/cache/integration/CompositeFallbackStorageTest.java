package org.geektimes.cache.integration;

import org.geektimes.cache.ExpirableEntry;
import org.junit.After;
import org.junit.Test;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class CompositeFallbackStorageTest {
    private CompositeFallbackStorage cfStorage = new CompositeFallbackStorage();

    @Test
    public void writeAllAndLoadAll() {
        cfStorage.writeAll(asList(ExpirableEntry.of("a", 1), ExpirableEntry.of("b", 2), ExpirableEntry.of("c", 3)));

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
