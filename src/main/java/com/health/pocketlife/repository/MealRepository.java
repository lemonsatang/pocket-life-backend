package com.health.pocketlife.repository;

import com.health.pocketlife.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    // 날짜(mealDate)가 똑같은 데이터만 싹 다 찾아오는 마법의 메서드
    List<Meal> findAllByMealDate(LocalDate mealDate);
}