package org.geektimes.projects.spring.cloud.service.redisbridge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.event.EnvironmentChangeRemoteApplicationEvent;
import org.springframework.context.ApplicationListener;

public class EnvironmentChangeListener implements ApplicationListener<EnvironmentChangeRemoteApplicationEvent> {

    @Autowired
    private MessageProcessor messageProcessor;

    @Override
    public void onApplicationEvent(EnvironmentChangeRemoteApplicationEvent environmentChangeRemoteApplicationEvent) {
        //TODO do something
    }
}
