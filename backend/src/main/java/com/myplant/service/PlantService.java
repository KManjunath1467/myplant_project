package com.myplant.service;

import com.myplant.dto.PlantDTO;
import com.myplant.entity.Plant;
import com.myplant.entity.PlantCareRule;
import com.myplant.entity.User;
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
 * Handles:
 * - Create Plant
 * - Read Plants
 * - Update Plants
 * - Delete Plants
 * - Watering Updates
 */

@Service
@AllArgsConstructor
@Transactional
public class PlantService {

    private final PlantRepository plantRepository;
    private final PlantCareRuleRepository plantCareRuleRepository;
    private final UserService userService;

    /**
     * CREATE PLANT
     */
    public PlantDTO createPlant(PlantDTO dto, User user) {

    Plant plant = new Plant();

    plant.setName(dto.getName());

    plant.setPlantType(dto.getPlantType());

    plant.setPotSize(dto.getPotSize());

    plant.setIsIndoor(dto.getIsIndoor());

    plant.setLocation(dto.getLocation());

    plant.setNotes(dto.getNotes());

    plant.setCustomWateringInterval(
            dto.getCustomWateringInterval()
    );

    plant.setLastWateredDate(LocalDate.now());

    plant.setCreatedAt(LocalDateTime.now());

    plant.setUpdatedAt(LocalDateTime.now());

    plant.setUser(user);

    /*
     * FIND CARE RULE USING PLANT TYPE
     */
    PlantCareRule rule =
            plantCareRuleRepository
                    .findByPlantNameIgnoreCase(
                            dto.getPlantType()
                    )
                    .orElse(null);

    /*
     * ATTACH RULE TO PLANT
     */
    plant.setPlantCareRule(rule);

    Plant savedPlant =
            plantRepository.save(plant);

    return convertToDTO(savedPlant);
}

    /**
     * GET ALL USER PLANTS (ENTITY)
     */
    public List<Plant> getUserPlantEntities(Long userId) {

        return plantRepository.findByUser_Id(userId);
    }

    /**
     * GET ALL USER PLANTS (DTO)
     */
    public List<PlantDTO> getUserPlants(Long userId) {

        List<Plant> plants = plantRepository.findByUser_Id(userId);

        return plants.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * GET SINGLE PLANT
     */
    public PlantDTO getPlant(Long userId, Long plantId) {

        Plant plant = plantRepository
                .findByUserAndId(
                        userService.getUserById(userId),
                        plantId
                )
                .orElseThrow(() -> new UnauthorizedException(
                        "This plant does not belong to you or does not exist."
                ));

        return convertToDTO(plant);
    }

    /**
     * UPDATE PLANT
     */
    public PlantDTO updatePlant(
            Long userId,
            Long plantId,
            PlantDTO plantDTO
    ) {

        User user = userService.getUserById(userId);

        Plant plant = plantRepository
                .findByUserAndId(user, plantId)
                .orElseThrow(() -> new UnauthorizedException(
                        "This plant does not belong to you or does not exist."
                ));

        plant.setName(plantDTO.getName());
        plant.setPlantType(plantDTO.getPlantType());

        plant.setPotSize(plantDTO.getPotSize());
        plant.setIsIndoor(plantDTO.getIsIndoor());

        plant.setLocation(plantDTO.getLocation());

        plant.setNotes(plantDTO.getNotes());

        plant.setCustomWateringInterval(
                plantDTO.getCustomWateringInterval()
        );

        plant.setUpdatedAt(LocalDateTime.now());

        Plant updatedPlant = plantRepository.save(plant);

        return convertToDTO(updatedPlant);
    }

    /**
     * DELETE PLANT
     */
    public void deletePlant(Long userId, Long plantId) {

        User user = userService.getUserById(userId);

        Plant plant = plantRepository
                .findByUserAndId(user, plantId)
                .orElseThrow(() -> new UnauthorizedException(
                        "This plant does not belong to you or does not exist."
                ));

        plantRepository.delete(plant);
    }

    /**
     * MARK AS WATERED
     */
    public PlantDTO markAsWatered(Long userId, Long plantId) {

        User user = userService.getUserById(userId);

        Plant plant = plantRepository
                .findByUserAndId(user, plantId)
                .orElseThrow(() -> new UnauthorizedException(
                        "This plant does not belong to you or does not exist."
                ));

        plant.setLastWateredDate(LocalDate.now());

        plant.setUpdatedAt(LocalDateTime.now());

        Plant updatedPlant = plantRepository.save(plant);

        return convertToDTO(updatedPlant);
    }

    /**
     * UPDATE HEALTH
     */
    public PlantDTO updatePlantHealth(
            Long userId,
            Long plantId,
            String health
    ) {

        User user = userService.getUserById(userId);

        Plant plant = plantRepository
                .findByUserAndId(user, plantId)
                .orElseThrow(() -> new UnauthorizedException(
                        "This plant does not belong to you or does not exist."
                ));

        plant.setHealth(health);

        plant.setUpdatedAt(LocalDateTime.now());

        Plant updatedPlant = plantRepository.save(plant);

        return convertToDTO(updatedPlant);
    }

    /**
     * ENTITY -> DTO
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

        dto.setCustomWateringInterval(
                plant.getCustomWateringInterval()
        );

        dto.setCreatedAt(plant.getCreatedAt());

        dto.setUpdatedAt(plant.getUpdatedAt());

        /*
         * OPTIONAL RULE DATA
         */
        if (plant.getPlantCareRule() != null) {

            PlantCareRule rule = plant.getPlantCareRule();

            dto.setPlantCareRuleId(rule.getId());

            dto.setCareRuleDescription(rule.getDescription());

            dto.setBaseWateringDays(rule.getBaseWateringDays());

            dto.setWateringFrequency(rule.getWateringFrequency());

            dto.setSunlightNeeds(rule.getSunlightNeeds());

            dto.setHumidityPreference(rule.getHumidityPreference());

            dto.setTemperatureRange(rule.getTemperatureRange());

            dto.setDifficultyLevel(rule.getDifficultyLevel());

            dto.setRecommendedPotSize(
                    rule.getRecommendedPotSize()
            );

            dto.setGrowthRate(rule.getGrowthRate());

            dto.setMaxSize(rule.getMaxSize());

            dto.setCareTips(rule.getCareTips());
        }

        return dto;
    }

    /**
     * DTO -> ENTITY
     */
    public Plant convertToEntity(PlantDTO plantDTO) {

        Plant plant = new Plant();

        plant.setId(plantDTO.getId());

        plant.setName(plantDTO.getName());

        plant.setPlantType(plantDTO.getPlantType());

        plant.setPotSize(plantDTO.getPotSize());

        plant.setIsIndoor(plantDTO.getIsIndoor());

        plant.setLocation(plantDTO.getLocation());

        plant.setLastWateredDate(
                plantDTO.getLastWateredDate()
        );

        plant.setWateringStreak(
                plantDTO.getWateringStreak()
        );

        plant.setHealth(plantDTO.getHealth());

        plant.setNotes(plantDTO.getNotes());

        plant.setCustomWateringInterval(
                plantDTO.getCustomWateringInterval()
        );

        plant.setCreatedAt(plantDTO.getCreatedAt());

        plant.setUpdatedAt(plantDTO.getUpdatedAt());

        return plant;
    }
}