package com.github.mrchcat.explorewithme.comments.mapper;

import com.github.mrchcat.explorewithme.comments.dto.CommentDto;
import com.github.mrchcat.explorewithme.comments.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .state(comment.getState())
                .eventId(comment.getEvent().getId())
                .authorName(comment.getAuthor().getName())
                .editable(comment.isEditable())
                .text(comment.getText())
                .created(comment.getCreated())
                .modified(comment.isModified())
                .lastModification(comment.getLastModification())
                .build();
    }

    public static List<CommentDto> toDto(Iterable<Comment> comments) {
        List<CommentDto> dtos=new ArrayList<>();
        for(Comment cmt:comments){
            dtos.add(CommentMapper.toDto(cmt));
        }
        return dtos;
    }
}
