package com.langthang.dto;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class JwtTokenDTO {
    private final String token;

    private int duration = 600000;

    public JwtTokenDTO(String token) {
        this.token = token;
    }

    public JwtTokenDTO(String token, int duration) {
        this.token = token;
        this.duration = duration;
    }
}
