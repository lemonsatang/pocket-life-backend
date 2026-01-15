package com.health.pocketlife.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartStatsResponse {
    private int totalQuantity;
    private int purchasedQuantity;
    private double purchaseRate; // Percentage (0.0 ~ 100.0)
}
