package com.langthang.exception;

import org.springframework.http.HttpStatus;

public class BadRequestError extends HttpError {
    public BadRequestError(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
