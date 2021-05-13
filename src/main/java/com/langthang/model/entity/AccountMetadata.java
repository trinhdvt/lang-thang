package com.langthang.model.entity;

import lombok.Value;

@Value
public class AccountMetadata {
    String email, password;
    Role role;
    boolean enabled;
}
