package com.myplant.exception;

/**
 * Exception thrown when email already exists during registration
 * 
 * Usage: When a user tries to register with an email that's already in the system
 */
public class EmailAlreadyExistsException extends ApplicationException {
    public EmailAlreadyExistsException(String email) {
        super(String.format("Email '%s' is already registered. Please use a different email.", email));
    }
}
