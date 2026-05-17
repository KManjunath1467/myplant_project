package com.myplant.exception;

/**
 * Exception thrown when invalid credentials are provided
 * 
 * Usage: When a user provides wrong email or password during login
 */
public class InvalidCredentialsException extends ApplicationException {
    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException() {
        super("Invalid email or password. Please check your credentials.");
    }
}
