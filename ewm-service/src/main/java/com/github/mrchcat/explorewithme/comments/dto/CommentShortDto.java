package com.github.mrchcat.explorewithme.comments.dto;

import com.github.mrchcat.explorewithme.comments.model.CommentState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CommentShortDto {
    private long id;
    private CommentState state;
    private long eventId;
    private String authorName;
    private boolean editable;
    private String text;
    private LocalDateTime created;
    private boolean modified;
    private LocalDateTime lastModification;
    private long likes;
    private long dislikes;
}
