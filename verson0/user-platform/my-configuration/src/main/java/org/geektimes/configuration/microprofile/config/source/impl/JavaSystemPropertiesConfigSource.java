package org.geektimes.configuration.microprofile.config.source.impl;

import org.geektimes.configuration.microprofile.config.source.MapBasedConfigSource;

import java.util.Map;

public class JavaSystemPropertiesConfigSource extends MapBasedConfigSource {

    /**
     * Java 系统属性最好通过本地变量保存，使用 Map 保存，尽可能运行期不去调整
     * -Dapplication.name=user-web
     */
    public JavaSystemPropertiesConfigSource() {
        super("Java System Properties", 400);
    }

    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        configData.putAll(System.getProperties());
    }

}
