package com.myplant.service;

import com.myplant.dto.WeatherDTO;
import com.myplant.entity.Plant;
import com.myplant.entity.PlantCareRule;
import com.myplant.repository.PlantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Watering Scheduler Service
 * 
 * This is the CORE of the smart plant care system!
 * 
 * Responsibilities:
 * 1. Calculate when each plant should be watered
 * 2. Adjust watering schedule based on weather
 * 3. Consider plant type, pot size, and location
 * 4. Track watering needs and overdue plants
 * 
 * Smart Watering Logic:
 * - Base watering interval from plant care rules
 * - Weather adjustments (rain, temperature, humidity)
 * - User's custom watering intervals
 * - Plant health status
 * - Last watering date
 */
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class WateringSchedulerService {

    private final PlantRepository plantRepository;
    private final WeatherService weatherService;
    private final PlantCareRuleService plantCareRuleService;

    /**
     * Calculate if a plant needs watering today
     * 
     * Algorithm:
     * 1. Get plant's base watering interval
     * 2. Adjust based on current weather
     * 3. Check days since last watering
     * 4. Return true if should water
     * 
     * @param plant the plant to check
     * @return true if plant needs watering today
     */
    public boolean shouldWaterToday(Plant plant) {
        if (plant.getLastWateredDate() == null) {
            // Plant has never been watered - water immediately
            return true;
        }

        // Get watering interval
        Integer wateringInterval = getCalculatedWateringInterval(plant);

        // Calculate days since last watering
        long daysSinceWatering = ChronoUnit.DAYS.between(plant.getLastWateredDate(), LocalDate.now());

        // Water if interval has passed
        return daysSinceWatering >= wateringInterval;
    }

    /**
     * Calculate the adjusted watering interval for a plant based on weather
     * 
     * Steps:
     * 1. Get base interval from plant care rules
     * 2. Use custom interval if user set one
     * 3. Adjust for current weather
     * 4. Account for seasonal adjustments
     * 5. Return final interval as integer (days)
     * 
     * @param plant the plant
     * @return watering interval in days
     */
    public Integer getCalculatedWateringInterval(Plant plant) {
        // Start with base interval
        Integer baseInterval = 10; // Default

        if (plant.getCustomWateringInterval() != null) {
            // Use custom interval if user set one
            baseInterval = plant.getCustomWateringInterval();
        } else if (plant.getPlantCareRule() != null) {
            // Use care rule interval
            baseInterval = plant.getPlantCareRule().getBaseWateringDays();
        }

        // Adjust for weather
        Double weatherMultiplier = 1.0;
        try {
            WeatherDTO weather = weatherService.getWeatherForCity(plant.getUser().getCity());
            weatherMultiplier = weatherService.getWateringIntervalMultiplier(weather);
        } catch (Exception e) {
            // If weather service fails, use default
            weatherMultiplier = 1.0;
        }

        // Calculate final interval
        int adjustedInterval = Math.max(1, (int) (baseInterval * weatherMultiplier));

        return adjustedInterval;
    }

    /**
     * Get the next watering date for a plant
     * 
     * @param plant the plant
     * @return LocalDate when the plant should next be watered
     */
    public LocalDate getNextWateringDate(Plant plant) {
        if (plant.getLastWateredDate() == null) {
            return LocalDate.now(); // Water now
        }

        Integer interval = getCalculatedWateringInterval(plant);
        return plant.getLastWateredDate().plusDays(interval);
    }

    /**
     * Get days until next watering
     * 
     * @param plant the plant
     * @return number of days (negative if overdue)
     */
    public Long getDaysUntilNextWatering(Plant plant) {
        LocalDate nextWateringDate = getNextWateringDate(plant);
        return ChronoUnit.DAYS.between(LocalDate.now(), nextWateringDate);
    }

    /**
     * Check if plant is overdue for watering
     * 
     * @param plant the plant
     * @return true if plant is overdue
     */
    public boolean isOverdueForWatering(Plant plant) {
        Long daysUntil = getDaysUntilNextWatering(plant);
        return daysUntil < 0;
    }

    /**
     * Get watering status for a plant
     * 
     * @param plant the plant
     * @return status string: "OVERDUE", "DUE_SOON", "NOT_DUE", "WATERED_TODAY"
     */
    public String getWateringStatus(Plant plant) {
        if (plant.getLastWateredDate() != null && 
            plant.getLastWateredDate().equals(LocalDate.now())) {
            return "WATERED_TODAY";
        }

        Long daysUntil = getDaysUntilNextWatering(plant);

        if (daysUntil < 0) {
            return "OVERDUE";
        } else if (daysUntil <= 1) {
            return "DUE_SOON";
        } else {
            return "NOT_DUE";
        }
    }

    /**
     * Generate a watering recommendation message for a plant
     * 
     * Used in notifications and dashboard.
     * 
     * @param plant the plant
     * @param weather current weather (optional)
     * @return human-readable recommendation
     */
    public String generateWateringRecommendation(Plant plant, WeatherDTO weather) {
        String plantName = plant.getName();
        Long daysUntil = getDaysUntilNextWatering(plant);

        if (weather != null && weather.getIsRaining()) {
            return String.format(
                    "🌧️ It's raining today! Skip watering '%s'. Plants usually get enough water from rain.",
                    plantName
            );
        }

        if (daysUntil < 0) {
            return String.format(
                    "🔴 URGENT! '%s' needs water TODAY! It's been %d days since last watering.",
                    plantName, Math.abs(daysUntil)
            );
        } else if (daysUntil == 0) {
            return String.format("🟡 Time to water '%s'!", plantName);
        } else if (daysUntil == 1) {
            return String.format("🟡 Water '%s' tomorrow!", plantName);
        } else if (daysUntil <= 3) {
            return String.format("🟢 '%s' will need water in %d days.", plantName, daysUntil);
        } else {
            return String.format("✅ '%s' is well-watered for now (next watering in %d days).", 
                    plantName, daysUntil);
        }
    }

    /**
     * Determine if a plant might have health issues based on watering
     * 
     * @param plant the plant
     * @return health recommendation message
     */
    public String getHealthRecommendation(Plant plant) {
        Long daysUntil = getDaysUntilNextWatering(plant);
        Long daysSinceWatering = ChronoUnit.DAYS.between(
                plant.getLastWateredDate() != null ? plant.getLastWateredDate() : LocalDate.now(),
                LocalDate.now()
        );

        // If severely overdue
        if (daysUntil < -7) {
            return "⚠️ WARNING: This plant may be suffering from lack of water!";
        }

        // If too frequently watered
        if (plant.getLastWateredDate() != null && daysSinceWatering < 2 && 
            plant.getPlantCareRule() != null && 
            plant.getPlantCareRule().getBaseWateringDays() > 3) {
            return "⚠️ WARNING: This plant may be over-watered!";
        }

        return "✅ Plant health looks good!";
    }
}
