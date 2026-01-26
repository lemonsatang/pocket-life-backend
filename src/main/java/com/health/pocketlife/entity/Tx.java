package com.health.pocketlife.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity @Table(name = "tx")
@Getter @Setter @NoArgsConstructor
public class Tx {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 거래내역 번호(자동 증가)

    @Column(nullable = false)
    private String userId; // 로그인한 유저 구분값(토큰에서 꺼낼 예정)

    @Column(nullable = false)
    private LocalDate txDate; // 거래 날짜(예: 2026-01-13)

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private Type type; // 수입(INCOME) / 지출(EXPENSE)

    @Column(length = 7)
    private String category; // 카테고리(교통/식비 등)

    @Column(nullable = false, length = 10)
    private String title;// 항목(급여/커피 등)

    @Column(length = 10)
    private String memo;     // 메모(비고)

    @Column(nullable = false)
    private Long amount; // 금액(원) - 음수로 저장하지 말고 type으로 구분

    public enum Type { INCOME, EXPENSE } // 수입/지출 구분 값
}
