package com.myplant.controller;

import com.myplant.dto.PlantDTO;
import com.myplant.security.JwtTokenProvider;
import com.myplant.service.PlantService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Plant Controller
 * 
 * Handles plant management (CRUD operations)
 * 
 * Endpoints:
 * - GET /api/plants - get all plants for current user
 * - GET /api/plants/{id} - get specific plant
 * - POST /api/plants - create new plant
 * - PUT /api/plants/{id} - update plant
 * - DELETE /api/plants/{id} - delete plant
 * - POST /api/plants/{id}/water - mark as watered
 */
@RestController
@RequestMapping("/api/plants")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class PlantController {

    private final PlantService plantService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Get all plants for current user
     * 
     * @param authorization Bearer token
     * @return list of plants
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PlantDTO>> getUserPlants(@RequestHeader("Authorization") String authorization) {
        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        
        List<PlantDTO> plants = plantService.getUserPlants(userId);
        return ResponseEntity.ok(plants);
    }

    /**
     * Get specific plant by ID
     * 
     * @param authorization Bearer token
     * @param id plant ID
     * @return plant details
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PlantDTO> getPlant(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id) {
        
        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        
        PlantDTO plant = plantService.getPlant(userId, id);
        return ResponseEntity.ok(plant);
    }

    /**
     * Create new plant
     * 
     * Request body:
     * {
     *   "name": "My Snake Plant",
     *   "plantType": "Snake Plant",
     *   "potSize": "Medium (6 inches)",
     *   "isIndoor": true,
     *   "location": "Living Room",
     *   "notes": "Placed near window",
     *   "customWateringInterval": null
     * }
     * 
     * @param authorization Bearer token
     * @param plantDTO plant details
     * @return created plant with ID
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PlantDTO> createPlant(
            @RequestHeader("Authorization") String authorization,
            @RequestBody PlantDTO plantDTO) {
        
        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        
        PlantDTO created = plantService.createPlant(userId, plantDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Update plant
     * 
     * @param authorization Bearer token
     * @param id plant ID
     * @param plantDTO updated plant data
     * @return updated plant
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PlantDTO> updatePlant(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id,
            @RequestBody PlantDTO plantDTO) {
        
        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        
        PlantDTO updated = plantService.updatePlant(userId, id, plantDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete plant
     * 
     * @param authorization Bearer token
     * @param id plant ID
     * @return success message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deletePlant(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id) {
        
        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        
        plantService.deletePlant(userId, id);
        return ResponseEntity.ok("{\"message\": \"Plant deleted successfully\"}");
    }

    /**
     * Mark plant as watered today
     * 
     * @param authorization Bearer token
     * @param id plant ID
     * @return updated plant
     */
    @PostMapping("/{id}/water")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PlantDTO> markAsWatered(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id) {
        
        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        
        PlantDTO updated = plantService.markAsWatered(userId, id);
        return ResponseEntity.ok(updated);
    }

    /**
     * Update plant health status
     * 
     * Request body:
     * {
     *   "health": "Healthy" or "Fair" or "Poor"
     * }
     * 
     * @param authorization Bearer token
     * @param id plant ID
     * @param healthUpdate health status
     * @return updated plant
     */
    @PutMapping("/{id}/health")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PlantDTO> updateHealth(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id,
            @RequestBody PlantHealthDTO healthUpdate) {
        
        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        
        PlantDTO updated = plantService.updatePlantHealth(userId, id, healthUpdate.getHealth());
        return ResponseEntity.ok(updated);
    }

    // Inner class for request body
    public static class PlantHealthDTO {
        private String health;

        public String getHealth() { return health; }
        public void setHealth(String health) { this.health = health; }
    }
}
