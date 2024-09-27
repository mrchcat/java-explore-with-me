package com.github.mrchcat.explorewithme.comments.repository;

import com.github.mrchcat.explorewithme.comments.model.Comment;
import com.github.mrchcat.explorewithme.event.model.EventState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {

    @Query("""
            SELECT CASE WHEN COUNT(cmt)>0 THEN TRUE ELSE FALSE END
            FROM Comment AS cmt
            WHERE cmt.author.id=:author_id AND cmt.event.id=:eventId
            """)
    boolean existByEventAndAuthor(long eventId, long author_id);

    @Query("""
            SELECT cmt
            FROM Comment AS cmt
            JOIN cmt.event AS e
            WHERE cmt.id=:commentId AND e.state=:state
            """)
    Optional<Comment> getByIdAndEventState(long commentId, EventState state);
}
