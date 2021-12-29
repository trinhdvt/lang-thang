package com.langthang.event.listener;

import com.langthang.model.dto.response.NotificationDTO;
import com.langthang.event.OnNewNotificationEvent;
import com.langthang.model.entity.Notification;
import com.langthang.utils.MyStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.persistence.PostPersist;
import javax.persistence.PrePersist;

@Component
@Slf4j
public class NotificationEntityListener {

    private static ApplicationEventPublisher eventPublisher;

    @Autowired
    public void init(ApplicationEventPublisher eventPublisher) {
        NotificationEntityListener.eventPublisher = eventPublisher;
    }

    @PrePersist
    public void beforeSave(Notification notification) {
        notification.setContent(MyStringUtils.escapeHtml(notification.getContent()));
    }

    @PostPersist
    public void afterSaved(Notification newNotification) {
        log.debug("Created notification: {}", newNotification);
        eventPublisher.publishEvent(new OnNewNotificationEvent(NotificationDTO.toNotificationDTO(newNotification)));
    }
}