package com.langthang.model;


import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_MEMBER, ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}