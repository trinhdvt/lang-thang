package com.langthang.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.http.HttpStatus;

@JsonIgnoreProperties({"cause", "stackTrace", "localizedMessage", "suppressed"})
public class HttpError extends RuntimeException {

    private final String message;

    private final HttpStatus httpStatus;

    @lombok.Getter
    private String path;

    public HttpError(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
