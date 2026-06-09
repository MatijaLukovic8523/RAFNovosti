package com.raf.novosti.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long targetId;

    @Column(nullable = false)
    private String targetType;

    @Column(nullable = false)
    private String reactionType;

    @Column(nullable = false)
    private String sessionId;
}