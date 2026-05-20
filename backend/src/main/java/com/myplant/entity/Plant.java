package com.myplant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Plant Entity
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

    // Reference to owner
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Plant Name
    @Column(nullable = false)
    private String name;

    // Plant Type
    @Column(nullable = false)
    private String plantType;

    // Pot Size
    @Column(nullable = false)
    private String potSize;

    // Indoor / Outdoor
    @Column(nullable = false)
    private Boolean isIndoor;

    // Plant Location
    @Column(nullable = false)
    private String location;

    // Last Watered Date
    private LocalDate lastWateredDate;

    // Watering Streak
    @Column(nullable = false)
    private Integer wateringStreak = 0;

    // Plant Health
    @Column(nullable = false)
    private String health = "Healthy";

    // Notes
    @Column(columnDefinition = "TEXT")
    private String notes;

    // Custom Watering Interval
    private Integer customWateringInterval;

    // Image URL
    @Column(name = "image_url")
    private String imageUrl;

    // Created At
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt =
            LocalDateTime.now();

    // Updated At
    @Column(nullable = false)
    private LocalDateTime updatedAt =
            LocalDateTime.now();

    // Watering History
    @OneToMany(
            mappedBy = "plant",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<WateringHistory> wateringHistory;

    // Plant Care Rule
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_care_rule_id")
    private PlantCareRule plantCareRule;
}