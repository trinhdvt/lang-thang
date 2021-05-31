package com.langthang.event;

import com.langthang.model.Account;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnRegistrationEvent extends ApplicationEvent {

    private final Account account;
    private final String appUrl;
    private final String token;

    public OnRegistrationEvent(Account account, String appUrl) {
        super(account);
        this.appUrl = appUrl;
        this.account = account;
        this.token = null;
    }

    public OnRegistrationEvent(Account account, String appUrl, String token) {
        super(account);
        this.account = account;
        this.appUrl = appUrl;
        this.token = token;
    }
}
