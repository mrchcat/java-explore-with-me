package com.github.mrchcat.explorewithme.comments.repository;

import com.github.mrchcat.explorewithme.comments.dto.CommentAdminSearchDto;
import com.github.mrchcat.explorewithme.comments.model.Comment;
import com.github.mrchcat.explorewithme.event.dto.EventPublicSearchDto;
import com.github.mrchcat.explorewithme.event.model.Event;

import java.util.List;

public interface CommentCustomRepository {

    List<Comment> getAllCommentsByQuery(CommentAdminSearchDto query);
}
