package com.github.mrchcat.explorewithme.comments.controller;

import com.github.mrchcat.explorewithme.comments.dto.CommentDto;
import com.github.mrchcat.explorewithme.comments.dto.CommentPrivateCreateDto;
import com.github.mrchcat.explorewithme.comments.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping("/{userId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    CommentDto createComment(@PathVariable(name = "userId") long userId,
                             @RequestParam(name = "event") long eventId,
                             @RequestBody @Valid CommentPrivateCreateDto createDto) {
        log.info("""
                Private API: received request from user id={} to create comment
                for event id={} with content {}
                """, userId, eventId, createDto);
        return commentService.create(userId, eventId, createDto);
    }

    @DeleteMapping("/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComment(@PathVariable(name = "userId") long userId,
                       @PathVariable(name = "commentId") long commentId) {
        log.info("Private API: received request from user id={} to delete comment id={}", userId, commentId);
        commentService.setDeadState(userId, commentId);
    }

    @PatchMapping("/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    CommentDto updateComment(@PathVariable(name = "userId") long userId,
                             @PathVariable(name = "commentId") long commentId,
                             @RequestBody @Valid CommentPrivateCreateDto updateDto) {
        log.info("Private API: received request from user id={} to update comment id={} by content {}",
                userId, commentId, updateDto);
        return commentService.updateByUser(userId, commentId, updateDto);
    }
}
