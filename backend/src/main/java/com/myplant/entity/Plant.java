package com.myplant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Plant Entity - Represents a plant belonging to a user
 * 
 * This class maps to the 'plants' table in the database.
 * It stores information about each plant like name, type, location,
 * and watering history to provide smart watering recommendations.
 */
@Entity
@Table(name = "plants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Reference to the plant owner
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Plant basic information
    @Column(nullable = false)
    private String name; // e.g., "My Snake Plant", "Office Pothos"

    @Column(nullable = false)
    private String plantType; // e.g., "Snake Plant", "Pothos", "Monstera"

    // Pot/Container information
    @Column(nullable = false)
    private String potSize; // e.g., "Small (4 inches)", "Medium (6 inches)", "Large (8+ inches)"

    // Location information
    @Column(nullable = false)
    private Boolean isIndoor; // true = indoor, false = outdoor

    @Column(nullable = false)
    private String location; // e.g., "Living Room", "Bedroom", "Office"

    // Last watering information
    private LocalDate lastWateredDate;

    // Watering streak tracking (motivation feature)
    @Column(nullable = false)
    private Integer wateringStreak = 0; // Number of consecutive on-time waterings

    // Plant health status
    @Column(nullable = false)
    private String health = "Healthy"; // Healthy, Fair, Poor

    // Notes about the plant
    @Column(columnDefinition = "TEXT")
    private String notes;

    // Custom watering interval (in days) - if user wants to override default
    private Integer customWateringInterval;

    // Timestamps
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // One plant can have many watering history records
    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WateringHistory> wateringHistory;

    // Many plants can reference one plant care rule
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_care_rule_id")
    private PlantCareRule plantCareRule;
}
