package com.github.mrchcat.explorewithme.comments.repository;

import com.github.mrchcat.explorewithme.comments.model.Comment;
import com.github.mrchcat.explorewithme.event.model.EventState;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {

    @Query("""
            SELECT CASE WHEN COUNT(cmt)>0 THEN TRUE ELSE FALSE END
            FROM Comment AS cmt
            WHERE cmt.author.id=:authorId AND cmt.event.id=:eventId
            """)
    boolean existByEventAndAuthor(long eventId, long authorId);

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
            WHERE cmt.event.id=:eventId AND cmt.state='ALIVE'
            """)
    @EntityGraph(attributePaths = {"event", "author"})
    List<Comment> findAliveByEvent(long eventId, Pageable pageable);
}
