package com.myplant.controller;

import com.myplant.service.AIService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin("*")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/watering-prediction")
    public Map<String, Object> predictWatering(
            @RequestParam double temperature,
            @RequestParam double humidity,
            @RequestParam int plantType
    ) {

        return aiService.getWateringPrediction(
                temperature,
                humidity,
                plantType
        );
    }
}