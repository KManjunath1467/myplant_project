package com.myplant.exception;

/**
 * Base application exception
 * 
 * This is the parent class for all custom exceptions in the application.
 * It extends RuntimeException to work with Spring's exception handling.
 */
public class ApplicationException extends RuntimeException {
    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
