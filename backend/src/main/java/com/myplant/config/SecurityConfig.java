package com.myplant.config;

import com.myplant.security.JwtAuthenticationFilter;
import com.myplant.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security Configuration
 * 
 * This configuration sets up Spring Security with JWT authentication.
 * 
 * Key features:
 * 1. Password encoding using BCrypt (industry standard)
 * 2. JWT token validation for every request
 * 3. Stateless sessions (no server-side sessions)
 * 4. CORS support for frontend-backend communication
 * 5. Public/protected endpoint configuration
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * BCrypt Password Encoder
     * 
     * Why BCrypt?
     * - Automatically adds salt to passwords
     * - Slows down password hashing (prevents brute force attacks)
     * - Industry standard recommended by Spring Security
     * 
     * Example:
     * - Plain password: "mypassword123"
     * - Encoded: "$2a$10$xK1BjK9f...."
     * - Each encoding is different (due to salt)
     * 
     * @return BCryptPasswordEncoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // 12 = strength level
    }

    /**
     * Authentication Manager
     * 
     * Used by AuthService to authenticate user credentials
     * 
     * @param config authentication configuration
     * @return AuthenticationManager bean
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * DAO Authentication Provider
     * 
     * Configures how Spring Security authenticates users
     * 
     * @param userDetailsService service to load user details
     * @param passwordEncoder password encoder
     * @return DaoAuthenticationProvider bean
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * Security Filter Chain
     * 
     * Defines which endpoints are public vs protected
     * Configures JWT authentication
     * Sets up CORS and other security settings
     * 
     * Public endpoints (no authentication needed):
     * - POST /api/auth/register - user registration
     * - POST /api/auth/login - user login
     * - GET /api/plant-care-rules - view plant types
     * 
     * Protected endpoints (require JWT token):
     * - All other endpoints
     * 
     * @param http HttpSecurity to configure
     * @return configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (not needed for stateless JWT auth)
                .csrf(csrf -> csrf.disable())
                
                // Set session creation policy to STATELESS
                // This means server doesn't store user sessions
                // Each request is authenticated via JWT token
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // Configure which endpoints are public vs protected
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints (anyone can access)
                        .requestMatchers(
                          "/",
                          "/error",
                         "/api/test",
                         "/api/auth/register",
                         "/api/auth/login",
                         "/api/auth/**"
                        ).permitAll()
                        .requestMatchers("/api/plant-care-rules/**").permitAll()
                        .requestMatchers("/api/health").permitAll()
                        .requestMatchers("/api/ai/**").permitAll()
                        
                        // Health check endpoint (for monitoring)
                        .requestMatchers("/actuator/**").permitAll()
                        
                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                
                // Enable CORS (allows frontend to call backend)
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                   corsConfig.setAllowedOrigins(java.util.List.of(
                                     "http://localhost:3000",
                                     "http://localhost:5173",
                                     "http://localhost:5174",
                                     "https://yourfrontend.com"
                                 ));
                    corsConfig.setAllowedMethods(java.util.List.of("*"));
                    corsConfig.setAllowedHeaders(java.util.List.of("*"));
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))
                
                // Add JWT filter
                // This filter runs before UsernamePasswordAuthenticationFilter
                // It extracts and validates JWT tokens from requests
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
