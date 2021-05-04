package com.langthang.event;

import com.langthang.model.entity.Account;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnResetPasswordEvent extends ApplicationEvent {

    private final Account account;
    private final String appUrl;

    public OnResetPasswordEvent(Account account, String appUrl) {
        super(account);
        this.account = account;
        this.appUrl = appUrl;
    }
}
