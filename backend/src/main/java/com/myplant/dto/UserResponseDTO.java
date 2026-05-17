package com.myplant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * User Response DTO
 * 
 * This DTO is returned by the API when responding with user information.
 * It does NOT include sensitive fields like passwords.
 * It's used for GET requests to fetch user profile information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String city;
    private String phoneNumber;
    private Boolean emailNotifications;
    private Boolean whatsappNotifications;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
