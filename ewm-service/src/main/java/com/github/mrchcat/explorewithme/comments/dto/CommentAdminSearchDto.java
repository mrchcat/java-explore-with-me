package com.github.mrchcat.explorewithme.comments.dto;

import com.github.mrchcat.explorewithme.event.model.EventState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentAdminSearchDto {
    List<Long> commentId;
    List<Long> eventId;
    EventState eventState;
    List<Long> userId;
    Boolean editable;
    String text;
    LocalDateTime start;
    LocalDateTime end;
    Pageable pageable;
}
