package com.myplant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Application Configuration
 * 
 * Defines beans needed across the application
 */
@Configuration
@EnableScheduling // Enable @Scheduled jobs for automatic watering reminders
public class ApplicationConfig {

    /**
     * WebClient Bean
     * 
     * Used for making HTTP calls to external APIs (like OpenWeatherMap)
     * Reactive, non-blocking HTTP client
     * 
     * @return WebClient bean
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://api.openweathermap.org")
                .build();
    }

          @Bean
          public RestTemplate restTemplate() {
              return new RestTemplate();
           }
}
