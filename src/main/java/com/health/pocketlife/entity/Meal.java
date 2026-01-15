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

    @Column(name = "protein")
    private Integer protein;

    @Column(name = "fat")
    private Integer fat;

    @Column(name = "carbs")
    private Integer carbs;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "meal_date")
    private LocalDate mealDate;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.calories == null) this.calories = 0;
        if (this.protein == null) this.protein = 0;
        if (this.fat == null) this.fat = 0;
        if (this.carbs == null) this.carbs = 0;
        if (this.quantity == null) this.quantity = 1;
    }
}