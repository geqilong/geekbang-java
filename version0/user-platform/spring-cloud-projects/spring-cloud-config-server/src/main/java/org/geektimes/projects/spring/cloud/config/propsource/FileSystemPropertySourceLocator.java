package org.geektimes.projects.spring.cloud.config.propsource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

@Order
public class FileSystemPropertySourceLocator implements PropertySourceLocator {
    private static Logger logger = LoggerFactory.getLogger(FileSystemPropertySourceLocator.class);
    private static final String resourceFileName = "default.properties";
    private static final String resourcePath = "/META-INF/config" + resourceFileName;
    private static final String ENCODING = "UTF-8";
    private final ResourcePropertySource resourcePropertySource;
    private final ExecutorService executorService;
    private final String sourceName;

    public FileSystemPropertySourceLocator() {
        this.executorService = Executors.newSingleThreadExecutor();
        //监听资源文件 （Java NIO 2 WatchService）
        onMessagePropertiesChanged();
        sourceName = "FileSystemPropertySource";
        resourcePropertySource = initializeResourcePropertySource();
    }

    private ResourcePropertySource initializeResourcePropertySource() {
        ResourcePropertySource tempResourcePropertySource;
        try {
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            Resource resource = resourceLoader.getResource(resourcePath);
            tempResourcePropertySource = new ResourcePropertySource(sourceName, new EncodedResource(resource, ENCODING));
        } catch (IOException e) {
            logger.error("Error initializing resourcePropertySource", e);
            tempResourcePropertySource = null;
        }
        return tempResourcePropertySource;
    }

    /**
     * 监听资源文件
     */
    private void onMessagePropertiesChanged() {
        //获取对应文件系统中的文件
        try {
            Path messagePropertiesFilePath = Paths.get(resourcePath);
            //获取当前OS文件系统，新建WatchService
            WatchService watchService = FileSystems.getDefault().newWatchService();
            //获取资源文件所在目录
            Path dirPath = messagePropertiesFilePath.getParent();
            //注册WatchService到dirPath，且关心修改事件
            dirPath.register(watchService, ENTRY_MODIFY);
            processMessagePropertiesChanged(watchService);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public PropertySource<?> locate(Environment environment) {
        return resourcePropertySource;
    }

    /**
     * 处理文件变化事件
     *
     * @param watchService
     */
    private void processMessagePropertiesChanged(WatchService watchService) {
        executorService.submit(() -> {
            while (true) {
                WatchKey watchKey = watchService.take(); //take 发生阻塞，有变化再发通知
                try {
                    //watchKey是否有效
                    System.out.println((null != watchKey) ? watchKey.isValid() : false);
                    if (null != watchKey && watchKey.isValid()) {
                        for (WatchEvent event : watchKey.pollEvents()) {
                            Watchable watchable = watchKey.watchable();
                            //目录路径（监听的注册目录）
                            Path dirPath = (Path) watchable;
                            //事件所关联的对象即注册目录的子文件（或子目录）
                            //事件发生源是相对路径
                            Path fileRelativePath = (Path) event.context();
                            System.out.println("fileRelativePath:" + fileRelativePath);
                            if (resourceFileName.equals(fileRelativePath.getFileName().toString())) {
                                //处理为绝对路径
                                Path filePath = dirPath.resolve(fileRelativePath);
                                File file = filePath.toFile();
                                Properties properties = loadMessageProperties(new FileReader(file));
                                synchronized (resourcePropertySource) {
                                    Map<String, Object> sourceMap = resourcePropertySource.getSource();
                                    sourceMap.putAll(new HashMap(properties));
                                }
                            }
                        }
                    }
                } finally {
                    if (null != watchKey) {
                        watchKey.reset();//重置watchKey
                    }
                }
            }
        });
    }

    private Properties loadMessageProperties(Reader reader) {
        Properties properties = new Properties();
        try {
            try {
                properties.load(reader);
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
        } catch (IOException e) {
            logger.error("Error load content", e);
        }
        return properties;
    }

}
