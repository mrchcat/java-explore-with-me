package com.github.mrchcat.explorewithme.comments.controller;

import com.github.mrchcat.explorewithme.comments.dto.CommentAdminSearchDto;
import com.github.mrchcat.explorewithme.comments.dto.CommentAdminUpdateDto;
import com.github.mrchcat.explorewithme.comments.dto.CommentDto;
import com.github.mrchcat.explorewithme.comments.model.CommentState;
import com.github.mrchcat.explorewithme.comments.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static com.github.mrchcat.explorewithme.comments.dto.CommentAdminSearchDto.isCorrectDateOrder;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class CommentAdminController {
    private final CommentService commentService;

    @GetMapping("/comments")
    @ResponseStatus(HttpStatus.OK)
    List<CommentDto> getAllComments(@RequestParam(name = "comment", required = false) List<Long> commentIds,
                                    @RequestParam(name = "commentState", required = false) CommentState commentState,
                                    @RequestParam(name = "event", required = false) List<Long> eventIds,
                                    @RequestParam(name = "user", required = false) List<Long> userIds,
                                    @RequestParam(name = "editable", required = false) Boolean editable,
                                    @RequestParam(name = "text", required = false) String text,
                                    @RequestParam(name = "start", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                    @RequestParam(name = "end", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                    @RequestParam(name = "order", required = false, defaultValue = "DESC") Sort.Direction order,
                                    @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                    @RequestParam(name = "size", defaultValue = "10", required = false) @Positive Integer size) {

        isCorrectDateOrder(start, end);
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0,
                size,
                order, "lastModification");
        var query = CommentAdminSearchDto.builder()
                .commentId(commentIds)
                .commentState(commentState)
                .eventId(eventIds)
                .userId(userIds)
                .editable(editable)
                .text(text)
                .start(start)
                .end(end)
                .pageable(pageable)
                .build();
        log.info("Admin API: received request to search comments by query {}", query);
        return commentService.getAllForAdmin(query);
    }

    @PatchMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    CommentDto updateComment(@PathVariable(name = "commentId") long commentId,
                             @RequestBody @Valid CommentAdminUpdateDto updateDto) {
        updateDto.notNullAll();
        log.info("Admin API: received request to update comment id={} with {}", commentService, updateDto);
        return commentService.updateByAdmin(commentId, updateDto);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComment(@PathVariable(name = "commentId") long commentId) {
        log.info("Admin API: received request to delete comment id={}", commentId);
        commentService.delete(commentId);
    }
}
