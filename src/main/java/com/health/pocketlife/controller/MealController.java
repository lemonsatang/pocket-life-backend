package com.health.pocketlife.controller;

import com.health.pocketlife.entity.Meal;
import com.health.pocketlife.repository.MealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meals")
@CrossOrigin(origins = "*")
public class MealController {
    private final MealRepository mealRepository;

    @GetMapping
    // 리액트에서 보낸 날짜 파라미터로 조회
    public List<Meal> getList(@RequestParam LocalDate date) {
        return mealRepository.findAllByMealDate(date);
    }

    @PostMapping
    public Meal create(@RequestBody Meal meal) {
        return mealRepository.save(meal);
    }

    @PutMapping("/{id}")
    public Meal update(@PathVariable Long id, @RequestBody Meal mealDetails) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 기록을 찾을 수 없습니다."));

        meal.setText(mealDetails.getText());
        meal.setCalories(mealDetails.getCalories()); // 칼로리 수정 반영 유지
        return mealRepository.save(meal);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        mealRepository.deleteById(id);
    }
}