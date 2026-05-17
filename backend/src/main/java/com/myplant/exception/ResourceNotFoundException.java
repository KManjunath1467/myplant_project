package com.myplant.exception;

/**
 * Exception thrown when a requested resource is not found
 * 
 * Usage: When a user tries to access a plant or plant care rule that doesn't exist
 */
public class ResourceNotFoundException extends ApplicationException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));
    }
}
