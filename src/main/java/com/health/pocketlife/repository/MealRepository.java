package com.health.pocketlife.repository;

import com.health.pocketlife.entity.Meal;
import com.health.pocketlife.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    // 날짜(mealDate)가 똑같은 데이터만 싹 다 찾아오는 마법의 메서드
    List<Meal> findAllByMealDate(LocalDate mealDate);

    // 날짜와 유저로 조회
    List<Meal> findAllByMealDateAndUser(LocalDate mealDate, User user);

    /**
     * [추가] 2026-01-XX / 효민
     * 무엇: 기간 범위 식단 조회 메서드 추가
     * 어디서: MealRepository.java
     * 왜: 프론트엔드에서 기간 범위 통계 API 구현을 위해 기간 범위 조회 기능 필요
     * 어떻게: JPA 메서드 네이밍 규칙으로 findAllByMealDateBetweenAndUser 생성
     */
    List<Meal> findAllByMealDateBetweenAndUser(LocalDate startDate, LocalDate endDate, User user);
}