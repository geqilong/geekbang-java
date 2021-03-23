package org.geektimes.configuration.microprofile.config.source.impl;

import org.geektimes.configuration.microprofile.config.source.MapBasedConfigSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public class DefaultResourceConfigResource extends MapBasedConfigSource {
    private static final String configFileLocation = "META-INF/*.properties";
    private final static String CONFIG_FILE_SUFFIX = "properties";
    private final Logger logger  = Logger.getLogger(getClass().getName());

    public DefaultResourceConfigResource() {
        super("Config Files", 100);
    }

    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "/META-INF";// try /META-INF
        File file = new File(rootPath);
        configData.putAll(locateAndRead(file));
    }

    private Map<String, String> locateAndRead(File file) throws Throwable {
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

    private Map<String, String> loadProperties(File file) throws Throwable {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new IllegalArgumentException("加载配置文件异常", e);
        }
        return new HashMap(properties);
    }
}
