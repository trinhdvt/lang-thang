package com.langthang.exception;

import org.springframework.http.HttpStatus;

public class NotFoundError extends HttpError {

    public NotFoundError(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
