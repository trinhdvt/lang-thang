package com.langthang.dto;

import lombok.Getter;

@Getter
public class JwtDTO {
    private final String token;

    private final int duration = 600000;

    public JwtDTO(String token) {
        this.token = token;
    }

}
