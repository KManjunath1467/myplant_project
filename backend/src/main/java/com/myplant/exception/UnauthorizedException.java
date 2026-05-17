package com.myplant.exception;

/**
 * Exception thrown when user is not authorized to access a resource
 * 
 * Usage: When a user tries to access or modify another user's plants
 */
public class UnauthorizedException extends ApplicationException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException() {
        super("You are not authorized to access this resource.");
    }
}
