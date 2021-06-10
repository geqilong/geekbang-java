package org.geektimes.projects.spring.cloud.service.redisbridge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.event.RefreshRemoteApplicationEvent;
import org.springframework.context.ApplicationListener;

public class RefreshListener implements ApplicationListener<RefreshRemoteApplicationEvent> {
    @Autowired
    private MessageProcessor messageProcessor;

    @Override
    public void onApplicationEvent(RefreshRemoteApplicationEvent refreshRemoteApplicationEvent) {
        //TODO do something
    }
}
