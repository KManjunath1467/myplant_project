package com.myplant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * PlantCareRule Entity - Predefined care rules for different plant types
 * 
 * This class maps to the 'plant_care_rules' table in the database.
 * It serves as a plant knowledge database containing optimal care parameters
 * for different plant species (e.g., Snake Plant, Pothos, Monstera).
 * 
 * These rules are used to generate smart watering recommendations
 * and adjust watering schedules based on weather conditions.
 */
@Entity
@Table(name = "plant_care_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantCareRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Plant type name
    @Column(unique = true, nullable = false)
    private String plantName; // e.g., "Snake Plant", "Pothos", "Spider Plant"

    // Base watering schedule
    @Column(nullable = false)
    private Integer baseWateringDays; // e.g., 10 days for Snake Plant

    @Column(nullable = false)
    private String wateringFrequency; // e.g., "Every 10 days"

    // Sunlight requirements
    @Column(nullable = false)
    private String sunlightNeeds; // e.g., "Low to Medium", "Bright Indirect Light"

    // Humidity preference
    @Column(nullable = false)
    private String humidityPreference; // e.g., "Moderate", "High", "Low"

    // Temperature range (in Celsius)
    @Column(nullable = false)
    private String temperatureRange; // e.g., "16-24°C"

    // Difficulty level
    @Column(nullable = false)
    private String difficultyLevel; // e.g., "Beginner", "Intermediate", "Advanced"

    // Plant characteristics
    @Column(columnDefinition = "TEXT")
    private String description; // Brief description of the plant

    // Common issues and solutions
    @Column(columnDefinition = "TEXT")
    private String commonIssues; // e.g., "Brown leaves, Root rot, Pests"

    // Care tips
    @Column(columnDefinition = "TEXT")
    private String careTips; // Best practices for caring for this plant

    // Seasonal adjustments
    @Column(nullable = false)
    private Integer summerWateringMultiplier = 1; // Default 1x (adjust if needed)
    @Column(nullable = false)
    private Integer winterWateringMultiplier = 1; // Default 1x (reduce water in winter)

    // Weather sensitivity
    @Column(nullable = false)
    private Boolean rainSensitive = true; // If true, skip watering during rain
    @Column(nullable = false)
    private Boolean temperatureSensitive = true; // If true, adjust based on temperature

    // Recommended pot size
    @Column(nullable = false)
    private String recommendedPotSize; // e.g., "Small (4 inches)"

    // Growth rate
    @Column(nullable = false)
    private String growthRate; // e.g., "Slow", "Medium", "Fast"

    // Maximum size the plant can grow
    private String maxSize; // e.g., "12-15 inches"

    // One plant care rule can apply to many plants
    @OneToMany(mappedBy = "plantCareRule", cascade = CascadeType.ALL)
    private List<Plant> plants;
}
