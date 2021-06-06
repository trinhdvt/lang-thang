package com.langthang.event.listener;

import com.langthang.dto.NotificationDTO;
import com.langthang.event.OnNewNotificationEvent;
import com.langthang.model.Notify;
import com.langthang.utils.Utils;
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
    public void beforeSave(Notify notify) {
        notify.setContent(Utils.escapeHtml(notify.getContent()));
    }

    @PostPersist
    public void afterSaved(Notify newNotify) {
        log.debug("Created notification: {}", newNotify);
        eventPublisher.publishEvent(new OnNewNotificationEvent(NotificationDTO.toNotificationDTO(newNotify)));
    }
}
