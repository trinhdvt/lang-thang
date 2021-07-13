package com.langthang.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedError extends HttpError {

    public UnauthorizedError(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
