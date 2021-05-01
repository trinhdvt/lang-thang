package com.langthang.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CustomResponse {
    private String message;
    private int status;
    private Date timestamp = new Date();

    public CustomResponse() {
    }

    public CustomResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }
}
