package org.geektimes.cache.integration;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import java.io.*;
import java.util.logging.Logger;

import static java.lang.String.format;

public class FileFallbackStorage extends AbstractFallbackStorage {
    private static final File CACHE_FALLBACK_DIRECTORY = new File("./cache/fallback/");
    private final Logger logger = Logger.getLogger(getClass().getName());

    static {
        if (!CACHE_FALLBACK_DIRECTORY.exists() && !CACHE_FALLBACK_DIRECTORY.mkdirs()) {
            throw new RuntimeException(format("The fallback directory[path:%s] can't be created!",
                    CACHE_FALLBACK_DIRECTORY.getAbsolutePath()));
        }
    }

    public FileFallbackStorage() {
        super(Integer.MAX_VALUE);
    }


    @Override
    public Object load(Object key) throws CacheLoaderException {
        File storageFile = toStorage(key);
        if (!storageFile.exists() || !storageFile.canRead()) {
            logger.warning(format("The storage file[path:%s] doesn't exist or can't be read, unable to load", storageFile.getAbsolutePath()));
            return null;
        }
        Object value = null;
        try (FileInputStream inputStream = new FileInputStream(storageFile);
             ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            value = objectInputStream.readObject();
        } catch (ClassNotFoundException | IOException e) {
            logger.severe(format("The deserialization of value[%s] failed, cause:%s", key, e.getCause()));
        }
        return value;
    }

    @Override
    public void write(Cache.Entry entry) throws CacheWriterException {
        Object key = entry.getKey();
        Object value = entry.getValue();
        File storageFile = toStorage(key);
        if (!storageFile.exists()) {
            try {
                storageFile.createNewFile();
            } catch (IOException e) {
                logger.warning(format("The storage file[path:%s] can't be created, cause:%s", storageFile.getAbsolutePath(), e.getCause()));
            }
        }
        if (!storageFile.canWrite()) {
            logger.warning(format("The storage file[path:%s] can't be written, entry not stored", storageFile.getAbsolutePath()));
            return;
        }
        try (FileOutputStream outputStream = new FileOutputStream(storageFile);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);) {
            objectOutputStream.writeObject(value);
        } catch (IOException e) {
            logger.severe(format("The serialization of value[%s] failed, cause:%s", value, e.getCause()));
        }
    }

    @Override
    public void delete(Object key) throws CacheWriterException {
        File storageFile = toStorage(key);
        storageFile.delete();
    }

    File toStorage(Object key) {
        return new File(CACHE_FALLBACK_DIRECTORY, key.toString() + ".dat");
    }

    @Override
    public void destroy() {
        destroyFallbackDirs();
    }

    private void destroyFallbackDirs() {
     if (CACHE_FALLBACK_DIRECTORY.exists()){
         // Delete all files into directory
         for (File storageFile : CACHE_FALLBACK_DIRECTORY.listFiles()) {
             storageFile.delete();
         }
     }
    }
}
