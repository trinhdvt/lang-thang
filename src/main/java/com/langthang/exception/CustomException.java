package com.langthang.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomException {
    private String message;
    private int status;
    private Date timestamp;
}
