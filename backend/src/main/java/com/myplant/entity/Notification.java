package com.myplant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Notification Entity - Records all notifications sent to users
 * 
 * This class maps to the 'notifications' table in the database.
 * It tracks:
 * 1. Watering reminders sent via Email/WhatsApp
 * 2. Plant health alerts
 * 3. Weather-based recommendations
 * 4. System notifications
 * 
 * This helps users see their notification history and track
 * which plants need care.
 */
@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Reference to the user who receives the notification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Reference to the plant (if this notification is plant-specific)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id")
    private Plant plant;

    // Notification type
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType; // WATERING_REMINDER, PLANT_ALERT, WEATHER_ALERT, etc.

    // Notification title
    @Column(nullable = false)
    private String title; // e.g., "Time to Water Your Snake Plant!"

    // Notification message
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message; // Full message content

    // Notification channel
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationChannel channel; // EMAIL, WHATSAPP, IN_APP

    // Status of the notification
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationStatus status = NotificationStatus.PENDING; // PENDING, SENT, FAILED, VIEWED

    // When the notification was sent
    private LocalDateTime sentAt;

    // When the notification was viewed by the user
    private LocalDateTime viewedAt;

    // If sending failed, store the reason
    @Column(columnDefinition = "TEXT")
    private String failureReason;

    // Timestamp when this record was created
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Enum for notification types
    public enum NotificationType {
        WATERING_REMINDER,      // Time to water the plant
        PLANT_ALERT,             // Plant needs attention (yellowing, wilting, etc.)
        WEATHER_ALERT,           // Weather-based recommendation (rain, heat, cold)
        ACHIEVEMENT,             // Watering streak or milestone
        SYSTEM_NOTIFICATION,     // General system updates
        HEALTH_TIP,              // AI-based plant care tips
        LOW_MAINTENANCE_SUGGESTION // Suggestions for low-maintenance plants
    }

    // Enum for notification channels
    public enum NotificationChannel {
        EMAIL,
        WHATSAPP,
        IN_APP
    }

    // Enum for notification status
    public enum NotificationStatus {
        PENDING,    // Waiting to be sent
        SENT,       // Successfully sent
        FAILED,     // Failed to send
        VIEWED      // User viewed the notification
    }
}
