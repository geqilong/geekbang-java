package org.geektimes.cache.integration;

import org.geektimes.cache.EntryAdapter;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FileFallbackStorageTest {
    private FileFallbackStorage fileFallbackStorage = new FileFallbackStorage();

    @Test
    public void writeAllAndLoadAll() {
        assertNull(fileFallbackStorage.load("a"));
        assertNull(fileFallbackStorage.load("b"));
        assertNull(fileFallbackStorage.load("c"));
        fileFallbackStorage.writeAll(asList(EntryAdapter.of("a", 1), EntryAdapter.of("b", 2), EntryAdapter.of("c", 3)));

        Map map = fileFallbackStorage.loadAll(asList("a", "b", "c"));
        assertEquals(1, map.get("a"));
        assertEquals(2, map.get("b"));
        assertEquals(3, map.get("c"));
        fileFallbackStorage.write(EntryAdapter.of("a", new ArrayList()));
    }

    @After
    public void deleteAll() {
        fileFallbackStorage.deleteAll(asList("a", "b", "c"));
    }
}
