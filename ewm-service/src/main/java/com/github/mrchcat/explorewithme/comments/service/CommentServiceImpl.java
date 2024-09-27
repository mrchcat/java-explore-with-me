package com.github.mrchcat.explorewithme.comments.service;

import com.github.mrchcat.explorewithme.comments.dto.CommentAdminSearchDto;
import com.github.mrchcat.explorewithme.comments.dto.CommentAdminUpdateDto;
import com.github.mrchcat.explorewithme.comments.dto.CommentPrivateCreateDto;
import com.github.mrchcat.explorewithme.comments.dto.CommentDto;
import com.github.mrchcat.explorewithme.comments.mapper.CommentMapper;
import com.github.mrchcat.explorewithme.comments.model.Comment;
import com.github.mrchcat.explorewithme.comments.model.CommentState;
import com.github.mrchcat.explorewithme.comments.repository.CommentRepository;
import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.event.repository.EventRepository;
import com.github.mrchcat.explorewithme.exception.NotFoundException;
import com.github.mrchcat.explorewithme.exception.RulesViolationException;
import com.github.mrchcat.explorewithme.user.model.User;
import com.github.mrchcat.explorewithme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.github.mrchcat.explorewithme.comments.model.CommentState.DEAD;
import static com.github.mrchcat.explorewithme.event.model.EventState.PUBLISHED;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentDto create(long userId, long eventId, CommentPrivateCreateDto createDto) {
        Event event = getPublishedEvent(eventId);
        User author = getUser(userId);
        canUserCommentThisEvent(event, author);
        Comment comment = Comment.builder()
                .event(event)
                .author(author)
                .text(createDto.getText())
                .build();
        Comment savedComment = commentRepository.save(comment);
        log.info("{} was saved", savedComment);
        return CommentMapper.toDto(savedComment);
    }

    @Override
    @Transactional
    public void delete(long userId, long commentId) {
        Comment comment = getCommentForPublishedEvent(commentId);
        canUserEditComment(comment, userId);
        comment.setState(DEAD);
        comment.setEditable(false);
        commentRepository.save(comment);
        log.info("Comment {} was deleted", comment);
    }

    @Override
    public CommentDto updateByUser(long userId, long commentId, CommentPrivateCreateDto updateDto) {
        Comment comment = getCommentForPublishedEvent(commentId);
        canUserEditComment(comment, userId);
        comment.setText(updateDto.getText());
        comment.setModified(true);
        comment.setLastModification(LocalDateTime.now());
        Comment updatedComment = commentRepository.save(comment);
        log.info("Comment {} was updated by user", updatedComment);
        return CommentMapper.toDto(updatedComment);
    }

    @Override
    public List<CommentDto> getAllForAdmin(CommentAdminSearchDto query) {
        var comments = commentRepository.getAllCommentsByQuery(query);
        return CommentMapper.toDto(comments);
    }

    @Override
    public CommentDto updateByAdmin(long commentId, CommentAdminUpdateDto updateDto) {
        Comment comment = getComment(commentId);
        CommentState newState = updateDto.getState();
        if (newState != null) {
            comment.setState(newState);
            if (newState.equals(DEAD)) {
                comment.setEditable(false);
            }
        } else {
            Boolean isEditable = updateDto.getEditable();
            if (isEditable != null) {
                comment.setEditable(isEditable);
            }
        }
        Comment updatedComment = commentRepository.save(comment);
        log.info("Comment {} was updated by admin", updatedComment);
        return CommentMapper.toDto(updatedComment);
    }

    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            String message = "User with id=" + userId + " was not found";
            return new NotFoundException(message);
        });
    }

    private Event getPublishedEvent(long eventId) {
        return eventRepository.getByIdAndStatus(eventId, PUBLISHED).orElseThrow(() -> {
            String message = "Published event with id=" + eventId + " was not found";
            return new NotFoundException(message);
        });
    }

    private Comment getCommentForPublishedEvent(long commentId) {
        return commentRepository.getByIdAndEventState(commentId, PUBLISHED).orElseThrow(() -> {
            String message = "Comment with id=" + commentId + "for published event was not found";
            return new NotFoundException(message);
        });
    }

    private Comment getComment(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> {
            String message = "Comment with id=" + commentId + "was not found";
            return new NotFoundException(message);
        });
    }


    private void canUserCommentThisEvent(Event event, User author) {
        if (event.getInitiator().getId() == author.getId()) {
            String message = "User can not comment it's own event";
            throw new RulesViolationException(message);
        }
        if (commentRepository.existByEventAndAuthor(event.getId(), author.getId())) {
            String message = "User can not comment the same event twice";
            throw new RulesViolationException(message);
        }
    }

    private void canUserEditComment(Comment comment, long userId) {
        boolean isCommentAlive = comment.getState().equals(CommentState.ALIVE);
        boolean isCommentEditable = comment.isEditable();
        boolean doesCommentBelongToUser = comment.getAuthor().getId() == userId;
        if (!isCommentAlive || !isCommentEditable || !doesCommentBelongToUser) {
            String message = "Comment id=" + comment.getId() + " can not be changed due to rule violation";
            throw new RulesViolationException(message);
        }
    }
}
