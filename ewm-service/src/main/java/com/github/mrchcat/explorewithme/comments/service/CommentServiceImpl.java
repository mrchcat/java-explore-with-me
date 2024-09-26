package com.github.mrchcat.explorewithme.comments.service;

import com.github.mrchcat.explorewithme.comments.dto.CommentCreateDto;
import com.github.mrchcat.explorewithme.comments.dto.CommentShortDto;
import com.github.mrchcat.explorewithme.comments.mapper.CommentMapper;
import com.github.mrchcat.explorewithme.comments.model.Comment;
import com.github.mrchcat.explorewithme.comments.repository.CommentRepository;
import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.event.model.EventState;
import com.github.mrchcat.explorewithme.event.repository.EventRepository;
import com.github.mrchcat.explorewithme.exception.NotFoundException;
import com.github.mrchcat.explorewithme.exception.RulesViolationException;
import com.github.mrchcat.explorewithme.user.model.User;
import com.github.mrchcat.explorewithme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentShortDto create(long userId, long eventId, CommentCreateDto createDto) {
        Event event = getEventById(eventId);
        hasEventCorrectStatus(event);
        User author = getUserById(userId);
        canUserCommentThisEvent(event, author);
        Comment comment = Comment.builder()
                .event(event)
                .author(author)
                .text(createDto.getText())
                .build();
        Comment savedComment = commentRepository.save(comment);
        log.info("{} was saved", savedComment);
        return CommentMapper.toShortDto(savedComment);
    }

    @Override
    @Transactional
    public void delete(long userId, long commentId) {
        int isDeleted = commentRepository.deleteByIdAndUserAndState(commentId, userId, EventState.PUBLISHED);
        if (isDeleted == 0) {
            String message = "Comment id=" + commentId + " was not deleted";
            log.info(message);
            throw new NotFoundException(message);
        }
        log.info("Comment {} was deleted.", commentId);
    }

//    @Override
//    public CommentShortDto update(long userId, long commentId, CommentUpdatePublicDto updateDto) {
//        Comment comment = getById(commentId);
//        checkIsUserCommented(userId, comment);
//        comment.setText(updateDto.getText());
//        comment.setLastModified(LocalDateTime.now());
//        commentRepository.save(comment);
//        log.info("{} was updated", comment);
//        return CommentMapper.toShortDto(comment);
//    }
//
//    @Override
//    public void rate(long userId, long commentId, CommentRating rating) {
//        Comment comment = getById(commentId);
//        checkIsUserNotCommented(userId, comment);
//        comment.updateRating(rating);
//        commentRepository.save(comment);
//    }
//
//    public Comment getById(long commentId) {
//        return commentRepository.findById(commentId).orElseThrow(() -> {
//            String message = String.format("Comment with id=%d  was not found", commentId);
//            return new NotFoundException(message);
//        });
//    }
//
//    private void checkIsAlive(Comment comment) {
//        if (!comment.getState().equals(CommentState.ALIVE)) {
//            throw new NotFoundException("Parent comment was deleted");
//        }
//    }
//
//    private void checkIsUserCommented(long userId, Comment comment) {
//        if (comment.getAuthor().getId() != userId) {
//            String message = String.format("User id=%d did not write comment %s", userId, comment);
//            throw new NotFoundException(message);
//        }
//    }
//
//    private void checkIsUserNotCommented(long userId, Comment comment) {
//        if (comment.getAuthor().getId() == userId) {
//            String message = "User cannot rate own comment";
//            throw new NotFoundException(message);
//        }
//    }

    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            String message = "User with id=" + userId + " was not found";
            return new NotFoundException(message);
        });
    }

    private Event getEventById(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> {
            String message = "Event with id=" + eventId + " was not found";
            return new NotFoundException(message);
        });
    }

    private Comment getCommentById(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> {
            String message = "Comment with id=" + commentId + " was not found";
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

    private void isParentForTheSameEvent(Comment parentComment, Event event) {
        if (parentComment.getEvent().getId() != event.getId()) {
            String message = "Parent comment must belong to the same event";
            throw new RulesViolationException(message);
        }
    }

    private void hasEventCorrectStatus(Event event) {
        if (event.getState() != EventState.PUBLISHED) {
            String message = "Event with id=" + event.getId() + " is not published";
            throw new NotFoundException(message);
        }
    }

}
