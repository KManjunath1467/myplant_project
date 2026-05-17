package com.myplant.repository;

import com.myplant.entity.Plant;
import com.myplant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Plant Repository
 * 
 * This repository provides database operations for Plant entities.
 * 
 * Custom methods:
 * - findByUser(user) - get all plants belonging to a user
 * - findByUserAndId(user, id) - get a specific plant of a user
 * - findByUser_Id(userId) - get all plants for a user ID
 */
@Repository
public interface PlantRepository extends JpaRepository<Plant, Long> {
    /**
     * Find all plants belonging to a user
     * 
     * @param user the user entity
     * @return list of plants owned by the user
     */
    List<Plant> findByUser(User user);

    /**
     * Find a specific plant by ID and user (security check)
     * Ensures a user can only access their own plants
     * 
     * @param user the user entity
     * @param id the plant ID
     * @return Optional containing the plant if found and belongs to the user
     */
    Optional<Plant> findByUserAndId(User user, Long id);

    /**
     * Find all plants for a user by user ID
     * 
     * @param userId the user's ID
     * @return list of plants owned by the user
     */
    List<Plant> findByUser_Id(Long userId);
}
