package com.langthang.dto;

import lombok.Getter;

@Getter
public class JwtDTO {
    private final String token;

    private int duration = 600000;

    public JwtDTO(String token) {
        this.token = token;
    }

    public JwtDTO(String token, int duration) {
        this.token = token;
        this.duration = duration;
    }
}
