package com.langthang.security;

import com.langthang.model.Role;
import lombok.Getter;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;

@Getter
public class CurrentUser extends User {

    private final Integer userId;
    private final Role role;

    public CurrentUser(String username, String password, boolean enabled, Integer userId, Role role) {
        super(username, password,
                enabled, true,
                true, true,
                Collections.singletonList(role));

        this.userId = userId;
        this.role = role;
    }

    @Override
    public String toString() {
        return getClass().getName() + " [" +
                "Email=" + getUsername() + ", " +
                "ID=" + getUserId() + ", " +
                "Enabled=" + isEnabled() + ", " +
                "Role=" + getRole() + "]";
    }
}