package com.langthang.event.listener.internal;

import com.github.sonus21.rqueue.annotation.RqueueListener;
import com.langthang.config.RQueueConfig;
import com.langthang.event.model.NotificationRequest;
import com.langthang.model.constraints.NotificationType;
import com.langthang.services.INotificationServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationFactoryListener {

    @Autowired
    private INotificationServices notificationServices;

    @RqueueListener(value = RQueueConfig.QK_NOTIFICATION_FACTORY_QUEUE)
    public void onNewNotificationRequest(NotificationRequest requestInfo) {
        log.debug("Received new notification request: {}", requestInfo);
        if (requestInfo.type().equals(NotificationType.PUBLISHED_NEW_POST)) return;

        notificationServices.createNotification(requestInfo);
    }

}
