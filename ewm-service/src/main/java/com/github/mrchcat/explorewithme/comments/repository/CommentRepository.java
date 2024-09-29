package com.github.mrchcat.explorewithme.comments.repository;

import com.github.mrchcat.explorewithme.comments.model.Comment;
import com.github.mrchcat.explorewithme.event.model.EventState;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {

    @Query("""
            SELECT cmt
            FROM Comment AS cmt
            JOIN cmt.event AS e
            WHERE cmt.id=:commentId AND e.state=:state
            """)
    @EntityGraph(attributePaths = {"author"})
    Optional<Comment> getByIdAndEventState(long commentId, EventState state);

    @Query("""
            SELECT cmt
            FROM Comment AS cmt
            JOIN cmt.event AS e
            WHERE e.id=:eventId AND e.state='PUBLISHED' AND cmt.state='ENABLE'
            """)
    @EntityGraph(attributePaths = {"author"})
    List<Comment> findEnableForPublishedEvent(long eventId, Pageable pageable);

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"author"})
    Page<Comment> findAll(@NonNull Predicate predicate, @NonNull Pageable pageable);

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"author"})
    Page<Comment> findAll(@NonNull Pageable pageable);
}
