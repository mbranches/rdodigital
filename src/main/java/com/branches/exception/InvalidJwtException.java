package com.branches.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidJwtException extends ResponseStatusException {
    public InvalidJwtException(String reason) {
        super(HttpStatus.UNAUTHORIZED, reason);
    }
}
