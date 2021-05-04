package com.langthang.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
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
        CustomResponse customResponse = new CustomResponse();
        customResponse.setMessage("File size cannot exceed " + maxFileSize);
        customResponse.setStatus(HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(customResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        CustomResponse customResponse = new CustomResponse();
        customResponse.setMessage(ex.getMessage());
        customResponse.setStatus(HttpStatus.BAD_REQUEST.value());

        return new ResponseEntity<>(customResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomException.class)
    public void handleCustomException(HttpServletResponse res, CustomException ex) throws IOException {
        res.sendError(ex.getHttpStatus().value(), ex.getMessage());
    }
}
