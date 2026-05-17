package com.myplant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Weather DTO
 * 
 * This DTO is used to represent weather data from OpenWeatherMap API.
 * It's used internally by the scheduler to adjust watering schedules.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDTO {
    private String city;
    private Double temperature;
    private Double humidity;
    private Integer pressure;
    private String weatherMain; // e.g., "Rain", "Clear", "Cloudy"
    private String weatherDescription; // e.g., "light rain"
    private Boolean isRaining;
}
