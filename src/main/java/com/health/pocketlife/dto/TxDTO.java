package com.health.pocketlife.dto;

import java.time.LocalDate;

public class TxDTO {
    // 화면에서 보내는 “거래 1건” 입력값(요청 바디)
    public record TxRequest(LocalDate txDate, String title, String category, String memo, long amount, String type) {}

    // 화면/포스트맨에 돌려줄 “거래 1건” 결과값
    public record TxResponse(Long id, LocalDate txDate, String title, String category, String memo, long amount, String type) {}

    // 월 요약(수입/지출/합계)
    public record TxSummary(long income, long expense, long total) {}
}
