package com.app;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.app.family.exceptions.AlreadyMemberException;
import com.app.family.exceptions.FamilyNotFoundException;
import com.app.family.exceptions.OwnerMustBeAdultException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FamilyNotFoundException.class)
    public ResponseEntity<BaseResponseDto> handleNotFound(FamilyNotFoundException e) {
        BaseResponseDto response = new BaseResponseDto();
        response.getBody().put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(AlreadyMemberException.class)
    public ResponseEntity<BaseResponseDto> handleAlreadyMember(AlreadyMemberException e) {
        BaseResponseDto response = new BaseResponseDto();
        response.getBody().put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    @ExceptionHandler(OwnerMustBeAdultException.class)
    public ResponseEntity<BaseResponseDto> handleOwnerMustBeAdult(OwnerMustBeAdultException e) {
        BaseResponseDto response = new BaseResponseDto();
        response.getBody().put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponseDto> handleUnexpected(Exception e) {
        BaseResponseDto response = new BaseResponseDto();
        response.getBody().put("message", "An unknown error occured please try again.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}