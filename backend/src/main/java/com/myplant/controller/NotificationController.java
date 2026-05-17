package com.myplant.controller;

import com.myplant.dto.NotificationDTO;
import com.myplant.security.JwtTokenProvider;
import com.myplant.service.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Notification Controller
 * 
 * Handles notifications related to watering reminders and plant alerts
 * 
 * Endpoints:
 * - GET /api/notifications - get all notifications
 * - GET /api/notifications/unread - get unread notifications
 * - PUT /api/notifications/{id}/read - mark as read
 */
@RestController
@RequestMapping("/api/notifications")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class NotificationController {

    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Get all notifications for current user
     * 
     * @param authorization Bearer token
     * @return list of notifications
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationDTO>> getNotifications(
            @RequestHeader("Authorization") String authorization) {
        
        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        
        List<NotificationDTO> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get unread notifications
     * 
     * @param authorization Bearer token
     * @return list of unread notifications
     */
    @GetMapping("/unread")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(
            @RequestHeader("Authorization") String authorization) {
        
        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        
        List<NotificationDTO> unread = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(unread);
    }

    @GetMapping("/unread/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Integer> getUnreadNotificationCount(
            @RequestHeader("Authorization") String authorization) {
        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        int count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(count);
    }

    /**
     * Mark notification as read
     * 
     * @param authorization Bearer token
     * @param id notification ID
     * @return updated notification
     */
    @PutMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationDTO> markAsRead(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id) {
        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        NotificationDTO updated = notificationService.markAsRead(userId, id);
        return ResponseEntity.ok(updated);
    }

    /**
     * Clear all notifications for the current user
     * 
     * @param authorization Bearer token
     * @return no content response
     */
    @DeleteMapping("/clear")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> clearNotifications(
            @RequestHeader("Authorization") String authorization) {
        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        notificationService.clearUserNotifications(userId);
        return ResponseEntity.noContent().build();
    }
}
