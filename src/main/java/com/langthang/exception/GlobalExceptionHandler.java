package com.langthang.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    private final ObjectMapper jacksonMapper;

    public GlobalExceptionHandler(ObjectMapper jacksonMapper) {
        this.jacksonMapper = jacksonMapper;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxUploadSizeExceed() {
        return new ResponseEntity<>("File size cannot exceed " + maxFileSize, HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<?> handleBindException(BindException ex, HttpServletRequest req) throws JsonProcessingException {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        Map<String, Set<String>> map = fieldErrors.stream().collect(Collectors.groupingBy(FieldError::getField,
                Collectors.mapping(FieldError::getDefaultMessage, Collectors.toSet())));

        String message = jacksonMapper.writeValueAsString(map);
        HttpError error = new HttpError(message, HttpStatus.BAD_REQUEST);
        error.setPath(req.getRequestURI());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler({SQLException.class, DataAccessException.class})
    public ResponseEntity<Object> handleQueryException(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
