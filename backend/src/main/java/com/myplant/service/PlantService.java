package com.myplant.service;

import com.myplant.dto.PlantDTO;
import com.myplant.entity.Plant;
import com.myplant.entity.PlantCareRule;
import com.myplant.entity.User;
import com.myplant.exception.ResourceNotFoundException;
import com.myplant.exception.UnauthorizedException;
import com.myplant.repository.PlantCareRuleRepository;
import com.myplant.repository.PlantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Plant Service
 * 
 * Handles plant management operations.
 * Responsibilities:
 * 1. Create, read, update, delete plants
 * 2. Link plants to plant care rules
 * 3. Security: ensure users can only manage their own plants
 * 4. Convert entities to DTOs
 */
@Service
@AllArgsConstructor
@Transactional
public class PlantService {

    private final PlantRepository plantRepository;
    private final PlantCareRuleRepository plantCareRuleRepository;
    private final UserService userService;

    /**
     * Create a new plant for a user
     * 
     * @param userId the user's ID
     * @param plantDTO plant information from frontend
     * @return created plant as DTO
     * @throws ResourceNotFoundException if plant care rule not found
     */
    public PlantDTO createPlant(Long userId, PlantDTO plantDTO) {
        User user = userService.getUserById(userId);

        Plant plant = new Plant();
        plant.setUser(user);
        plant.setName(plantDTO.getName());
        plant.setPlantType(plantDTO.getPlantType());
        plant.setHealth(plantDTO.getHealth() != null ? plantDTO.getHealth() : "Healthy");
        plant.setPotSize(plantDTO.getPotSize());
        plant.setIsIndoor(plantDTO.getIsIndoor());
        plant.setLocation(plantDTO.getLocation());
        plant.setNotes(plantDTO.getNotes());
        plant.setCustomWateringInterval(plantDTO.getCustomWateringInterval());
        plant.setLastWateredDate(plantDTO.getLastWateredDate());
        plant.setCreatedAt(LocalDateTime.now());
        plant.setUpdatedAt(LocalDateTime.now());

        // Link to plant care rule if plant type is provided
        if (plantDTO.getPlantType() != null) {
            PlantCareRule careRule = plantCareRuleRepository
                    .findByPlantName(plantDTO.getPlantType())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Plant care rule", "plantName", plantDTO.getPlantType()));
            plant.setPlantCareRule(careRule);
            if (plant.getPotSize() == null) {
                plant.setPotSize(careRule.getRecommendedPotSize());
            }
            if (plant.getIsIndoor() == null) {
                plant.setIsIndoor(true);
            }
            if (plant.getLocation() == null) {
                plant.setLocation("Indoor shelf");
            }
        }

        if (plant.getPotSize() == null) {
            plant.setPotSize("Medium (6 inches)");
        }
        if (plant.getIsIndoor() == null) {
            plant.setIsIndoor(true);
        }
        if (plant.getLocation() == null) {
            plant.setLocation("Indoor garden");
        }

        Plant savedPlant = plantRepository.save(plant);
        return convertToDTO(savedPlant);
    }

    /**
     * Get all plants for a user as entities
     * Used for scheduler calculations
     * 
     * @param userId the user's ID
     * @return list of user's plant entities
     */
    public List<Plant> getUserPlantEntities(Long userId) {
        return plantRepository.findByUser_Id(userId);
    }

    /**
     * Get a specific plant (with authorization check)
     * 
     * @param userId the user's ID
     * @param plantId the plant's ID
     * @return plant as DTO
     * @throws UnauthorizedException if plant doesn't belong to user
     */
    public PlantDTO getPlant(Long userId, Long plantId) {
        Plant plant = plantRepository.findByUserAndId(userService.getUserById(userId), plantId)
                .orElseThrow(() -> new UnauthorizedException(
                        "This plant does not belong to you or does not exist."));
        return convertToDTO(plant);
    }

    /**
     * Update a plant
     * 
     * @param userId the user's ID
     * @param plantId the plant's ID
     * @param plantDTO updated plant data
     * @return updated plant as DTO
     */
    public PlantDTO updatePlant(Long userId, Long plantId, PlantDTO plantDTO) {
        User user = userService.getUserById(userId);
        Plant plant = plantRepository.findByUserAndId(user, plantId)
                .orElseThrow(() -> new UnauthorizedException(
                        "This plant does not belong to you or does not exist."));

        plant.setName(plantDTO.getName());
        plant.setPlantType(plantDTO.getPlantType());
        plant.setPotSize(plantDTO.getPotSize());
        plant.setIsIndoor(plantDTO.getIsIndoor());
        plant.setLocation(plantDTO.getLocation());
        plant.setNotes(plantDTO.getNotes());
        plant.setCustomWateringInterval(plantDTO.getCustomWateringInterval());
        plant.setUpdatedAt(LocalDateTime.now());

        // Update plant care rule if plant type changed
        if (plantDTO.getPlantType() != null) {
            PlantCareRule careRule = plantCareRuleRepository
                    .findByPlantName(plantDTO.getPlantType())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Plant care rule", "plantName", plantDTO.getPlantType()));
            plant.setPlantCareRule(careRule);
        }

        Plant updatedPlant = plantRepository.save(plant);
        return convertToDTO(updatedPlant);
    }

    /**
     * Delete a plant
     * 
     * @param userId the user's ID
     * @param plantId the plant's ID
     * @throws UnauthorizedException if plant doesn't belong to user
     */
    public void deletePlant(Long userId, Long plantId) {
        User user = userService.getUserById(userId);
        Plant plant = plantRepository.findByUserAndId(user, plantId)
                .orElseThrow(() -> new UnauthorizedException(
                        "This plant does not belong to you or does not exist."));
        
        plantRepository.delete(plant);
    }

    /**
     * Mark plant as watered
     * Updates the last watered date
     * 
     * @param userId the user's ID
     * @param plantId the plant's ID
     * @return updated plant as DTO
     */
    public PlantDTO markAsWatered(Long userId, Long plantId) {
        User user = userService.getUserById(userId);
        Plant plant = plantRepository.findByUserAndId(user, plantId)
                .orElseThrow(() -> new UnauthorizedException(
                        "This plant does not belong to you or does not exist."));
        
        plant.setLastWateredDate(LocalDate.now());
        plant.setUpdatedAt(LocalDateTime.now());
        
        Plant updatedPlant = plantRepository.save(plant);
        return convertToDTO(updatedPlant);
    }

    /**
     * Update plant health status
     * 
     * @param userId the user's ID
     * @param plantId the plant's ID
     * @param health new health status (Healthy, Fair, Poor)
     * @return updated plant as DTO
     */
    public PlantDTO updatePlantHealth(Long userId, Long plantId, String health) {
        User user = userService.getUserById(userId);
        Plant plant = plantRepository.findByUserAndId(user, plantId)
                .orElseThrow(() -> new UnauthorizedException(
                        "This plant does not belong to you or does not exist."));
        
        plant.setHealth(health);
        plant.setUpdatedAt(LocalDateTime.now());
        
        Plant updatedPlant = plantRepository.save(plant);
        return convertToDTO(updatedPlant);
    }

    /**
     * Convert Plant entity to PlantDTO
     * 
     * @param plant the plant entity
     * @return DTO with plant info
     */
    private PlantDTO convertToDTO(Plant plant) {
        PlantDTO dto = new PlantDTO();
        dto.setId(plant.getId());
        dto.setName(plant.getName());
        dto.setPlantType(plant.getPlantType());
        dto.setPotSize(plant.getPotSize());
        dto.setIsIndoor(plant.getIsIndoor());
        dto.setLocation(plant.getLocation());
        dto.setLastWateredDate(plant.getLastWateredDate());
        dto.setWateringStreak(plant.getWateringStreak());
        dto.setHealth(plant.getHealth());
        dto.setNotes(plant.getNotes());
        dto.setCustomWateringInterval(plant.getCustomWateringInterval());
        dto.setCreatedAt(plant.getCreatedAt());
        dto.setUpdatedAt(plant.getUpdatedAt());

        if (plant.getPlantCareRule() != null) {
            dto.setPlantCareRuleId(plant.getPlantCareRule().getId());
            dto.setCareRuleDescription(plant.getPlantCareRule().getDescription());
            dto.setBaseWateringDays(plant.getPlantCareRule().getBaseWateringDays());
            dto.setWateringFrequency(plant.getPlantCareRule().getWateringFrequency());
            dto.setSunlightNeeds(plant.getPlantCareRule().getSunlightNeeds());
            dto.setHumidityPreference(plant.getPlantCareRule().getHumidityPreference());
            dto.setTemperatureRange(plant.getPlantCareRule().getTemperatureRange());
            dto.setDifficultyLevel(plant.getPlantCareRule().getDifficultyLevel());
            dto.setRecommendedPotSize(plant.getPlantCareRule().getRecommendedPotSize());
            dto.setGrowthRate(plant.getPlantCareRule().getGrowthRate());
            dto.setMaxSize(plant.getPlantCareRule().getMaxSize());
            dto.setCareTips(plant.getPlantCareRule().getCareTips());
        }

        return dto;
    }

    /**
     * Convert PlantDTO to Plant entity
     * Used for scheduler calculations
     * 
     * @param plantDTO the plant DTO
     * @return Plant entity
     */
    public Plant convertToEntity(PlantDTO plantDTO) {
        Plant plant = new Plant();
        plant.setId(plantDTO.getId());
        plant.setName(plantDTO.getName());
        plant.setPlantType(plantDTO.getPlantType());
        plant.setPotSize(plantDTO.getPotSize());
        plant.setIsIndoor(plantDTO.getIsIndoor());
        plant.setLocation(plantDTO.getLocation());
        plant.setLastWateredDate(plantDTO.getLastWateredDate());
        plant.setWateringStreak(plantDTO.getWateringStreak());
        plant.setHealth(plantDTO.getHealth());
        plant.setNotes(plantDTO.getNotes());
        plant.setCustomWateringInterval(plantDTO.getCustomWateringInterval());
        plant.setCreatedAt(plantDTO.getCreatedAt());
        plant.setUpdatedAt(plantDTO.getUpdatedAt());

        // Set plant care rule from partial DTO data if available
        if (plantDTO.getPlantCareRuleId() != null || plantDTO.getBaseWateringDays() != null) {
            PlantCareRule careRule = new PlantCareRule();
            careRule.setId(plantDTO.getPlantCareRuleId());
            careRule.setPlantName(plantDTO.getPlantType());
            careRule.setBaseWateringDays(plantDTO.getBaseWateringDays());
            careRule.setWateringFrequency(plantDTO.getWateringFrequency());
            careRule.setSunlightNeeds(plantDTO.getSunlightNeeds());
            careRule.setHumidityPreference(plantDTO.getHumidityPreference());
            careRule.setTemperatureRange(plantDTO.getTemperatureRange());
            careRule.setDifficultyLevel(plantDTO.getDifficultyLevel());
            careRule.setRecommendedPotSize(plantDTO.getRecommendedPotSize());
            careRule.setGrowthRate(plantDTO.getGrowthRate());
            careRule.setMaxSize(plantDTO.getMaxSize());
            careRule.setCareTips(plantDTO.getCareTips());
            plant.setPlantCareRule(careRule);
        }

        return plant;
    }
}
