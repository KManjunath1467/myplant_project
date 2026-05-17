package com.myplant.service;

import com.myplant.dto.PlantCareRuleDTO;
import com.myplant.entity.PlantCareRule;
import com.myplant.exception.ResourceNotFoundException;
import com.myplant.repository.PlantCareRuleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Plant Care Rule Service
 * 
 * Handles the plant knowledge database.
 * Responsibilities:
 * 1. Provide plant care information
 * 2. Suggest watering schedules based on plant type
 * 3. Return care tips and best practices
 */
@Service
@AllArgsConstructor
@Transactional(readOnly = true) // This service mainly reads data
public class PlantCareRuleService {

    private final PlantCareRuleRepository plantCareRuleRepository;

    /**
     * Get all available plant types
     * Frontend displays these when user adds a new plant
     * 
     * @return list of all plant care rules
     */
    public List<PlantCareRuleDTO> getAllPlants() {
        return plantCareRuleRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get care information for a specific plant type
     * 
     * @param plantName the plant type name
     * @return plant care rule as DTO
     * @throws ResourceNotFoundException if plant type not found
     */
    public PlantCareRuleDTO getPlantCareRule(String plantName) {
        PlantCareRule careRule = plantCareRuleRepository.findByPlantName(plantName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Plant care rule", "plantName", plantName));
        return convertToDTO(careRule);
    }

    /**
     * Get base watering interval for a plant type
     * 
     * @param plantName the plant type name
     * @return number of days between waterings
     */
    public Integer getBaseWateringInterval(String plantName) {
        PlantCareRule careRule = plantCareRuleRepository.findByPlantName(plantName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Plant care rule", "plantName", plantName));
        return careRule.getBaseWateringDays();
    }

    /**
     * Convert PlantCareRule entity to DTO
     * 
     * @param careRule the entity
     * @return DTO
     */
    private PlantCareRuleDTO convertToDTO(PlantCareRule careRule) {
        return new PlantCareRuleDTO(
                careRule.getId(),
                careRule.getPlantName(),
                careRule.getBaseWateringDays(),
                careRule.getWateringFrequency(),
                careRule.getSunlightNeeds(),
                careRule.getHumidityPreference(),
                careRule.getTemperatureRange(),
                careRule.getDifficultyLevel(),
                careRule.getDescription(),
                careRule.getCommonIssues(),
                careRule.getCareTips(),
                careRule.getRainSensitive(),
                careRule.getTemperatureSensitive(),
                careRule.getRecommendedPotSize(),
                careRule.getGrowthRate(),
                careRule.getMaxSize()
        );
    }
}
