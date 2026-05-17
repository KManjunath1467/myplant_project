package com.myplant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

/**
 * User Entity - Represents a user in the system
 * 
 * This class maps to the 'users' table in the database.
 * It stores user authentication credentials, profile information,
 * and preferences for plant care notifications.
 */
@Entity
@Table(name = "users")
@Getter
@Setter// Lombok: Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor // Lombok: Generates default constructor
@AllArgsConstructor // Lombok: Generates constructor with all fields
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    // User's city for weather integration
    @Column(nullable = false)
    private String city;

    // User's phone number for WhatsApp notifications
    private String phoneNumber;

    // Notification preferences
    @Column(nullable = false)
    private Boolean emailNotifications = true;

    @Column(nullable = false)
    private Boolean whatsappNotifications = false;

    // Account status
    @Column(nullable = false)
    private Boolean active = true;

    // Timestamps
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // One user can have many plants (One-to-Many relationship)
    @JsonIgnore
   @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
   private List<Plant> plants;

    // One user can have many notifications
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;
}
