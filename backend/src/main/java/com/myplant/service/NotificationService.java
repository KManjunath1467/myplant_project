package com.myplant.service;

import com.myplant.dto.NotificationDTO;
import com.myplant.entity.Notification;
import com.myplant.entity.Plant;
import com.myplant.entity.User;
import com.myplant.repository.NotificationRepository;
import com.myplant.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Notification Service
 * 
 * Handles sending and tracking notifications.
 * Responsibilities:
 * 1. Create notification records in database
 * 2. Send email notifications via Spring Mail
 * 3. Send WhatsApp notifications via Twilio (future implementation)
 * 4. Track notification delivery status
 * 5. Provide notification history to users
 * 
 * Note: WhatsApp integration would require Twilio service implementation
 */
@Service
@AllArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private final UserService userService;
    // Future: private final TwilioWhatsAppService twilioService;

    /**
     * Create and send a watering reminder notification
     * 
     * @param userId the user's ID
     * @param plantId the plant's ID (if plant-specific)
     * @param title notification title
     * @param message notification message
     * @param sendEmail whether to send via email
     * @param sendWhatsApp whether to send via WhatsApp
     * @return created notification as DTO
     */
    public NotificationDTO createAndSendNotification(
            Long userId,
            Long plantId,
            String title,
            String message,
            boolean sendEmail,
            boolean sendWhatsApp) {

        User user = userService.getUserById(userId);

        // Create notification record
        Notification notification = new Notification();
        notification.setUser(user);

        if (plantId != null) {
            Plant plant = new Plant();
            plant.setId(plantId);
            notification.setPlant(plant);
        }

        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(Notification.NotificationType.WATERING_REMINDER);
        notification.setStatus(Notification.NotificationStatus.PENDING);
        notification.setChannel(Notification.NotificationChannel.IN_APP);

        // Send email if requested
        if (sendEmail) {
            notification.setChannel(Notification.NotificationChannel.EMAIL);
            sendEmailNotification(notification);
        }

        // Send WhatsApp if requested (future implementation)
        if (sendWhatsApp) {
            notification.setChannel(Notification.NotificationChannel.WHATSAPP);
            // sendWhatsAppNotification(notification);
        }

        Notification savedNotification = notificationRepository.save(notification);
        return convertToDTO(savedNotification);
    }

    /**
     * Send email notification
     * 
     * @param notification the notification to send
     */
    private void sendEmailNotification(Notification notification) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(notification.getUser().getEmail());
            email.setSubject("[MyPlant] " + notification.getTitle());
            email.setText(notification.getMessage() + "\n\n" +
                    "Visit MyPlant dashboard to manage your plants and watering schedules.\n\n" +
                    "Happy planting! 🌱");

            mailSender.send(email);

            notification.setStatus(Notification.NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            notification.setStatus(Notification.NotificationStatus.FAILED);
            notification.setFailureReason(e.getMessage());
        }
    }

    /**
     * Send WhatsApp notification (placeholder for Twilio integration)
     * 
     * @param notification the notification to send
     */
    private void sendWhatsAppNotification(Notification notification) {
        try {
            // Future: Use Twilio WhatsApp API
            // twilioService.sendWhatsAppMessage(notification.getUser().getPhoneNumber(), notification.getMessage());
            
            notification.setStatus(Notification.NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            notification.setStatus(Notification.NotificationStatus.FAILED);
            notification.setFailureReason(e.getMessage());
        }
    }

    /**
     * Get all notifications for a user
     * 
     * @param userId the user's ID
     * @return list of notifications
     */
    public List<NotificationDTO> getUserNotifications(Long userId) {
        User user = userService.getUserById(userId);

        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get unread notifications for a user
     * 
     * @param userId the user's ID
     * @return list of unread notifications
     */
    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        User user = userService.getUserById(userId);

        return notificationRepository.findByUserAndStatus(user, Notification.NotificationStatus.PENDING)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get unread notification count for a user
     *
     * @param userId the user's ID
     * @return count of unread notifications
     */
    public int getUnreadCount(Long userId) {
        User user = userService.getUserById(userId);
        return notificationRepository.countByUserAndStatus(user, Notification.NotificationStatus.PENDING);
    }

    /**
     * Clear notifications for a user
     *
     * @param userId the user's ID
     */
    public void clearUserNotifications(Long userId) {
        User user = userService.getUserById(userId);
        notificationRepository.deleteByUser(user);
    }

    /**
     * Mark notification as read
     * 
     * @param userId the user's ID
     * @param notificationId the notification's ID
     * @return updated notification
     */
    public NotificationDTO markAsRead(Long userId, Long notificationId) {
        User user = userService.getUserById(userId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Notification does not belong to the current user.");
        }
        
        notification.setStatus(Notification.NotificationStatus.VIEWED);
        notification.setViewedAt(LocalDateTime.now());
        
        Notification updated = notificationRepository.save(notification);
        return convertToDTO(updated);
    }

    /**
     * Convert Notification entity to DTO
     * 
     * @param notification the entity
     * @return DTO
     */
    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO(
                notification.getId(),
                notification.getUser().getId(),
                notification.getPlant() != null ? notification.getPlant().getId() : null,
                notification.getPlant() != null ? notification.getPlant().getName() : null,
                notification.getNotificationType().toString(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getChannel().toString(),
                notification.getStatus().toString(),
                notification.getSentAt(),
                notification.getViewedAt(),
                notification.getCreatedAt(),
                notification.getViewedAt() != null || notification.getStatus() == Notification.NotificationStatus.VIEWED,
                notification.getCreatedAt() != null ? notification.getCreatedAt().toString() : null
        );
        return dto;
    }
}
