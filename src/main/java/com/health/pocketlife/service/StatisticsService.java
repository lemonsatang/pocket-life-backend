package com.health.pocketlife.service;

import com.health.pocketlife.dto.CartStatsResponse;
import com.health.pocketlife.dto.MealStatsResponse;
import com.health.pocketlife.entity.Cart;
import com.health.pocketlife.entity.Meal;
import com.health.pocketlife.entity.User;
import com.health.pocketlife.repository.CartRepository;
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
    private final CartRepository cartRepository;
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

    public CartStatsResponse getCartStats(String userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Cart> carts = cartRepository.findAllByShoppingDateAndUser(date, user);

        int totalQuantity = 0;
        int purchasedQuantity = 0;

        for (Cart cart : carts) {
            int count = (cart.getCount() != null) ? cart.getCount() : 1;
            totalQuantity += count;
            if (Boolean.TRUE.equals(cart.getIsBought())) {
                purchasedQuantity += count;
            }
        }

        double purchaseRate = 0.0;
        if (totalQuantity > 0) {
            purchaseRate = (double) purchasedQuantity / totalQuantity * 100.0;
        }

        return CartStatsResponse.builder()
                .totalQuantity(totalQuantity)
                .purchasedQuantity(purchasedQuantity)
                .purchaseRate(purchaseRate)
                .build();
    }
}
