package com.langthang.event;

import com.langthang.model.Account;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnRegisterWithGoogle extends ApplicationEvent {
    private final Account account;
    private final String rawPassword;

    public OnRegisterWithGoogle(Account account, String rawPassword) {
        super(account);
        this.account = account;
        this.rawPassword = rawPassword;
    }

}
