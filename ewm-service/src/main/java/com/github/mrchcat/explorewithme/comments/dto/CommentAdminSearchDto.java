package com.github.mrchcat.explorewithme.comments.dto;

import com.github.mrchcat.explorewithme.comments.model.CommentState;
import com.github.mrchcat.explorewithme.event.model.EventState;
import com.github.mrchcat.explorewithme.exception.ArgumentNotValidException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class CommentAdminSearchDto {
    List<Long> commentId;
    CommentState commentState;
    List<Long> eventId;
    List<Long> userId;
    Boolean editable;
    String text;
    LocalDateTime start;
    LocalDateTime end;
    Pageable pageable;

    public static void isCorrectDateOrder(LocalDateTime start, LocalDateTime finish) {
        if (start != null && finish != null && finish.isBefore(start)) {
            String message = "The dates violate order: " + start + " must be before " + finish;
            throw new ArgumentNotValidException(message);
        }
    }
}
