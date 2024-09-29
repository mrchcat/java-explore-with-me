package com.github.mrchcat.explorewithme.comments.service;

import com.github.mrchcat.explorewithme.comments.dto.CommentAdminSearchDto;
import com.github.mrchcat.explorewithme.comments.dto.CommentAdminUpdateDto;
import com.github.mrchcat.explorewithme.comments.dto.CommentDto;
import com.github.mrchcat.explorewithme.comments.dto.CommentPrivateCreateDto;
import com.github.mrchcat.explorewithme.comments.mapper.CommentMapper;
import com.github.mrchcat.explorewithme.comments.model.Comment;
import com.github.mrchcat.explorewithme.comments.model.CommentState;
import com.github.mrchcat.explorewithme.comments.model.QComment;
import com.github.mrchcat.explorewithme.comments.repository.CommentRepository;
import com.github.mrchcat.explorewithme.event.model.Event;
import com.github.mrchcat.explorewithme.event.repository.EventRepository;
import com.github.mrchcat.explorewithme.exception.NotFoundException;
import com.github.mrchcat.explorewithme.exception.RulesViolationException;
import com.github.mrchcat.explorewithme.user.model.User;
import com.github.mrchcat.explorewithme.user.repository.UserRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.github.mrchcat.explorewithme.comments.model.CommentState.DISABLE;
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
    public void setDeadState(long userId, long commentId) {
        Comment comment = getCommentForPublishedEvent(commentId);
        canUserEditComment(comment, userId);
        comment.setState(DISABLE);
        comment.setEditable(false);
        commentRepository.save(comment);
        log.info("Comment status {} was set to DEAD", comment);
    }

    @Override
    @Transactional
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

//    @Override
//    public List<CommentDto> getAllForAdmin(CommentAdminSearchDto query) {
//        var comments = commentRepository.getAllCommentsByQuery(query);
//        return CommentMapper.toDto(comments);
//    }

    @Override
    public List<CommentDto> getAllForAdmin(CommentAdminSearchDto qp) {
        BooleanBuilder builder = new BooleanBuilder();
        List<Long> commentIds = qp.getCommentId();
        if (commentIds != null && !commentIds.isEmpty()) {
            Predicate inCommentList = QComment.comment.id.in(commentIds);
            builder.and(inCommentList);
        }
        CommentState commentState = qp.getCommentState();
        if (commentState != null) {
            Predicate isCommentState = QComment.comment.state.eq(commentState);
            builder.and(isCommentState);
        }
        List<Long> eventIds = qp.getEventId();
        if (eventIds != null && !eventIds.isEmpty()) {
            Predicate inEventList = QComment.comment.event.id.in(eventIds);
            builder.and(inEventList);
        }

        List<Long> userIds = qp.getUserId();
        if (userIds != null && !userIds.isEmpty()) {
            Predicate inUserList = QComment.comment.author.id.in(userIds);
            builder.and(inUserList);
        }

        Boolean editable = qp.getEditable();
        if (editable != null) {
            Predicate isEditable = QComment.comment.editable.eq(editable);
            builder.and(isEditable);
        }

        String text = qp.getText();
        if (text != null && !text.isEmpty()) {
            Predicate textSearch = QComment.comment.text.likeIgnoreCase("%" + text.trim() + "%");
            builder.and(textSearch);
        }

        var start = qp.getStart();
        if (start != null) {
            Predicate greaterThen = QComment.comment.lastModification.after(start);
            Predicate equal = QComment.comment.lastModification.eq(start);
            builder.andAnyOf(equal,greaterThen);
        }
        var end = qp.getEnd();
        if (end != null) {
            Predicate lessThen = QComment.comment.lastModification.before(end);
            Predicate equal = QComment.comment.lastModification.eq(end);
            builder.andAnyOf(equal,lessThen);
        }
        Iterable<Comment> comments;
        if (builder.hasValue()) {
            comments = commentRepository.findAll(builder.getValue(), qp.getPageable());
        } else {
            comments = commentRepository.findAll(qp.getPageable());
        }
        return CommentMapper.toDto(comments);
    }


    @Override
    public List<CommentDto> getAllForPublic(long eventId, Pageable pageable) {
        var comments = commentRepository.findEnableForPublishedEvent(eventId, pageable);
        return CommentMapper.toDto(comments);
    }

    @Override
    @Transactional
    public CommentDto updateByAdmin(long commentId, CommentAdminUpdateDto updateDto) {
        Comment comment = getComment(commentId);
        CommentState newState = updateDto.getState();
        Boolean isEditable = updateDto.getEditable();
        if (newState == null) {
            comment.setEditable(isEditable);
        } else {
            comment.setState(newState);
            if (newState.equals(DISABLE)) {
                comment.setEditable(false);
            } else {
                comment.setEditable(isEditable);
            }
        }
        Comment updatedComment = commentRepository.save(comment);
        log.info("Comment {} was updated by admin", updatedComment);
        return CommentMapper.toDto(updatedComment);
    }

    @Override
    public void delete(long commentId) {
        commentRepository.deleteById(commentId);
        log.info("Comment id={} was deleted", commentId);
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
            String message = "Comment with id=" + commentId + " was not found";
            return new NotFoundException(message);
        });
    }

    private void canUserCommentThisEvent(Event event, User author) {
        if (event.getInitiator().getId() == author.getId()) {
            String message = "User can not comment it's own event";
            throw new RulesViolationException(message);
        }
    }

    private void canUserEditComment(Comment comment, long userId) {
        boolean isCommentAlive = comment.getState().equals(CommentState.ENABLE);
        boolean isCommentEditable = comment.isEditable();
        boolean doesCommentBelongToUser = comment.getAuthor().getId() == userId;
        if (!isCommentAlive || !isCommentEditable || !doesCommentBelongToUser) {
            String message = "Comment id=" + comment.getId() + " can not be changed due to rule violation";
            throw new RulesViolationException(message);
        }
    }
}
