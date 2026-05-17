package com.myplant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Plant DTO
 * 
 * This DTO is used for both POST requests (creating plants) and GET responses.
 * It contains all plant information needed by the frontend for display and editing.
 * 
 * For responses, it includes:
 * - ID (set by database)
 * - Plant details
 * - Care recommendations (from linked PlantCareRule)
 * - Last watering date
 * - Watering streak
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantDTO {
    private Long id;
    private String name;
    private String plantType;
    private String potSize;
    private Boolean isIndoor;
    private String location;
    private LocalDate lastWateredDate;
    private Integer wateringStreak;
    private String health;
    private String notes;
    private Integer customWateringInterval;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Related care rule information
    private Long plantCareRuleId;
    private String careRuleDescription;
    private Integer baseWateringDays;
    private String wateringFrequency;
    private String sunlightNeeds;
    private String humidityPreference;
    private String temperatureRange;
    private String difficultyLevel;
    private String recommendedPotSize;
    private String growthRate;
    private String maxSize;
    private String careTips;
}

