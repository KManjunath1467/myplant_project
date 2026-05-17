package com.myplant.repository;

import com.myplant.entity.Notification;
import com.myplant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Notification Repository
 * 
 * This repository provides database operations for Notification entities.
 * 
 * Custom methods:
 * - findByUser(user) - get all notifications for a user
 * - findByUserOrderByCreatedAtDesc(user) - get notifications sorted by date
 * - findByUserAndStatus(user, status) - get notifications by status
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    /**
     * Find all notifications for a user
     * 
     * @param user the user entity
     * @return list of notifications
     */
    List<Notification> findByUser(User user);

    /**
     * Find notifications for a user sorted by creation date (newest first)
     * 
     * @param user the user entity
     * @return list of notifications sorted by date
     */
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Find notifications by user and status
     * Useful for finding pending notifications to send
     * 
     * @param user the user entity
     * @param status the notification status
     * @return list of notifications with the given status
     */
    List<Notification> findByUserAndStatus(User user, Notification.NotificationStatus status);

    int countByUserAndStatus(User user, Notification.NotificationStatus status);

    void deleteByUser(User user);

    /**
     * Find all pending notifications (that haven't been sent yet)
     * Used by the scheduler to send pending notifications
     * 
     * @return list of pending notifications
     */
    @Query("SELECT n FROM Notification n WHERE n.status = 'PENDING'")
    List<Notification> findPendingNotifications();

    /**
     * Find unsent notifications for a user created after a certain time
     * 
     * @param userId the user's ID
     * @param createdAfter only get notifications created after this time
     * @return list of unsent notifications
     */
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId " +
           "AND n.status IN ('PENDING', 'FAILED') " +
           "AND n.createdAt > :createdAfter")
    List<Notification> findUnsentNotificationsSince(
            @Param("userId") Long userId,
            @Param("createdAfter") LocalDateTime createdAfter
    );
}
