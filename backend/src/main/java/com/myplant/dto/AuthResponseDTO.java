package com.myplant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Authentication Response DTO
 * 
 * This DTO is returned after successful login or registration.
 * It contains:
 * 1. JWT token - used for authenticating future requests
 * 2. User information - basic user details (without password)
 * 3. Message - success/error message
 * 
 * Frontend stores the JWT token and includes it in the Authorization header
 * for all subsequent API requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String message;
}
