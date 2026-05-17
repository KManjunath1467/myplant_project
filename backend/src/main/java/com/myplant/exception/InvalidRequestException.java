package com.myplant.exception;

/**
 * Exception for invalid request data
 * 
 * Usage: When required fields are missing or invalid in API requests
 */
public class InvalidRequestException extends ApplicationException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
