package com.myplant.service;

import com.myplant.dto.PlantCareRuleDTO;
import com.myplant.entity.PlantCareRule;
import com.myplant.repository.PlantCareRuleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * AI Analysis Service
 *
 * Simulates AI-powered plant image analysis.
 * In production, this would integrate with:
 * - Google Vision AI
 * - Plant.id API
 * - Custom ML models
 * - OpenAI Vision
 */
@Service
@AllArgsConstructor
public class AIAnalysisService {

    private final PlantCareRuleRepository plantCareRuleRepository;
    private final Random random = new Random();

    /**
     * Analyze uploaded plant image
     *
     * @param image uploaded image file
     * @param plantName optional hint from user
     * @param userId user ID for personalization
     * @return analysis results
     */
    public Map<String, Object> analyzePlantImage(MultipartFile image, String plantName, Long userId) {
        Map<String, Object> result = new HashMap<>();

        // Simulate AI processing time
        try {
            Thread.sleep(2000); // 2 second delay to simulate processing
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Get all available plant types
        List<PlantCareRule> allPlants = plantCareRuleRepository.findAll();

        // Simulate plant detection
        PlantCareRule detectedPlant;
        double confidence;

        if (plantName != null && !plantName.trim().isEmpty()) {
            // User provided hint - find matching plant
            detectedPlant = plantCareRuleRepository.findByPlantName(plantName.trim())
                    .orElse(allPlants.get(random.nextInt(allPlants.size())));
            confidence = 0.85 + random.nextDouble() * 0.1; // 85-95% confidence
        } else {
            // Random plant detection
            detectedPlant = allPlants.get(random.nextInt(allPlants.size()));
            confidence = 0.70 + random.nextDouble() * 0.25; // 70-95% confidence
        }

        // Simulate health analysis
        String healthStatus = analyzePlantHealth();
        Map<String, Object> healthAnalysis = new HashMap<>();
        healthAnalysis.put("status", healthStatus);
        healthAnalysis.put("confidence", 0.75 + random.nextDouble() * 0.2);
        healthAnalysis.put("issues", getHealthIssues(healthStatus));

        // Simulate care recommendations
        Map<String, Object> careRecommendations = new HashMap<>();
        careRecommendations.put("wateringSchedule", detectedPlant.getWateringFrequency());
        careRecommendations.put("sunlightNeeds", detectedPlant.getSunlightNeeds());
        careRecommendations.put("humidityPreference", detectedPlant.getHumidityPreference());
        careRecommendations.put("temperatureRange", detectedPlant.getTemperatureRange());
        careRecommendations.put("soilType", "Well-draining potting mix");
        careRecommendations.put("fertilizerSchedule", "Every 4-6 weeks during growing season");
        careRecommendations.put("toxicity", simulateToxicityCheck(detectedPlant.getPlantName()));

        // Build result
        result.put("detectedPlant", convertToDTO(detectedPlant));
        result.put("confidence", Math.round(confidence * 100.0) / 100.0);
        result.put("healthAnalysis", healthAnalysis);
        result.put("careRecommendations", careRecommendations);
        result.put("tips", detectedPlant.getCareTips());

        return result;
    }

    /**
     * Simulate plant health analysis
     */
    private String analyzePlantHealth() {
        double rand = random.nextDouble();
        if (rand < 0.7) return "Healthy";
        if (rand < 0.9) return "Fair";
        return "Poor";
    }

    /**
     * Get health issues based on status
     */
    private List<String> getHealthIssues(String status) {
        return switch (status) {
            case "Healthy" -> List.of();
            case "Fair" -> List.of("Minor leaf discoloration", "Slight wilting");
            case "Poor" -> List.of("Severe leaf yellowing", "Root rot suspected", "Pest infestation");
            default -> List.of();
        };
    }

    /**
     * Simulate toxicity check for pets
     */
    private String simulateToxicityCheck(String plantName) {
        // Some plants are toxic, others are safe
        List<String> toxicPlants = List.of("Peace Lily", "Pothos", "ZZ Plant", "Snake Plant");
        return toxicPlants.contains(plantName) ? "Toxic to cats and dogs" : "Safe for pets";
    }

    /**
     * Convert PlantCareRule to DTO
     */
    private PlantCareRuleDTO convertToDTO(PlantCareRule rule) {
        PlantCareRuleDTO dto = new PlantCareRuleDTO();
        dto.setId(rule.getId());
        dto.setPlantName(rule.getPlantName());
        dto.setBaseWateringDays(rule.getBaseWateringDays());
        dto.setWateringFrequency(rule.getWateringFrequency());
        dto.setSunlightNeeds(rule.getSunlightNeeds());
        dto.setHumidityPreference(rule.getHumidityPreference());
        dto.setTemperatureRange(rule.getTemperatureRange());
        dto.setDifficultyLevel(rule.getDifficultyLevel());
        dto.setDescription(rule.getDescription());
        dto.setCommonIssues(rule.getCommonIssues());
        dto.setCareTips(rule.getCareTips());
        dto.setRecommendedPotSize(rule.getRecommendedPotSize());
        dto.setGrowthRate(rule.getGrowthRate());
        dto.setMaxSize(rule.getMaxSize());
        return dto;
    }
}