package org.geektimes.projects.context;

import java.util.List;

public interface ComponentContext {

    //生命周期方法

    /**
     * 初始化
     */
    void init();

    /**
     * 销毁
     */
    void destroy();

    /**
     * 根据名称获取组件
     *
     * @param name
     * @param <C>
     * @return
     */
    <C> C getComponent(String name);

    /**
     * 获取所有组件名称
     *
     * @return
     */
    List<String> getComponentNames();
}
