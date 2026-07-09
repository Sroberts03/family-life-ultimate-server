package com.app;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponseDto> handleUnexpected(BaseException e) {
        BaseResponseDto response = new BaseResponseDto();
        response.getBody().put("message", e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponseDto> handleUnexpected(Exception e) {
        BaseResponseDto response = new BaseResponseDto();
        response.getBody().put("message", "An Unknown Error Occured");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}