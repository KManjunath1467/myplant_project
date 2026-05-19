package com.myplant.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AIService {

    public Map<String, Object> getWateringPrediction(
            double temperature,
            double humidity,
            int plantType
    ) {

        try {

            String url =
                    "http://127.0.0.1:5000/predict"
                    + "?temperature=" + temperature
                    + "&humidity=" + humidity
                    + "&plantType=" + plantType;

            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> response =
                    restTemplate.getForObject(url, Map.class);

            return response;

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Failed to connect to AI service"
            );
        }
    }
}