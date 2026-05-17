package com.myplant.repository;

import com.myplant.entity.WateringHistory;
import com.myplant.entity.Plant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Watering History Repository
 * 
 * This repository provides database operations for WateringHistory entities.
 * 
 * Custom methods:
 * - findByPlant(plant) - get all watering records for a plant
 * - findByPlant_User_Id(userId) - get all watering records for a user's plants
 * - findByPlantOrderByWateredAtDesc(plant) - get watering history sorted by date
 */
@Repository
public interface WateringHistoryRepository extends JpaRepository<WateringHistory, Long> {
    /**
     * Find all watering records for a specific plant
     * 
     * @param plant the plant entity
     * @return list of watering history records
     */
    List<WateringHistory> findByPlant(Plant plant);

    /**
     * Find all watering records for all plants of a user
     * 
     * @param userId the user's ID
     * @return list of watering history records for user's plants
     */
    List<WateringHistory> findByPlant_User_Id(Long userId);

    /**
     * Find watering history for a plant sorted by date (newest first)
     * 
     * @param plant the plant entity
     * @return list sorted by watered date in descending order
     */
    List<WateringHistory> findByPlantOrderByWateredAtDesc(Plant plant);

    /**
     * Find recent watering records within a date range
     * 
     * @param userId the user's ID
     * @param startDate start of date range
     * @param endDate end of date range
     * @return list of watering records in the date range
     */
    @Query("SELECT wh FROM WateringHistory wh WHERE wh.plant.user.id = :userId " +
           "AND wh.wateredAt BETWEEN :startDate AND :endDate")
    List<WateringHistory> findRecentWateringsByDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
