package com.langthang.exception;

import org.springframework.http.HttpStatus;

public class InternalError extends HttpError {
    public InternalError(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
