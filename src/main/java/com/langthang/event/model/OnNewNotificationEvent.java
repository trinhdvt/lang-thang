package com.langthang.event.model;

import com.langthang.model.dto.response.NotificationDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnNewNotificationEvent extends ApplicationEvent {

    private final transient NotificationDTO notification;

    public OnNewNotificationEvent(NotificationDTO notification) {
        super(notification);
        this.notification = notification;
    }
}