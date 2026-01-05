package com.health.pocketlife.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter @Setter
@Table(name = "tx")
public class Tx {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TXID")
    private Long txId;

    @Column(name = "USRID")
    private String userId;

    @Column(name = "TXDATE")
    private LocalDate txDate;

    @Column(name = "TXTYP")
    private String txType;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "AMOUNT")
    private Long amount;
}