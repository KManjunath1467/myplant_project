package com.myplant.controller;

import com.myplant.dto.WateringHistoryDTO;
import com.myplant.security.JwtTokenProvider;
import com.myplant.service.WateringHistoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Watering History Controller
 * 
 * Tracks when plants were watered
 * Provides analytics on watering patterns
 * 
 * Endpoints:
 * - POST /api/watering-history/{plantId} - record watering event
 * - GET /api/watering-history/plant/{plantId} - get plant's watering history
 * - GET /api/watering-history - get all user's watering history
 * - GET /api/watering-history/recent - get recent watering (last 7 days)
 */
@RestController
@RequestMapping("/api/watering-history")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class WateringHistoryController {

    private final WateringHistoryService wateringHistoryService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Record a watering event for a plant
     * 
     * Request body:
     * {
     *   "notes": "Watered generously due to hot weather"
     * }
     * 
     * @param authorization Bearer token
     * @param plantId the plant ID
     * @param request watering event details
     * @return watering history record
     */
    @PostMapping("/record/{plantId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WateringHistoryDTO> recordWatering(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long plantId,
            @RequestBody WateringEventDTO request) {
        
        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        
        WateringHistoryDTO history = wateringHistoryService.recordWatering(userId, plantId, request.getNotes());
        return new ResponseEntity<>(history, HttpStatus.CREATED);
    }

    /**
     * Get watering history for a specific plant
     * 
     * @param authorization Bearer token
     * @param plantId the plant ID
     * @return list of watering history
     */
    @GetMapping("/plant/{plantId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WateringHistoryDTO>> getPlantWateringHistory(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long plantId) {
        
        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        
        List<WateringHistoryDTO> history = wateringHistoryService.getPlantWateringHistory(userId, plantId);
        return ResponseEntity.ok(history);
    }

    /**
     * Get all watering history for user's plants
     * 
     * @param authorization Bearer token
     * @return list of all watering history
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WateringHistoryDTO>> getUserWateringHistory(
            @RequestHeader("Authorization") String authorization) {
        
        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        
        List<WateringHistoryDTO> history = wateringHistoryService.getUserWateringHistory(userId);
        return ResponseEntity.ok(history);
    }

    /**
     * Get recent watering activity (last 7 days)
     * Useful for dashboard display
     * 
     * @param authorization Bearer token
     * @return recent watering records
     */
    @GetMapping("/recent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<WateringHistoryDTO>> getRecentWatering(
            @RequestHeader("Authorization") String authorization) {
        
        String token = jwtTokenProvider.extractTokenFromBearerToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        
        List<WateringHistoryDTO> recent = wateringHistoryService.getRecentWatering(userId);
        return ResponseEntity.ok(recent);
    }

    // Inner class for request body
    public static class WateringEventDTO {
        private String notes;

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
}
