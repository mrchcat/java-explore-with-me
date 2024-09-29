package com.github.mrchcat.explorewithme.comments.controller;

import com.github.mrchcat.explorewithme.comments.dto.CommentDto;
import com.github.mrchcat.explorewithme.comments.service.CommentService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentPublicController {
    private final CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    List<CommentDto> getAllComments(@RequestParam(name = "event") long eventId,
                                    @RequestParam(name = "order", required = false, defaultValue = "DESC") Sort.Direction order,
                                    @RequestParam(name = "from", defaultValue = "0", required = false) Integer from,
                                    @RequestParam(name = "size", defaultValue = "10", required = false) @Positive Integer size) {

        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0,
                size,
                order, "lastModification");
        log.info("Public API: received request to search comments for event {} with parameters {}", eventId, pageable);
        return commentService.getAllForPublic(eventId, pageable);
    }
}
