package com.myplant.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myplant.dto.PlantDTO;
import com.myplant.dto.WeatherDTO;
import com.myplant.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Weather Service
 * 
 * Integrates with OpenWeatherMap API to fetch real-time weather data.
 * Responsibilities:
 * 1. Fetch current weather for a city
 * 2. Parse weather data (temperature, humidity, rain, etc.)
 * 3. Determine watering adjustments based on weather
 * 4. Create user-facing weather care recommendations
 * 
 * Weather data is used to:
 * - Skip watering when it rains
 * - Increase watering frequency during hot weather
 * - Decrease watering during cold weather
 * - Adjust based on humidity levels
 * 
 * Note: Requires OpenWeatherMap API key in application.properties
 * Get free API key: https://openweathermap.org/api
 */
@Service
@Transactional(readOnly = true)
public class WeatherService {

    @Value("${openweathermap.api.key:}")
    private String apiKey;

    @Value("${openweathermap.api.url:https://api.openweathermap.org/data/2.5/weather}")
    private String apiUrl;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final PlantService plantService;

    public WeatherService(WebClient.Builder webClientBuilder,
                          ObjectMapper objectMapper,
                          UserService userService,
                          PlantService plantService) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.plantService = plantService;
    }

    /**
     * Fetch current weather for a city
     */
    public WeatherDTO getWeatherForCity(String city) {
        try {
            if (apiKey == null || apiKey.isEmpty()) {
                return getDefaultWeather(city);
            }

            String response = webClient.get()
                    .uri(apiUrl + "?q={city}&appid={apiKey}&units=metric", city, apiKey)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return parseWeatherResponse(response, city);
        } catch (Exception e) {
            System.err.println("Weather API Error: " + e.getMessage());
            return getDefaultWeather(city);
        }
    }

    private WeatherDTO parseWeatherResponse(String response, String city) {
        try {
            Map<String, Object> payload = objectMapper.readValue(response, new TypeReference<>() {});
            WeatherDTO weather = new WeatherDTO();
            weather.setCity(city);

            if (payload.containsKey("main")) {
                Map<String, Object> main = (Map<String, Object>) payload.get("main");
                if (main.containsKey("temp")) {
                    weather.setTemperature(Double.parseDouble(main.get("temp").toString()));
                }
                if (main.containsKey("humidity")) {
                    weather.setHumidity(Double.parseDouble(main.get("humidity").toString()));
                }
                if (main.containsKey("pressure")) {
                    weather.setPressure(Integer.parseInt(main.get("pressure").toString()));
                }
            }

            if (payload.containsKey("weather")) {
                List<Map<String, Object>> weatherArray = (List<Map<String, Object>>) payload.get("weather");
                if (!weatherArray.isEmpty()) {
                    Map<String, Object> first = weatherArray.get(0);
                    weather.setWeatherMain(first.getOrDefault("main", "Unknown").toString());
                    weather.setWeatherDescription(first.getOrDefault("description", "").toString());
                }
            }

            weather.setIsRaining("Rain".equalsIgnoreCase(weather.getWeatherMain()));
            return weather;
        } catch (Exception e) {
            return getDefaultWeather(city);
        }
    }

    public Map<String, Object> getCurrentWeather(String city) {
        WeatherDTO weather = getWeatherForCity(city);
        Map<String, Object> response = new HashMap<>();
        response.put("city", weather.getCity());
        response.put("temperature", weather.getTemperature());
        response.put("humidity", weather.getHumidity());
        response.put("weatherMain", weather.getWeatherMain());
        response.put("weatherDescription", weather.getWeatherDescription());
        response.put("isRaining", weather.getIsRaining());
        response.put("careTip", buildWeatherCareTip(weather));
        return response;
    }

    public Map<String, Object> getWeatherRecommendations(Long userId, Long plantId) {
        User user = userService.getUserById(userId);
        PlantDTO plant = plantService.getPlant(userId, plantId);
        WeatherDTO weather = getWeatherForCity(user.getCity());

        Map<String, Object> recommendation = new HashMap<>();
        recommendation.put("plant", plant);
        recommendation.put("weather", weather);
        recommendation.put("shouldSkipWatering", shouldSkipWatering(weather));
        recommendation.put("nextAction", buildWeatherWateringAction(plant, weather));
        recommendation.put("careTip", buildWeatherCareTip(weather));
        recommendation.put("recommendationTitle", weather.getIsRaining() ? "Rainy Day Care" : "Weather Smart Care");
        return recommendation;
    }

    private String buildWeatherWateringAction(PlantDTO plant, WeatherDTO weather) {
        if (weather.getIsRaining()) {
            return "Skip watering today and let the rain hydrate your plant.";
        }

        if (weather.getTemperature() != null && weather.getTemperature() > 30) {
            return "Water more carefully: plants may need extra hydration when it is very hot.";
        }

        if (weather.getHumidity() != null && weather.getHumidity() > 70) {
            return "High humidity detected. Reduce watering frequency slightly.";
        }

        if (weather.getTemperature() != null && weather.getTemperature() < 10) {
            return "Cool conditions detected. Water sparingly to avoid soggy soil.";
        }

        return "The weather looks balanced today. Follow your plant's regular care schedule.";
    }

    private String buildWeatherCareTip(WeatherDTO weather) {
        if (weather.getIsRaining()) {
            return "Since it's raining, your plants can rest. Avoid extra watering and monitor humidity.";
        }
        if (weather.getTemperature() != null && weather.getTemperature() > 30) {
            return "It is warm outside. Keep soil moist and consider moving plants away from direct heat.";
        }
        if (weather.getHumidity() != null && weather.getHumidity() > 70) {
            return "Humidity is high. Reduce watering and ensure good airflow around your plants.";
        }
        if (weather.getTemperature() != null && weather.getTemperature() < 10) {
            return "Cold weather is here. Keep plants warm and avoid wet soil.";
        }
        return "Today's weather is favorable. Keep your regular plant care rhythm going.";
    }

    private WeatherDTO getDefaultWeather(String city) {
        WeatherDTO weather = new WeatherDTO();
        weather.setCity(city);
        weather.setTemperature(25.0);
        weather.setHumidity(60.0);
        weather.setIsRaining(false);
        weather.setWeatherMain("Unknown");
        weather.setWeatherDescription("Weather API not configured");
        return weather;
    }

    public boolean shouldSkipWatering(WeatherDTO weather) {
        if (weather.getIsRaining() != null && weather.getIsRaining()) {
            return true;
        }

        if (weather.getHumidity() != null && weather.getHumidity() > 80) {
            return true;
        }

        if (weather.getTemperature() != null && weather.getTemperature() < 0) {
            return true;
        }

        return false;
    }

    public Double getWateringIntervalMultiplier(WeatherDTO weather) {
        double multiplier = 1.0;

        if (weather.getTemperature() != null && weather.getTemperature() > 35) {
            multiplier *= 0.7;
        } else if (weather.getTemperature() != null && weather.getTemperature() > 28) {
            multiplier *= 0.85;
        } else if (weather.getTemperature() != null && weather.getTemperature() < 10) {
            multiplier *= 1.5;
        }

        if (weather.getHumidity() != null && weather.getHumidity() > 70) {
            multiplier *= 1.2;
        }

        return multiplier;
    }
}
