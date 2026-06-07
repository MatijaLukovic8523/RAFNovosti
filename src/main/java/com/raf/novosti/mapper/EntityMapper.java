package com.raf.novosti.mapper;

import com.raf.novosti.dto.*;
import com.raf.novosti.model.*;
import java.util.stream.Collectors;

public class EntityMapper {

    public static NewsDTO toNewsDTO(News news) {
        NewsDTO dto = new NewsDTO();
        dto.setId(news.getId());
        dto.setTitle(news.getTitle());
        dto.setText(news.getText());
        dto.setPublishedAt(news.getPublishedAt());
        dto.setVisits(news.getVisits());

        dto.setAuthorName(news.getAuthor().getFirstName() + " " + news.getAuthor().getLastName());

        CategoryDTO catDto = new CategoryDTO();
        catDto.setId(news.getCategory().getId());
        catDto.setName(news.getCategory().getName());
        catDto.setDescription(news.getCategory().getDescription());
        dto.setCategory(catDto);

        dto.setComments(news.getComments().stream()
                .map(EntityMapper::toCommentDTO)
                .collect(Collectors.toList()));

        dto.setTags(news.getTags().stream()
                .map(EntityMapper::toTagDTO)
                .collect(Collectors.toSet()));

        return dto;
    }

    public static CommentDTO toCommentDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setAuthorName(comment.getAuthorName());
        dto.setText(comment.getText());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }

    public static TagDTO toTagDTO(Tag tag) {
        TagDTO dto = new TagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        return dto;
    }

    public static UserDTO toUserDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        return dto;
    }
}