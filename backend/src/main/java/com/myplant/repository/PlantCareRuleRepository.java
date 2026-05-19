package com.myplant.repository;

import com.myplant.entity.PlantCareRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.Optional;

/**
 * Plant Care Rule Repository
 * 
 * This repository provides database operations for PlantCareRule entities.
 * 
 * Custom methods:
 * - findByPlantName(plantName) - find care rules by plant type name
 */
@Repository
public interface PlantCareRuleRepository extends JpaRepository<PlantCareRule, Long> {
    /**
     * Find care rules by plant name (type)
     * 
     * @param plantName the name of the plant type (e.g., "Snake Plant")
     * @return Optional containing the care rule if found
     */
    Optional<PlantCareRule> findByPlantName(String plantName);
    Optional<PlantCareRule> findByPlantNameIgnoreCase(String plantName);

    /**
     * Check if a plant care rule exists
     * 
     * @param plantName the name of the plant type
     * @return true if exists, false otherwise
     */
    boolean existsByPlantName(String plantName);
}
