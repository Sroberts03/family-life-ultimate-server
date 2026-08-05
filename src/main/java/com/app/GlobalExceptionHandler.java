package com.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.app.errors.ErrorDao;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ErrorDao errordao;

    @Autowired
    public GlobalExceptionHandler(ErrorDao errordao) {
        this.errordao = errordao;
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponseDto> handleUnexpected(BaseException e) {
        BaseResponseDto response = new BaseResponseDto();
        response.getBody().put("message", e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponseDto> handleUnexpected(Exception e) {
        String userId = null;
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            userId = jwt.getSubject();
        }

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        errordao.logError(e.getClass().getName(), e.getMessage(), sw.toString(), userId);

        BaseResponseDto response = new BaseResponseDto();
        response.getBody().put("message", "An Unknown Error Occured");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}