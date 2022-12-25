package com.langthang.security.services;

import com.langthang.model.constraints.Role;
import com.langthang.model.entity.Account;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

@Getter
@EqualsAndHashCode(callSuper = false)
public class CurrentUser extends User {

    private final Integer userId;
    private final Role role;
    private final Account source;

    public CurrentUser(String username, String password, boolean enabled, Integer userId, Role role, Account source) {
        super(username, password,
                enabled, true,
                true, true,
                Collections.singletonList(role));

        this.userId = userId;
        this.role = role;
        this.source = source;
    }
}