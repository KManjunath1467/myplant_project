package com.myplant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * MyPlant Application
 * 
 * Smart Plant Care Web Application
 * 
 * Main features:
 * - User authentication with JWT
 * - Plant management with watering tracking
 * - Smart watering recommendations based on weather
 * - Automated notification scheduler
 * - Dashboard with analytics
 * 
 * Tech Stack:
 * - Spring Boot 3.1.5
 * - Spring Data JPA for database access
 * - Spring Security with JWT
 * - MySQL database
 * - Spring Scheduler for cron jobs
 * 
 * To run the application:
 * mvn spring-boot:run
 * 
 * API will be available at: http://localhost:8080
 */
@SpringBootApplication
@EnableScheduling // Enable scheduled tasks
public class MyPlantApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyPlantApplication.class, args);
    }
}
