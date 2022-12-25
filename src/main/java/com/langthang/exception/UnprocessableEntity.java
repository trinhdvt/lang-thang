package com.langthang.exception;

import org.springframework.http.HttpStatus;

public class UnprocessableEntity extends HttpError {

    public UnprocessableEntity(String message) {
        super(message, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
