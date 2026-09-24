package com.example.zukbambe.global.error.exception;

import org.springframework.http.HttpStatus;

public interface ErrorProperty {
    String getMessage();
    HttpStatus getStatus();
    String getCode();
}
