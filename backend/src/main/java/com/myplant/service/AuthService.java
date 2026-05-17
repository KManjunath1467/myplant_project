package com.myplant.service;

import com.myplant.dto.AuthResponseDTO;
import com.myplant.dto.UserLoginDTO;
import com.myplant.dto.UserRegisterDTO;
import com.myplant.dto.UserResponseDTO;
import com.myplant.entity.User;
import com.myplant.exception.*;
import com.myplant.repository.UserRepository;
import com.myplant.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authentication Service
 * 
 * Handles user registration and login logic.
 * Responsibilities:
 * 1. Validate registration data and create new users
 * 2. Authenticate users and generate JWT tokens
 * 3. Check email uniqueness
 * 4. Encrypt passwords using bcrypt
 */
@Service
@AllArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // bcrypt password encoder
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Register a new user
     * 
     * Process:
     * 1. Check if email already exists
     * 2. Create new user entity
     * 3. Encrypt password
     * 4. Save to database
     * 5. Generate JWT token
     * 6. Return auth response
     * 
     * @param registerDTO registration data from frontend
     * @return JWT token and user info
     * @throws EmailAlreadyExistsException if email is already registered
     */
    public AuthResponseDTO register(UserRegisterDTO registerDTO) {
        // Check if email already exists
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new EmailAlreadyExistsException(registerDTO.getEmail());
        }

        // Create new user
        User user = new User();
        user.setEmail(registerDTO.getEmail());
        user.setFirstName(registerDTO.getFirstName());
        user.setLastName(registerDTO.getLastName());
        user.setCity(registerDTO.getCity());
        user.setPhoneNumber(registerDTO.getPhoneNumber());
        
        // Encrypt password using bcrypt
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        
        // Save user to database
        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(savedUser);

        // Return auth response
        return new AuthResponseDTO(
                token,
                "Bearer",
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                "User registered successfully!"
        );
    }

    /**
     * Login user with email and password
     * 
     * Process:
     * 1. Find user by email
     * 2. Compare provided password with stored encrypted password
     * 3. Generate JWT token
     * 4. Return auth response
     * 
     * @param loginDTO email and password from frontend
     * @return JWT token and user info
     * @throws InvalidCredentialsException if email not found or password is wrong
     */
    public AuthResponseDTO login(UserLoginDTO loginDTO) {
        // Find user by email
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException());

        // Verify password (passwordEncoder.matches checks if provided password matches encrypted one)
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user);

        // Return auth response
        return new AuthResponseDTO(
                token,
                "Bearer",
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                "Login successful!"
        );
    }
}
