package com.myplant.config;

import com.myplant.entity.User;
import com.myplant.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom UserDetailsService
 * 
 * Spring Security uses this service to load user information for authentication.
 * 
 * When Spring Security needs to authenticate a user, it:
 * 1. Calls loadUserByUsername()
 * 2. Receives UserDetails object
 * 3. Compares password hashes
 * 4. Sets up Security Context if password matches
 */
@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user by email address (we use email instead of username)
     * 
     * @param email the user's email
     * @return UserDetails object containing user information
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // For now, all users have ROLE_USER
        // In future, could have ROLE_ADMIN, ROLE_PREMIUM, etc.
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(!user.getActive()) // Lock if marked inactive
                .credentialsExpired(false)
                .disabled(!user.getActive())
                .build();
    }

    /**
     * Alternative: Load user by ID (more efficient for JWT auth)
     * 
     * @param userId the user's ID
     * @return UserDetails object
     * @throws UsernameNotFoundException if user not found
     */
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(!user.getActive())
                .credentialsExpired(false)
                .disabled(!user.getActive())
                .build();
    }
}
