package com.myplant.scheduler;

import com.myplant.dto.WeatherDTO;
import com.myplant.entity.Plant;
import com.myplant.entity.User;
import com.myplant.repository.PlantRepository;
import com.myplant.repository.UserRepository;
import com.myplant.service.NotificationService;
import com.myplant.service.WateringSchedulerService;
import com.myplant.service.WeatherService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Watering Reminder Scheduler
 * 
 * This component automatically sends watering reminders to users.
 * 
 * How it works:
 * 1. Runs daily at 9:00 AM (configurable)
 * 2. For each user and their plants:
 *    a. Get current weather
 *    b. Calculate if plant needs watering
 *    c. Check user's notification preferences
 *    d. Send email/WhatsApp notifications
 * 3. Logs all activities for debugging
 * 
 * Cron Expression: "0 0 9 * * *"
 * - 0: seconds (every second)
 * - 0: minutes (at 00 minutes)
 * - 9: hours (at 9 AM)
 * - *: day of month (every day)
 * - *: month (every month)
 * - *: day of week (every day)
 */
@Component
@AllArgsConstructor
@Slf4j // Lombok annotation for logger
public class WateringReminderScheduler {

    private final UserRepository userRepository;
    private final PlantRepository plantRepository;
    private final WateringSchedulerService wateringSchedulerService;
    private final WeatherService weatherService;
    private final NotificationService notificationService;

    /**
     * Daily task: Check all plants and send watering reminders
     * Runs every day at 9:00 AM
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDailyWateringReminders() {
        log.info("========== WATERING REMINDER SCHEDULER STARTED ==========");

        try {
            // Get all users in system
            List<User> allUsers = userRepository.findAll();
            log.info("Processing {} users", allUsers.size());

            // Process each user
            for (User user : allUsers) {
                processPlantsForUser(user);
            }

            log.info("========== WATERING REMINDER SCHEDULER COMPLETED ==========");
        } catch (Exception e) {
            log.error("Error in watering reminder scheduler", e);
        }
    }

    /**
     * Process all plants for a user
     * Check which ones need watering and send reminders
     * 
     * @param user the user to process
     */
    private void processPlantsForUser(User user) {
        try {
            log.debug("Processing plants for user: {} ({})", user.getEmail(), user.getId());

            // Get user's city weather
            WeatherDTO weather = weatherService.getWeatherForCity(user.getCity());
            log.debug("Weather for {}: Temp={}°C, Humidity={}%, Raining={}", 
                    user.getCity(), weather.getTemperature(), weather.getHumidity(), weather.getIsRaining());

            // Get all plants for this user
            List<Plant> userPlants = plantRepository.findByUser(user);
            log.debug("User has {} plants", userPlants.size());

            // Check each plant
            for (Plant plant : userPlants) {
                checkAndNotifyPlant(user, plant, weather);
            }
        } catch (Exception e) {
            log.error("Error processing plants for user: {}", user.getEmail(), e);
        }
    }

    /**
     * Check if a plant needs watering and send notification if yes
     * 
     * @param user the plant owner
     * @param plant the plant to check
     * @param weather current weather
     */
    private void checkAndNotifyPlant(User user, Plant plant, WeatherDTO weather) {
        try {
            String plantName = plant.getName();
            log.debug("Checking plant: {}", plantName);

            // Check if plant needs watering
            if (!wateringSchedulerService.shouldWaterToday(plant)) {
                log.debug("Plant {} doesn't need water today", plantName);
                return;
            }

            log.info("Plant {} needs water today!", plantName);

            // Skip if raining and plant is sensitive to rain
            if (weather.getIsRaining() && plant.getPlantCareRule() != null && 
                plant.getPlantCareRule().getRainSensitive()) {
                log.info("Skipping notification for {} - it's raining and plant is rain-sensitive", plantName);
                return;
            }

            // Generate notification message
            String message = wateringSchedulerService.generateWateringRecommendation(plant, weather);
            String title = "Time to water " + plantName + "! 🌱";

            log.info("Sending notification for {}: {}", plantName, message);

            // Send notification if user enabled it
            if (user.getEmailNotifications()) {
                notificationService.createAndSendNotification(
                        user.getId(),
                        plant.getId(),
                        title,
                        message,
                        true,  // Send email
                        false  // Don't send WhatsApp (unless configured)
                );
                log.debug("Email notification sent for {}", plantName);
            }

            // Send WhatsApp if enabled
            if (user.getWhatsappNotifications()) {
                notificationService.createAndSendNotification(
                        user.getId(),
                        plant.getId(),
                        title,
                        message,
                        false, // Don't send email
                        true   // Send WhatsApp
                );
                log.debug("WhatsApp notification sent for {}", plantName);
            }

        } catch (Exception e) {
            log.error("Error checking plant: {}", plant.getName(), e);
        }
    }

    /**
     * Optional: Additional scheduler to check for overdue plants (runs every 6 hours)
     * Sends alert notifications for plants that are severely overdue
     */
    @Scheduled(cron = "0 0 */6 * * *") // Every 6 hours
    public void checkOverduePlants() {
        log.info("========== OVERDUE PLANT CHECKER STARTED ==========");

        try {
            List<User> allUsers = userRepository.findAll();

            for (User user : allUsers) {
                List<Plant> userPlants = plantRepository.findByUser(user);

                for (Plant plant : userPlants) {
                    Long daysUntil = wateringSchedulerService.getDaysUntilNextWatering(plant);

                    // Alert if overdue by more than 3 days
                    if (daysUntil < -3) {
                        log.warn("ALERT: Plant {} is {} days overdue!", plant.getName(), Math.abs(daysUntil));

                        if (user.getEmailNotifications()) {
                            notificationService.createAndSendNotification(
                                    user.getId(),
                                    plant.getId(),
                                    "🔴 ALERT: " + plant.getName() + " is severely overdue for watering!",
                                    "Your plant " + plant.getName() + " hasn't been watered in " + 
                                    Math.abs(daysUntil) + " days. It needs immediate attention!",
                                    true,
                                    false
                            );
                        }
                    }
                }
            }

            log.info("========== OVERDUE PLANT CHECKER COMPLETED ==========");
        } catch (Exception e) {
            log.error("Error in overdue plant checker", e);
        }
    }
}
