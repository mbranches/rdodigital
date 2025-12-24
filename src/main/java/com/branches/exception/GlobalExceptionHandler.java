package com.branches.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
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

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<DefaultErrorMessage> handleAuthenticationException() {
        DefaultErrorMessage errorMessage = new DefaultErrorMessage(HttpStatus.UNAUTHORIZED.value(), "Email ou senha inválidos");

        return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
    }
}
