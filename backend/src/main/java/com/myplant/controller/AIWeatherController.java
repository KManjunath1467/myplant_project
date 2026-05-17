package com.myplant.controller;

import com.myplant.security.JwtTokenProvider;
import com.myplant.service.AIAnalysisService;
import com.myplant.service.UserService;
import com.myplant.service.WeatherService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * AI and Weather Controller
 *
 * Handles AI-powered plant analysis and weather-based recommendations
 *
 * Endpoints:
 * - POST /api/ai/analyze-plant - analyze uploaded plant image
 * - GET /api/weather/recommendations/{plantId} - get weather-based care recommendations
 * - GET /api/weather/current - get current weather for user's city
 */
@RestController
@RequestMapping("/api")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AIWeatherController {

    private final AIAnalysisService aiAnalysisService;
    private final WeatherService weatherService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Analyze uploaded plant image using AI
     *
     * @param authorization Bearer token
     * @param image uploaded plant image
     * @param plantName optional plant name hint
     * @return AI analysis results
     */
    @PostMapping("/ai/analyze-plant")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> analyzePlantImage(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "plantName", required = false) String plantName) {

        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        Map<String, Object> analysis = aiAnalysisService.analyzePlantImage(image, plantName, userId);

        return ResponseEntity.ok(analysis);
    }

    /**
     * Get weather-based care recommendations for a plant
     *
     * @param authorization Bearer token
     * @param plantId plant ID
     * @return weather recommendations
     */
    @GetMapping("/weather/recommendations/{plantId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getWeatherRecommendations(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long plantId) {

        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        Map<String, Object> recommendations = weatherService.getWeatherRecommendations(userId, plantId);

        return ResponseEntity.ok(recommendations);
    }

    /**
     * Get weather data for a specific city
     *
     * @param city the city name
     * @return current weather data
     */
    @GetMapping("/weather/{city}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getWeatherByCity(
            @PathVariable String city) {

        Map<String, Object> weather = weatherService.getCurrentWeather(city);
        return ResponseEntity.ok(weather);
    }

    /**
     * Get current weather for the authenticated user's city
     *
     * @param authorization Bearer token
     * @return current weather data
     */
    @GetMapping("/weather/current")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCurrentWeather(
            @RequestHeader("Authorization") String authorization) {

        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        String city = userService.getUserById(userId).getCity();
        Map<String, Object> weather = weatherService.getCurrentWeather(city);

        return ResponseEntity.ok(weather);
    }
}
