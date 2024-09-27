package com.github.mrchcat.explorewithme.comments.service;

import com.github.mrchcat.explorewithme.comments.dto.CommentAdminSearchDto;
import com.github.mrchcat.explorewithme.comments.dto.CommentAdminUpdateDto;
import com.github.mrchcat.explorewithme.comments.dto.CommentDto;
import com.github.mrchcat.explorewithme.comments.dto.CommentPrivateCreateDto;

import java.util.List;

public interface CommentService {
    CommentDto create(long userId, long eventId, CommentPrivateCreateDto createDto);

    void setDeadState(long userId, long commentId);

    CommentDto updateByUser(long userId, long commentId, CommentPrivateCreateDto updateDto);

    List<CommentDto> getAllForAdmin(CommentAdminSearchDto query);

    CommentDto updateByAdmin(long commentId, CommentAdminUpdateDto updateDto);

    void delete(long commentId);

}