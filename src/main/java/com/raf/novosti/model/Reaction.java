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
    private Long targetId; // ID vesti ili komentara

    @Column(nullable = false)
    private String targetType; // "NEWS" ili "COMMENT"

    @Column(nullable = false)
    private String reactionType; // "LIKE" ili "DISLIKE"

    @Column(nullable = false)
    private String sessionId; // Identifikator sesije iz browser-a
}