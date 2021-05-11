package com.langthang.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnNewNotifyEvent extends ApplicationEvent {
    private final int accountId;

    public OnNewNotifyEvent(int accountId) {
        super(accountId);
        this.accountId = accountId;
    }

}
