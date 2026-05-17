package com.myplant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Registration DTO
 * 
 * This DTO is used when a user registers a new account.
 * It only accepts the fields needed for registration and
 * does NOT include sensitive fields like passwords in responses.
 * 
 * @see UserLoginDTO for login requests
 * @see UserResponseDTO for API responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String city;
    private String phoneNumber;
}
