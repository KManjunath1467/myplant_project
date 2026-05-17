package com.myplant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Notification DTO
 * 
 * This DTO is used to return notification information to the frontend.
 * Frontend can display notifications in the notification center.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private Long userId;
    private Long plantId;
    private String plantName;
    private String notificationType;
    private String title;
    private String message;
    private String channel;
    private String status;
    private LocalDateTime sentAt;
    private LocalDateTime viewedAt;
    private LocalDateTime createdAt;
    private boolean read;
    private String timestamp;
}
