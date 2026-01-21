package com.health.pocketlife.controller;

import com.health.pocketlife.dto.MealRangeStatsResponse;
import com.health.pocketlife.dto.MealStatsResponse;
import com.health.pocketlife.jwt.CustomUserDetails;
import com.health.pocketlife.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 통계 API 컨트롤러 (StatisticsController)
 *
 * [의도]
 * - 로그인한 사용자의 식단(Meal) 데이터를 기반으로
 *   특정 날짜의 통계 정보를 제공하기 위해 생성되었습니다.
 *
 * [구성]
 * 1. GET /api/stats/meal
 *    - 사용자의 해당 날짜 식단 기록을 조회하여 칼로리 및 탄단지 영양소 합계를 반환합니다.
 *    - 권장 칼로리(targetCalories) 비교 값을 포함합니다.
 * 2. GET /api/stats/meal/range
 *    - 사용자의 기간 범위 식단 기록을 조회하여 총 칼로리 및 목표 칼로리를 반환합니다.
 */
@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/meal")
    public ResponseEntity<MealStatsResponse> getMealStats(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("date") LocalDate date) {
        
        String userId = userDetails.getUsername(); // getUsername returns usrid
        MealStatsResponse response = statisticsService.getMealStats(userId, date);
        return ResponseEntity.ok(response);
    }

    /**
     * [추가] 2026-01-XX / 효민
     * 무엇: 기간 범위 식단 통계 API 엔드포인트 추가
     * 어디서: StatisticsController.java
     * 왜: 프론트엔드에서 날짜별 API를 여러 번 호출하는 대신 기간 범위를 한 번에 처리하여 성능 개선
     * 어떻게: GET /api/stats/meal/range?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD 형식으로 호출, StatisticsService의 getMealRangeStats 호출
     */
    @GetMapping("/meal/range")
    public ResponseEntity<MealRangeStatsResponse> getMealRangeStats(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {
        
        String userId = userDetails.getUsername();
        MealRangeStatsResponse response = statisticsService.getMealRangeStats(userId, startDate, endDate);
        return ResponseEntity.ok(response);
    }
}
