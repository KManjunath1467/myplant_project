package com.myplant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Plant Care Rule DTO
 * 
 * This DTO returns plant care knowledge from the database.
 * It provides information about optimal care for different plant types.
 * 
 * Used when:
 * 1. User selects a plant type - shows care requirements
 * 2. Frontend displays care recommendations
 * 3. System calculates watering schedules
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantCareRuleDTO {
    private Long id;
    private String plantName;
    private Integer baseWateringDays;
    private String wateringFrequency;
    private String sunlightNeeds;
    private String humidityPreference;
    private String temperatureRange;
    private String difficultyLevel;
    private String description;
    private String commonIssues;
    private String careTips;
    private Boolean rainSensitive;
    private Boolean temperatureSensitive;
    private String recommendedPotSize;
    private String growthRate;
    private String maxSize;
}
