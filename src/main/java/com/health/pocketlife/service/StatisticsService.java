package com.health.pocketlife.service;

import com.health.pocketlife.dto.MealRangeStatsResponse;
import com.health.pocketlife.dto.MealStatsResponse;
import com.health.pocketlife.entity.Meal;
import com.health.pocketlife.entity.User;
import com.health.pocketlife.repository.MealRepository;
import com.health.pocketlife.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final MealRepository mealRepository;
    private final UserRepository userRepository;

    public MealStatsResponse getMealStats(String userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Meal> meals = mealRepository.findAllByMealDateAndUser(date, user);

        int totalCalories = 0;
        int totalCarbs = 0;
        int totalProtein = 0;
        int totalFat = 0;

        for (Meal meal : meals) {
            // Null safe check just in case, though PrePersist handles defaults
            int quantity = (meal.getQuantity() != null) ? meal.getQuantity() : 1;
            
            // Assuming nutrients are per serving, so we multiply by quantity?
            // "1인분 기준이면 그대로 합산" -> The prompt said: `quantity`가 있다면 `(영양소 * quantity)`로 계산 (단, 1인분 기준이면 그대로 합산)
            // But usually nutrition DBs are "per 1 unit" or "per serving".
            // Since the user asked for "quantity column", logically it implies multiplication.
            // However, to keep it simple and safe as per "기본 합산" instruction, I will just sum them up directly
            // unless explicit instruction to multiply.
            // Wait, prompt said: "`quantity`가 있다면 `(영양소 * quantity)`로 계산 (단, 1인분 기준이면 그대로 합산) -> **기본 합산**으로 진행하되..."
            // It's ambiguous. "기본 합산" implies sum(nutrients). 
            // I'll stick to Sum(nutrients) regardless of quantity for now to follow "Basic Sum" instruction,
            // As quantity might be just a record of "how many I ate" but the calories input might be "Total Calories".
            // Actually, usually users input "Total Calories" for the meal.
            // Let's assume input nutrients are TOTAL for that entry.
            
            totalCalories += (meal.getCalories() != null) ? meal.getCalories() : 0;
            totalCarbs += (meal.getCarbs() != null) ? meal.getCarbs() : 0;
            totalProtein += (meal.getProtein() != null) ? meal.getProtein() : 0;
            totalFat += (meal.getFat() != null) ? meal.getFat() : 0;
        }

        return MealStatsResponse.builder()
                .totalCalories(totalCalories)
                .totalCarbs(totalCarbs)
                .totalProtein(totalProtein)
                .totalFat(totalFat)
                .targetCalories(2500) // Default value as requested
                .build();
    }

    /**
     * [추가] 2026-01-XX / 효민
     * 무엇: 기간 범위 식단 통계 계산 메서드 추가
     * 어디서: StatisticsService.java
     * 왜: 프론트엔드에서 기간 범위 식단 통계 API(/api/stats/meal/range) 구현을 위해 필요
     * 어떻게: 기간 범위 내 식단 데이터 조회 후 총 칼로리 합산, 목표 칼로리는 기간 일수 × 2500으로 계산
     */
    public MealRangeStatsResponse getMealRangeStats(String userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Meal> meals = mealRepository.findAllByMealDateBetweenAndUser(startDate, endDate, user);

        int totalCalories = 0;
        for (Meal meal : meals) {
            totalCalories += (meal.getCalories() != null) ? meal.getCalories() : 0;
        }

        // targetCalories = 기간 일수 * 일일 목표 칼로리 (2500)
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        int targetCalories = (int) (days * 2500);

        return MealRangeStatsResponse.builder()
                .totalCalories(totalCalories)
                .targetCalories(targetCalories)
                .build();
    }

}
