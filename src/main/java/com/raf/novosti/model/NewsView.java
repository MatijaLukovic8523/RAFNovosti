package com.raf.novosti.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "news_views")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    @Column(nullable = false)
    private String sessionId;
}