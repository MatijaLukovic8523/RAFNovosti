package com.raf.novosti.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Long id;
    private String authorName;
    private String text;
    private LocalDateTime createdAt;

    private long likes;
    private long dislikes;
}