package com.health.pocketlife.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter @Setter
@Table(name = "todo")
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TODOID")
    private Long todoId;

    @Column(name = "USRID")
    private String userId;

    @Column(name = "DODATE")
    private LocalDate doDate;

    @Column(name = "CONTENT")
    private String content;
}