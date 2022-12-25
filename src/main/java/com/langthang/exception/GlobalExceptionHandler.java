package com.langthang.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.sql.SQLException;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    public GlobalExceptionHandler() {
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxUploadSizeExceed(HttpServletRequest req) {
        String message = "File size cannot exceed " + maxFileSize;
        HttpError err = new HttpError(message, HttpStatus.BAD_REQUEST);
        err.setPath(req.getRequestURI());

        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        HttpError err = new HttpError(ex.getMessage(), HttpStatus.BAD_REQUEST);
        err.setPath(req.getRequestURI());

        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpError.class)
    public ResponseEntity<HttpError> handleCustomException(HttpError ex, HttpServletRequest req) {
        ex.setPath(req.getRequestURI());
        return new ResponseEntity<>(ex, ex.getHttpStatus());
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> handleBindException(BindException ex, HttpServletRequest req) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        var firstMessage = fieldErrors.get(0).getDefaultMessage();

        HttpError error = new HttpError(firstMessage, HttpStatus.BAD_REQUEST);
        error.setPath(req.getRequestURI());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler({SQLException.class, DataAccessException.class})
    public ResponseEntity<Object> handleQueryException(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
