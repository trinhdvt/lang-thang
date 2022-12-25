package com.langthang.model.dto.response;

import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Data
public class JwtTokenDTO implements Serializable {
    private final String token;

    private int duration;

    public JwtTokenDTO(String token, int duration) {
        this.token = token;
        this.duration = duration;
    }
}