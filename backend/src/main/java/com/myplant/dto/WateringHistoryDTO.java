package com.myplant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * Watering History DTO
 * 
 * This DTO is used to return watering history records.
 * Frontend can use this to:
 * 1. Display watering timeline for each plant
 * 2. Calculate watering patterns
 * 3. Track plant health history
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WateringHistoryDTO {
    private Long id;
    private Long plantId;
    private String plantName;
    private LocalDate wateredDate;
    private String notes;
    private Boolean onTime;
    private String waterAmount;
    private String plantHealthAtWatering;
    private String weatherCondition;
}
