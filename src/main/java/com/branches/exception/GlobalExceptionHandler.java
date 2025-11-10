package com.branches.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<DefaultErrorMessage> handleResponseStatusException(ResponseStatusException ex) {
        DefaultErrorMessage errorMessage = new DefaultErrorMessage(ex.getStatusCode().value(), ex.getReason());

        return new ResponseEntity<>(errorMessage, ex.getStatusCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DefaultErrorMessage> handlerArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errorsList = e.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();

        String errors = String.join("; ", errorsList);

        DefaultErrorMessage errorResponse = new DefaultErrorMessage(HttpStatus.BAD_REQUEST.value(), errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
