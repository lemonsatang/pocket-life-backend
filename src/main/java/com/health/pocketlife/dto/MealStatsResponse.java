package com.health.pocketlife.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealStatsResponse {
    private int totalCalories;
    private int totalCarbs;
    private int totalProtein;
    private int totalFat;
    private int targetCalories;
}
