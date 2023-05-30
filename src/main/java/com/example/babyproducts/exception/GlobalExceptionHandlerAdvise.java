package com.example.babyproducts.exception;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandlerAdvise {

    @ExceptionHandler(value = {ResponseStatusException.class})
    public ResponseEntity<Object> handleException(ResponseStatusException ex) {


        return new ResponseEntity<>(ex.getMessage(), ex.getStatusCode());

    }
}
