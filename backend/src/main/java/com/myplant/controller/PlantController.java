package com.myplant.controller;

import com.myplant.dto.PlantDTO;
import com.myplant.entity.Plant;
import com.myplant.entity.User;
import com.myplant.repository.PlantRepository;
import com.myplant.security.JwtTokenProvider;
import com.myplant.service.PlantService;
import com.myplant.service.UserService;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.*;
import java.io.IOException;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
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
    private final UserService userService;

    @Autowired
private PlantRepository plantRepository;

    /**
     * Get all plants for current user
     */
    @PostMapping("/{id}/upload-image")
public ResponseEntity<?> uploadImage(
        @PathVariable Long id,
        @RequestParam("image") MultipartFile file
) {

    try {

        Plant plant = plantRepository.findById(id).orElseThrow();

        String uploadDir = "uploads/";

        String fileName =
                System.currentTimeMillis()
                + "_"
                + file.getOriginalFilename();

        Path filePath = Paths.get(uploadDir, fileName);

        Files.createDirectories(filePath.getParent());

        Files.write(filePath, file.getBytes());

        plant.setImageUrl(
                "http://localhost:8081/uploads/" + fileName
        );

        plantRepository.save(plant);

        return ResponseEntity.ok(plant);

    } catch (IOException e) {

        return ResponseEntity.internalServerError()
                .body("Upload failed");
    }
}
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PlantDTO>> getUserPlants(
            @RequestHeader("Authorization") String authorization) {

        String token =
                jwtTokenProvider.extractTokenFromBearerToken(authorization);

        Long userId =
                jwtTokenProvider.getUserIdFromToken(token);

        List<PlantDTO> plants =
                plantService.getUserPlants(userId);

        return ResponseEntity.ok(plants);
    }

    /**
     * Get specific plant by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PlantDTO> getPlant(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id) {

        String token =
                jwtTokenProvider.extractTokenFromBearerToken(authorization);

        Long userId =
                jwtTokenProvider.getUserIdFromToken(token);

        PlantDTO plant =
                plantService.getPlant(userId, id);

        return ResponseEntity.ok(plant);
    }

    /**
     * Create new plant
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PlantDTO> createPlant(
            @RequestHeader("Authorization") String authorization,
            @RequestBody PlantDTO plantDTO) {

        String token =
                jwtTokenProvider.extractTokenFromBearerToken(authorization);

        Long userId =
                jwtTokenProvider.getUserIdFromToken(token);

        User user =
                userService.getUserById(userId);

        PlantDTO createdPlant =
                plantService.createPlant(plantDTO, user);

        return new ResponseEntity<>(createdPlant, HttpStatus.CREATED);
    }

    /**
     * Update plant
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PlantDTO> updatePlant(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id,
            @RequestBody PlantDTO plantDTO) {

        String token =
                jwtTokenProvider.extractTokenFromBearerToken(authorization);

        Long userId =
                jwtTokenProvider.getUserIdFromToken(token);

        PlantDTO updated =
                plantService.updatePlant(userId, id, plantDTO);

        return ResponseEntity.ok(updated);
    }

    /**
     * Delete plant
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deletePlant(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id) {

        String token =
                jwtTokenProvider.extractTokenFromBearerToken(authorization);

        Long userId =
                jwtTokenProvider.getUserIdFromToken(token);

        plantService.deletePlant(userId, id);

        return ResponseEntity.ok("Plant deleted successfully");
    }

    /**
     * Mark plant as watered
     */
    @PostMapping("/{id}/water")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PlantDTO> markAsWatered(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id) {

        String token =
                jwtTokenProvider.extractTokenFromBearerToken(authorization);

        Long userId =
                jwtTokenProvider.getUserIdFromToken(token);

        PlantDTO updated =
                plantService.markAsWatered(userId, id);

        return ResponseEntity.ok(updated);
    }

    /**
     * Update plant health
     */
    @PutMapping("/{id}/health")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PlantDTO> updateHealth(
            @RequestHeader("Authorization") String authorization,
            @PathVariable Long id,
            @RequestBody PlantHealthDTO healthUpdate) {

        String token =
                jwtTokenProvider.extractTokenFromBearerToken(authorization);

        Long userId =
                jwtTokenProvider.getUserIdFromToken(token);

        PlantDTO updated =
                plantService.updatePlantHealth(
                        userId,
                        id,
                        healthUpdate.getHealth());

        return ResponseEntity.ok(updated);
    }

    /**
     * Inner DTO class
     */
    public static class PlantHealthDTO {

        private String health;

        public String getHealth() {
            return health;
        }

        public void setHealth(String health) {
            this.health = health;
        }
    }
}