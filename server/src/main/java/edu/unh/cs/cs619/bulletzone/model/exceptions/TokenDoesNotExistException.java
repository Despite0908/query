package edu.unh.cs.cs619.bulletzone.model.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public final class TokenDoesNotExistException extends Exception {
    public TokenDoesNotExistException(Long tankId) {
        super(String.format("Token '%d' does not exist", tankId));
    }
}
