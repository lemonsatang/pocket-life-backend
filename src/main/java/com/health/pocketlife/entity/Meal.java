package com.health.pocketlife.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "meal")
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 변수명을 menu_name에서 text로 변경하여 프론트엔드와 일치시킴 [cite: 1]
    @Column(name = "menu_name", length = 30, nullable = false)
    private String text;

    @Column(name = "meal_type", length = 20)
    private String mealType;

    @Column(name = "calories")
    private Integer calories;

    @Column(name = "meal_date")
    private LocalDate mealDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.calories == null) {
            this.calories = 0;
        }
    }
}