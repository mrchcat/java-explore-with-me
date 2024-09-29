package com.github.mrchcat.explorewithme.comments.repository;

import com.github.mrchcat.explorewithme.comments.model.Comment;
import com.github.mrchcat.explorewithme.event.model.EventState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {

    @Query("""
            SELECT cmt
            FROM Comment AS cmt
            JOIN cmt.event AS e
            WHERE cmt.id=:commentId AND e.state=:state
            """)
    Optional<Comment> getByIdAndEventState(long commentId, EventState state);

    @Query("""
            SELECT cmt
            FROM Comment AS cmt
            JOIN cmt.event AS e
            WHERE e.id=:eventId AND e.state='PUBLISHED' AND cmt.state='ENABLE'
            """)
    List<Comment> findEnableForPublishedEvent(long eventId, Pageable pageable);
}
