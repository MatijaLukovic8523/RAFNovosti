package com.raf.novosti.dto;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class NewsDTO {
    private Long id;
    private String title;
    private String text;
    private LocalDateTime publishedAt;
    private int visits;
    private String authorName;
    private CategoryDTO category;
    private List<CommentDTO> comments;
    private Set<TagDTO> tags;

    private long likes;
    private long dislikes;
}