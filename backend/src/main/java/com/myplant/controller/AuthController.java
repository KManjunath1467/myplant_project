package com.myplant.controller;

import com.myplant.dto.AuthResponseDTO;
import com.myplant.dto.UserLoginDTO;
import com.myplant.dto.UserRegisterDTO;
import com.myplant.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * 
 * Handles user registration and login
 * 
 * Endpoints:
 * - POST /api/auth/register - create new user account
 * - POST /api/auth/login - authenticate user
 */
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@CrossOrigin(origins = {
    "http://localhost:3000",
    "http://localhost:5173",
    "http://localhost:5174"
})
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user
     * 
     * Request body:
     * {
     *   "email": "user@example.com",
     *   "password": "securePassword123",
     *   "firstName": "John",
     *   "lastName": "Doe",
     *   "city": "New York",
     *   "phoneNumber": "+1234567890"
     * }
     * 
     * Response: JWT token + user info
     * 
     * @param registerDTO registration data
     * @return JWT token and user information
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody UserRegisterDTO registerDTO) {
        AuthResponseDTO response = authService.register(registerDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Login user
     * 
     * Request body:
     * {
     *   "email": "user@example.com",
     *   "password": "securePassword123"
     * }
     * 
     * Response: JWT token + user info
     * 
     * @param loginDTO email and password
     * @return JWT token and user information
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody UserLoginDTO loginDTO) {
        AuthResponseDTO response = authService.login(loginDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Health check endpoint (for monitoring)
     * 
     * @return ok message
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("MyPlant API is running!");
    }
}
