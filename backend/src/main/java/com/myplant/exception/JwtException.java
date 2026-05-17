package com.myplant.exception;

/**
 * Exception thrown when JWT token is invalid or expired
 * 
 * Usage: When a user's JWT token is invalid, expired, or tampered with
 */
public class JwtException extends ApplicationException {
    public JwtException(String message) {
        super(message);
    }

    public JwtException() {
        super("Invalid or expired JWT token. Please login again.");
    }
}
