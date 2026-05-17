package com.myplant.controller;

import com.myplant.dto.PlantDTO;
import com.myplant.dto.WateringHistoryDTO;
import com.myplant.dto.NotificationDTO;
import com.myplant.security.JwtTokenProvider;
import com.myplant.service.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dashboard Controller
 * 
 * Provides aggregated dashboard data combining multiple services
 * Shows:
 * - Upcoming watering tasks
 * - Recently watered plants
 * - Plant health status
 * - Recent notifications
 * 
 * Endpoints:
 * - GET /api/dashboard - complete dashboard data
 * - GET /api/dashboard/tasks - upcoming watering tasks
 */
@RestController
@RequestMapping("/api/dashboard")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class DashboardController {

    private final PlantService plantService;
    private final WateringHistoryService wateringHistoryService;
    private final NotificationService notificationService;
    private final WateringSchedulerService wateringSchedulerService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Get complete dashboard data
     * 
     * Aggregates data from multiple services to provide a comprehensive view
     * 
     * Response:
     * {
     *   "plants": [...],
     *   "upcomingTasks": [...],
     *   "recentlyWatered": [...],
     *   "notifications": [...],
     *   "stats": {
     *     "totalPlants": 5,
     *     "plantsDueForWatering": 2,
     *     "overduePlants": 0
     *   }
     * }
     * 
     * @param authorization Bearer token
     * @return dashboard data
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @RequestHeader("Authorization") String authorization) {
        
        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        Map<String, Object> dashboard = new HashMap<>();

        // Get all plants and build the dashboard state
        List<PlantDTO> plants = plantService.getUserPlants(userId);
        dashboard.put("plants", plants);

        List<com.myplant.entity.Plant> plantEntities = plantService.getUserPlantEntities(userId);

        // Get upcoming watering tasks
        List<Map<String, Object>> upcomingTasks = plantEntities.stream()
                .map(plant -> {
                    Map<String, Object> task = new HashMap<>();
                    task.put("plantId", plant.getId());
                    task.put("plantName", plant.getName());
                    task.put("daysUntilWatering", wateringSchedulerService.getDaysUntilNextWatering(plant));
                    task.put("status", wateringSchedulerService.getWateringStatus(plant));
                    task.put("priority", getPriorityFromStatus(wateringSchedulerService.getWateringStatus(plant)));
                    return task;
                })
                .filter(task -> !"WATERED_TODAY".equals(task.get("status")))
                .sorted((a, b) -> Long.compare(
                    (Long) a.get("daysUntilWatering"), 
                    (Long) b.get("daysUntilWatering")))
                .limit(5)
                .toList();
        dashboard.put("upcomingTasks", upcomingTasks);

        // Get recently watered plants
        List<WateringHistoryDTO> recentWatering = wateringHistoryService.getRecentWatering(userId);
        dashboard.put("recentlyWatered", recentWatering);

        // Get recent notifications and unread count
        List<NotificationDTO> notifications = notificationService.getUserNotifications(userId);
        dashboard.put("recentNotifications", notifications.stream().limit(5).toList());
        dashboard.put("unreadNotificationsCount", notificationService.getUnreadCount(userId));

        // Get statistics
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPlants", plants.size());
        
        // Calculate plants watered today
        List<WateringHistoryDTO> todayWatering = wateringHistoryService.getRecentWatering(userId).stream()
                .filter(w -> w.getWateredDate().equals(java.time.LocalDate.now()))
                .toList();
        stats.put("plantsWateredToday", todayWatering.size());
        
        // Calculate plants due for watering
        long plantsDueForWatering = plantEntities.stream()
                .filter(wateringSchedulerService::shouldWaterToday)
                .count();
        stats.put("plantsDueForWatering", (int) plantsDueForWatering);
        
        // Calculate overdue plants
        long overduePlants = plantEntities.stream()
                .filter(wateringSchedulerService::isOverdueForWatering)
                .count();
        stats.put("overduePlants", (int) overduePlants);
        
        // Calculate health score (percentage of healthy plants)
        long healthyPlants = plantEntities.stream()
                .filter(p -> "Healthy".equals(p.getHealth()))
                .count();
        double healthScore = plantEntities.isEmpty() ? 100.0 : (healthyPlants * 100.0) / plantEntities.size();
        stats.put("healthScore", Math.round(healthScore));
        
        dashboard.put("stats", stats);

        return ResponseEntity.ok(dashboard);
    }

    /**
     * Get upcoming watering tasks
     * 
     * Response:
     * [
     *   {
     *     "plantId": 1,
     *     "plantName": "Snake Plant",
     *     "daysUntil": 2,
     *     "status": "DUE_SOON",
     *     "recommendation": "Water tomorrow"
     *   },
     *   ...
     * ]
     * 
     * @param authorization Bearer token
     * @return list of upcoming tasks
     */
    @GetMapping("/tasks")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Map<String, Object>>> getUpcomingTasks(
            @RequestHeader("Authorization") String authorization) {
        
        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        List<PlantDTO> plants = plantService.getUserPlants(userId);
        List<com.myplant.entity.Plant> plantEntities = plantService.getUserPlantEntities(userId);

        List<Map<String, Object>> tasks = plantEntities.stream()
                .map(plant -> {
                    String status = wateringSchedulerService.getWateringStatus(plant);
                    Map<String, Object> task = new HashMap<>();
                    task.put("plantId", plant.getId());
                    task.put("plantName", plant.getName());
                    task.put("lastWateredDate", plant.getLastWateredDate());
                    task.put("daysUntilWatering", wateringSchedulerService.getDaysUntilNextWatering(plant));
                    task.put("status", status);
                    task.put("priority", getPriorityFromStatus(status));
                    task.put("recommendation", wateringSchedulerService.generateWateringRecommendation(plant, null));
                    return task;
                })
                .toList();

        return ResponseEntity.ok(tasks);
    }

    /**
     * Get priority level from watering status
     * 
     * @param status watering status
     * @return priority string
     */
    private String getPriorityFromStatus(String status) {
        return switch (status) {
            case "OVERDUE" -> "high";
            case "DUE_SOON" -> "medium";
            default -> "low";
        };
    }
}
