package com.langthang.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
                Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
                errorAttributes.remove("exception");
                errorAttributes.remove("trace");
                return errorAttributes;
            }
        };
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxUploadSizeExceed() {
        return new ResponseEntity<>("File size cannot exceed " + maxFileSize, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        return new ResponseEntity<>(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpError.class)
    public ResponseEntity<HttpError> handleCustomException(HttpError ex, HttpServletRequest req) {
        ex.setPath(req.getRequestURI());
        return new ResponseEntity<>(ex, ex.getHttpStatus());
    }


    @ExceptionHandler({SQLException.class, DataAccessException.class})
    public ResponseEntity<Object> handleQueryException(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
