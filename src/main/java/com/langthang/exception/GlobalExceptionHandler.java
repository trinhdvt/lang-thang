package com.langthang.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.validation.ConstraintViolationException;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxUploadSizeExceed() {
        CustomException customException = new CustomException();
        customException.setMessage("File size cannot exceed " + maxFileSize);
        customException.setStatus(HttpStatus.BAD_REQUEST.value());
        customException.setTimestamp(new Date());

        return new ResponseEntity<>(customException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation() {
        CustomException customException = new CustomException();
        customException.setMessage("Not support file type");
        customException.setStatus(HttpStatus.BAD_REQUEST.value());
        customException.setTimestamp(new Date());

        return new ResponseEntity<>(customException, HttpStatus.BAD_REQUEST);
    }
}
