package org.geektimes.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigFilesConfigSource implements ConfigSource {
    private Logger logger = Logger.getLogger(getClass().getName());
    private final static String CONFIG_FILE_SUFFIX = "properties";
    private final Map<String, String> properties;

    public ConfigFilesConfigSource() {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "/META-INF";
        File file = new File(rootPath);
        this.properties = locateAndRead(file);
    }

    private Map<String, String> locateAndRead(File file) {
        Map<String, String> allProperties = new HashMap<>();
        if (file.isDirectory()) {
            for (File ele : file.listFiles()) {
                allProperties.putAll(locateAndRead(ele));
            }
        } else {
            if (file.getName().endsWith(CONFIG_FILE_SUFFIX)) {
                allProperties.putAll(loadProperties(file));
            }
        }
        return allProperties;
    }

    private Map<String, String> loadProperties(File file) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading file" + file.getAbsolutePath() + ", cause:" + e.getMessage());
        }
        return new HashMap(properties);
    }

    @Override
    public Set<String> getPropertyNames() {
        return this.properties.keySet();
    }

    @Override
    public String getValue(String s) {
        return this.properties.get(s);
    }

    @Override
    public String getName() {
        return "ConfigFilesProperties";
    }

}
