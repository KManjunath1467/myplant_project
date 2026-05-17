package com.myplant.service;

import com.myplant.dto.UserResponseDTO;
import com.myplant.entity.User;
import com.myplant.exception.ResourceNotFoundException;
import com.myplant.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User Service
 * 
 * Handles user profile operations.
 * Responsibilities:
 * 1. Fetch user information
 * 2. Update user profile
 * 3. Update notification preferences
 * 4. Convert entities to DTOs
 */
@Service
@AllArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    /**
     * Get user by ID
     * 
     * @param userId the user's ID
     * @return User entity
     * @throws ResourceNotFoundException if user not found
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    /**
     * Get user profile information as DTO (without sensitive data)
     * 
     * @param userId the user's ID
     * @return UserResponseDTO containing safe profile info
     */
    public UserResponseDTO getUserProfile(Long userId) {
        User user = getUserById(userId);
        return convertToResponseDTO(user);
    }

    /**
     * Update user notification preferences
     * 
     * @param userId the user's ID
     * @param emailNotifications whether to receive email notifications
     * @param whatsappNotifications whether to receive WhatsApp notifications
     * @return updated user profile
     */
    public UserResponseDTO updateNotificationPreferences(
            Long userId,
            Boolean emailNotifications,
            Boolean whatsappNotifications) {
        
        User user = getUserById(userId);
        
        if (emailNotifications != null) {
            user.setEmailNotifications(emailNotifications);
        }
        if (whatsappNotifications != null) {
            user.setWhatsappNotifications(whatsappNotifications);
        }
        
        User updatedUser = userRepository.save(user);
        return convertToResponseDTO(updatedUser);
    }

    /**
     * Update user city (for weather integration)
     * 
     * @param userId the user's ID
     * @param city the new city
     * @return updated user profile
     */
    public UserResponseDTO updateCity(Long userId, String city) {
        User user = getUserById(userId);
        user.setCity(city);
        User updatedUser = userRepository.save(user);
        return convertToResponseDTO(updatedUser);
    }

    /**
     * Convert User entity to UserResponseDTO
     * This ensures we don't expose sensitive data like password
     * 
     * @param user the user entity
     * @return DTO without sensitive fields
     */
    private UserResponseDTO convertToResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getCity(),
                user.getPhoneNumber(),
                user.getEmailNotifications(),
                user.getWhatsappNotifications(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
