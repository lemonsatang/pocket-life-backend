package com.health.pocketlife.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * [추가] 2026-01-XX / 효민
 * 무엇: 카테고리별 지출 통계 응답 DTO 클래스 생성
 * 어디서: CategoryStatsResponse.java
 * 왜: 프론트엔드에서 카테고리별 지출 통계 API 응답 형식 정의 필요 (가계부 페이지에서 사용 가능)
 * 어떻게: 총 지출 금액(totalExpense)과 카테고리별 항목 리스트(categories)를 가진 DTO 생성, CategoryItem 내부 클래스로 카테고리명, 금액, 비율 포함
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatsResponse {
    private long totalExpense;
    private List<CategoryItem> categories;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryItem {
        private String category;
        private long amount;
        private int percentage; // 전체 지출 대비 비율 (소수점 버림)
    }
}
