package com.myplant.controller;

import com.myplant.dto.UserResponseDTO;
import com.myplant.security.JwtTokenProvider;
import com.myplant.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller
 * 
 * Handles user profile operations
 * 
 * Endpoints:
 * - GET /api/users/{id} - get user profile
 * - PUT /api/users/{id}/preferences - update notification preferences
 * - PUT /api/users/{id}/city - update city
 */
@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Get current user's profile
     * 
     * Uses JWT token from Authorization header to identify user
     * 
     * @param authorization Bearer token from header
     * @return user profile information
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> getProfile(@RequestHeader("Authorization") String authorization) {
        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        
        UserResponseDTO profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * Get user by ID
     * 
     * @param id user ID
     * @return user profile
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.getUserProfile(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Update notification preferences
     * 
     * Request body:
     * {
     *   "emailNotifications": true,
     *   "whatsappNotifications": false
     * }
     * 
     * @param id user ID
     * @param preferences preference update
     * @return updated user profile
     */
    @PutMapping("/{id}/preferences")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> updatePreferences(
            @PathVariable Long id,
            @RequestBody UserPreferencesDTO preferences) {
        
        UserResponseDTO updated = userService.updateNotificationPreferences(
                id,
                preferences.getEmailNotifications(),
                preferences.getWhatsappNotifications()
        );
        
        return ResponseEntity.ok(updated);
    }

    /**
     * Update user's city (for weather integration)
     * 
     * Request body:
     * {
     *   "city": "London"
     * }
     * 
     * @param id user ID
     * @param cityUpdate city name
     * @return updated user profile
     */
    @PutMapping("/{id}/city")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> updateCity(
            @PathVariable Long id,
            @RequestBody UserCityDTO cityUpdate) {
        
        UserResponseDTO updated = userService.updateCity(id, cityUpdate.getCity());
        return ResponseEntity.ok(updated);
    }

    // Inner classes for request bodies
    public static class UserPreferencesDTO {
        private Boolean emailNotifications;
        private Boolean whatsappNotifications;

        public Boolean getEmailNotifications() { return emailNotifications; }
        public void setEmailNotifications(Boolean emailNotifications) { 
            this.emailNotifications = emailNotifications; 
        }

        public Boolean getWhatsappNotifications() { return whatsappNotifications; }
        public void setWhatsappNotifications(Boolean whatsappNotifications) { 
            this.whatsappNotifications = whatsappNotifications; 
        }
    }

    public static class UserCityDTO {
        private String city;

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
    }
}
