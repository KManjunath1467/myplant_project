package com.myplant.service;

import com.myplant.dto.PlantDTO;
import com.myplant.dto.WateringHistoryDTO;
import com.myplant.entity.Plant;
import com.myplant.entity.WateringHistory;
import com.myplant.repository.WateringHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Watering History Service
 *
 * Handles tracking of watering events.
 * Responsibilities:
 * 1. Record when plants were watered
 * 2. Track watering streaks
 * 3. Provide analytics on watering patterns
 * 4. Help identify neglected plants
 */
@Service
@AllArgsConstructor
@Transactional
public class WateringHistoryService {

    private final WateringHistoryRepository wateringHistoryRepository;
    private final PlantService plantService;
    private final UserService userService;

    /**
     * Record a watering event for a plant
     *
     * @param userId the user's ID
     * @param plantId the plant's ID
     * @param notes optional notes about the watering
     * @return watering history record as DTO
     */
    public WateringHistoryDTO recordWatering(Long userId, Long plantId, String notes) {

        // Validate plant ownership
        PlantDTO plantDTO = plantService.getPlant(userId, plantId);

        // Update plant's last watered date
        plantService.markAsWatered(userId, plantId);

        // Create Plant reference
        Plant plant = new Plant();
        plant.setId(plantId);

        // Create watering history record
        WateringHistory history = new WateringHistory();
        history.setPlant(plant);
        history.setWateredAt(LocalDateTime.now());
        history.setNotes(notes);
        history.setOnTime(true);

        WateringHistory saved = wateringHistoryRepository.save(history);

        return convertToDTO(saved);
    }

    /**
     * Get watering history for a specific plant
     *
     * @param userId the user's ID
     * @param plantId the plant's ID
     * @return list of watering history records
     */
    public List<WateringHistoryDTO> getPlantWateringHistory(Long userId, Long plantId) {

        // Validate plant ownership
        plantService.getPlant(userId, plantId);

        Plant plant = new Plant();
        plant.setId(plantId);

        return wateringHistoryRepository
                .findByPlantOrderByWateredAtDesc(plant)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all watering history for user's plants
     *
     * @param userId the user's ID
     * @return list of all watering history
     */
    public List<WateringHistoryDTO> getUserWateringHistory(Long userId) {

        userService.getUserById(userId);

        return wateringHistoryRepository
                .findByPlant_User_Id(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get recent watering activity (last 7 days)
     * Used for dashboard display
     *
     * @param userId the user's ID
     * @return list of recent watering records
     */
    public List<WateringHistoryDTO> getRecentWatering(Long userId) {

        userService.getUserById(userId);

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime now = LocalDateTime.now();

        return wateringHistoryRepository
                .findRecentWateringsByDateRange(userId, sevenDaysAgo, now)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert WateringHistory entity to DTO
     *
     * @param history the entity
     * @return DTO
     */
    private WateringHistoryDTO convertToDTO(WateringHistory history) {

        return new WateringHistoryDTO(
                history.getId(),
                history.getPlant() != null ? history.getPlant().getId() : null,
                history.getPlant() != null ? history.getPlant().getName() : null,
                history.getWateredAt().toLocalDate(),
                history.getNotes(),
                history.getOnTime(),
                history.getWaterAmount(),
                history.getPlantHealthAtWatering(),
                history.getWeatherCondition()
        );
    }
}