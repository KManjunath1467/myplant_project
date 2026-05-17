package com.myplant.controller;

import com.myplant.dto.PlantCareRuleDTO;
import com.myplant.service.PlantCareRuleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Plant Care Rule Controller
 * 
 * Provides access to plant knowledge database
 * Shows optimal care information for different plant types
 * 
 * Endpoints:
 * - GET /api/plant-care-rules - get all plant types
 * - GET /api/plant-care-rules/{plantName} - get care info for plant type
 * 
 * These endpoints are PUBLIC (no authentication needed)
 */
@RestController
@RequestMapping("/api/plant-care-rules")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class PlantCareRuleController {

    private final PlantCareRuleService plantCareRuleService;

    /**
     * Get all available plant types
     * 
     * Frontend displays this list when user adds a new plant
     * 
     * Example response:
     * [
     *   {
     *     "id": 1,
     *     "plantName": "Snake Plant",
     *     "baseWateringDays": 10,
     *     "wateringFrequency": "Every 10 days",
     *     "sunlightNeeds": "Low to Medium",
     *     "difficultyLevel": "Beginner",
     *     ...
     *   },
     *   ...
     * ]
     * 
     * @return list of all plant care rules
     */
    @GetMapping
    public ResponseEntity<List<PlantCareRuleDTO>> getAllPlants() {
        List<PlantCareRuleDTO> plants = plantCareRuleService.getAllPlants();
        return ResponseEntity.ok(plants);
    }

    /**
     * Get care information for a specific plant type
     * 
     * Example: GET /api/plant-care-rules/Snake%20Plant
     * 
     * @param plantName the plant type name
     * @return plant care details
     */
    @GetMapping("/{plantName}")
    public ResponseEntity<PlantCareRuleDTO> getPlantCareRule(@PathVariable String plantName) {
        PlantCareRuleDTO careRule = plantCareRuleService.getPlantCareRule(plantName);
        return ResponseEntity.ok(careRule);
    }
}
