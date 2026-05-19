package com.myplant.dto;

import lombok.Data;

@Data
public class AIRequestDTO {

    private Double temperature;
    private Double humidity;
    private Integer plantType;
}