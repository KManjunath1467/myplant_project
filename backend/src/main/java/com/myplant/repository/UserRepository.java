package com.myplant.repository;

import com.myplant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * User Repository
 * 
 * This repository provides database operations for User entities.
 * JpaRepository automatically provides:
 * - save(user) - save or update a user
 * - findById(id) - find user by ID
 * - findAll() - get all users
 * - delete(user) - delete a user
 * 
 * We add custom methods:
 * - findByEmail(email) - find user by email (for login)
 * - existsByEmail(email) - check if email already exists (for registration validation)
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find a user by email address
     * Used during login to authenticate the user
     * 
     * @param email the user's email
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a user with given email exists
     * Used during registration to validate email uniqueness
     * 
     * @param email the email to check
     * @return true if email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
