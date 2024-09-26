package com.github.mrchcat.explorewithme.comments.service;

import com.github.mrchcat.explorewithme.comments.dto.CommentCreateDto;
import com.github.mrchcat.explorewithme.comments.dto.CommentShortDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

public interface CommentService {
    CommentShortDto create(long userId, long eventId, CommentCreateDto createDto);

//    Comment getById(long commentId);

    void delete(long userId, long commentId);

//    CommentShortDto update(long userId, long commentId, CommentPublicUpdateDto updateDto);
//
//    void rate(long userId, long commentId, CommentRating rating);

}
