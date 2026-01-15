package com.health.pocketlife.controller;

import com.health.pocketlife.dto.CartStatsResponse;
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
 * - 로그인한 사용자의 식단(Meal) 및 장바구니(Cart) 데이터를 기반으로
 *   특정 날짜의 통계 정보를 제공하기 위해 생성되었습니다.
 *
 * [구성]
 * 1. GET /api/stats/meal
 *    - 사용자의 해당 날짜 식단 기록을 조회하여 칼로리 및 탄단지 영양소 합계를 반환합니다.
 *    - 권장 칼로리(targetCalories) 비교 값을 포함합니다.
 * 2. GET /api/stats/cart
 *    - 사용자의 해당 날짜 장바구니 목록을 조회하여 구매 완료율을 계산합니다.
 *    - 단순 품목 수가 아닌, 수량(item_count) 가중치를 적용하여 더 정확한 구매율을 제공합니다.
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

    @GetMapping("/cart")
    public ResponseEntity<CartStatsResponse> getCartStats(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("date") LocalDate date) {
            
        String userId = userDetails.getUsername();
        CartStatsResponse response = statisticsService.getCartStats(userId, date);
        return ResponseEntity.ok(response);
    }
}
