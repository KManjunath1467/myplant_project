package com.myplant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * WateringHistory Entity - Records every watering event
 * 
 * This class maps to the 'watering_history' table in the database.
 * It maintains a complete history of when each plant was watered,
 * allowing the system to:
 * 1. Track watering streaks
 * 2. Calculate optimal watering schedules
 * 3. Identify neglected plants
 * 4. Provide analytics and statistics
 */
@Entity
@Table(name = "watering_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WateringHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Reference to the plant that was watered
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;

    // Timestamp when the plant was watered
    @Column(nullable = false)
    private LocalDateTime wateredAt = LocalDateTime.now();

    // Optional: Notes about the watering event
    @Column(columnDefinition = "TEXT")
    private String notes; // e.g., "Watered more due to hot weather", "Used filtered water"

    // Whether this was an on-time watering (for streak tracking)
    @Column(nullable = false)
    private Boolean onTime = true; // true = watered on schedule, false = late

    // Amount of water given (optional)
    private String waterAmount; // e.g., "500 ml", "1 liter"

    // Plant health at time of watering
    private String plantHealthAtWatering; // e.g., "Healthy", "Slightly dry", "Overwatered"

    // Weather condition at time of watering (for analysis)
    private String weatherCondition; // e.g., "Sunny", "Rainy", "Cloudy"

    // Timestamp when this record was created
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
