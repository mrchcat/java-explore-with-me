package com.github.mrchcat.explorewithme.comments.mapper;

import com.github.mrchcat.explorewithme.comments.dto.CommentShortDto;
import com.github.mrchcat.explorewithme.comments.model.Comment;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CommentMapper {

    public static CommentShortDto toShortDto(Comment comment) {
        return CommentShortDto.builder()
                .id(comment.getId())
                .state(comment.getState())
                .eventId(comment.getEvent().getId())
                .authorName(comment.getAuthor().getName())
                .editable(comment.isEditable())
                .text(comment.getText())
                .created(comment.getCreated())
                .modified(comment.isModified())
                .lastModification(comment.getLastModification())
                .likes(comment.getLikes())
                .dislikes(comment.getDislikes())
                .build();
    }
}
