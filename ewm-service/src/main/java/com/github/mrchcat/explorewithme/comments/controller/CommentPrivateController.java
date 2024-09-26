package com.github.mrchcat.explorewithme.comments.controller;

import com.github.mrchcat.explorewithme.comments.dto.CommentCreateDto;
import com.github.mrchcat.explorewithme.comments.dto.CommentPublicUpdateDto;
import com.github.mrchcat.explorewithme.comments.dto.CommentShortDto;
import com.github.mrchcat.explorewithme.comments.model.CommentRating;
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
    CommentShortDto createComment(@PathVariable(name = "userId") long userId,
                                  @RequestParam(name = "event") long eventId,
                                  @RequestBody @Valid CommentCreateDto createDto) {
        log.info("""
                Private API: received request from user id={} to create comment 
                for event id={} with content {}
                """, userId, eventId, createDto);
//        TODO передавать через DTO
        return commentService.create(userId, eventId, createDto);
    }

    @DeleteMapping("/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteComment(@PathVariable(name = "userId") long userId,
                       @PathVariable(name = "commentId") long commentId) {
        log.info("Private API: received request from user id={} to delete comment id={}", userId, commentId);
        commentService.delete(userId, commentId);
    }

//    @PatchMapping("/{userId}/comments/{commentId}")
//    @ResponseStatus(HttpStatus.OK)
//    CommentShortDto updateEvent(@PathVariable(name = "userId") long userId,
//                                @PathVariable(name = "commentId") long commentId,
//                                @RequestBody @Valid CommentPublicUpdateDto updateDto) {
//        log.info("Private API: received request from user id={} to update {}", userId, updateDto);
//        return commentService.update(userId, commentId, updateDto);
//    }
//
//    @PatchMapping("/{userId}/comments/{commentId}/rating")
//    @ResponseStatus(HttpStatus.OK)
//    void likeEvent(@PathVariable(name = "userId") long userId,
//                   @PathVariable(name = "commentId") long commentId,
//                   @RequestParam(name = "rating") CommentRating rating) {
//        log.info("Private API: received request from user id={} to rate comment {} as {}", userId, commentId, rating);
//        commentService.rate(userId, commentId, rating);
//    }

}
