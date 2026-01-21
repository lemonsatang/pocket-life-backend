package com.health.pocketlife.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [추가] 2026-01-XX / 효민
 * 무엇: 기간 범위 식단 통계 응답 DTO 클래스 생성
 * 어디서: MealRangeStatsResponse.java
 * 왜: 프론트엔드에서 기간 범위 식단 통계 API 응답 형식 정의 필요
 * 어떻게: 총 칼로리(totalCalories)와 목표 칼로리(targetCalories) 필드를 가진 DTO 생성
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealRangeStatsResponse {
    private int totalCalories;
    private int targetCalories;
}
